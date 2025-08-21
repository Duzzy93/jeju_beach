import os
import time
import json
import sys
import base64
import cv2
import numpy as np
from ultralytics import YOLO
from deep_sort_realtime.deepsort_tracker import DeepSort

class BeachCrowdAnalyzer:
    def __init__(self, video_path, beach_name):
        self.video_path = video_path
        self.beach_name = beach_name
        
        # 환경변수 기반 설정
        self.yolo_weights = os.getenv("YOLO_WEIGHTS", "yolov8n.pt")
        self.yolo_conf = float(os.getenv("YOLO_CONF", "0.35"))
        self.yolo_iou = float(os.getenv("YOLO_IOU", "0.5"))
        self.yolo_imgsz = int(os.getenv("YOLO_IMGSZ", "960"))
        self.frame_stride = int(os.getenv("FRAME_STRIDE", "1"))
        
        # 쓰러짐 감지 설정
        self.fall_ratio = float(os.getenv("FALL_RATIO", "1.8"))
        self.fall_max_height_ratio = float(os.getenv("FALL_MAX_HEIGHT_RATIO", "0.35"))
        self.fall_cooldown_sec = float(os.getenv("FALL_COOLDOWN_SEC", "5.0"))
        
        # DeepSORT 설정
        self.deepsort_cfg = dict(
            max_age=30,
            n_init=3,
            max_iou_distance=0.7,
            max_cosine_distance=0.2,
            nn_budget=None,
            embedder="mobilenet",
            half=True
        )
        
        # 모델 및 추적기 초기화
        self.model = YOLO(self.yolo_weights)
        self.tracker = DeepSort(**self.deepsort_cfg)
        
        # 상태 관리
        self.unique_ids = set()
        self.last_fall_alert = {}
        self.stats = {
            "unique_person_count": 0,
            "last_visible_count": 0,
            "last_fallen_visible": 0,
            "total_fall_alerts": 0
        }
    
    def compute_aspect_fall(self, l, t, r, b, frame_h):
        """LTRB 박스 기준으로 aspect ratio와 쓰러짐 여부 계산"""
        w = max(1, r - l)
        h = max(1, b - t)
        aspect = w / h
        fallen = (aspect >= self.fall_ratio) and ((h / max(1, frame_h)) <= self.fall_max_height_ratio)
        return aspect, fallen
    
    def draw_box_with_label(self, frame, l, t, r, b, label, fallen=False):
        """박스와 라벨을 프레임에 그리기"""
        color = (0, 0, 255) if fallen else (0, 255, 0)  # 쓰러짐: 빨강, 정상: 초록
        cv2.rectangle(frame, (l, t), (r, b), color, 2)
        cv2.putText(frame, label, (l, max(0, t - 8)), cv2.FONT_HERSHEY_SIMPLEX, 0.6, color, 2)
    
    def get_density_level(self, count):
        """사람 수에 따른 혼잡도 레벨 반환"""
        if count < 5:
            return "낮음"
        elif count < 15:
            return "중간"
        else:
            return "높음"
    
    def frame_to_base64(self, frame):
        """프레임을 base64로 인코딩"""
        _, buffer = cv2.imencode('.jpg', frame, [cv2.IMWRITE_JPEG_QUALITY, 80])
        jpg_as_text = base64.b64encode(buffer).decode('utf-8')
        return jpg_as_text
    
    def process_video(self):
        """비디오를 처리하고 분석 결과를 반환"""
        cap = cv2.VideoCapture(self.video_path)
        
        if not cap.isOpened():
            print(f"Error: Could not open video file {self.video_path}")
            return None
        
        frame_index = 0
        analyzed_frame = None
        
        # 첫 번째 프레임부터 분석 시작
        while True:
            ret, frame = cap.read()
            if not ret:
                break
            
            if (frame_index % self.frame_stride) != 0:
                frame_index += 1
                continue
            
            H, W = frame.shape[:2]
            
            # 1) YOLO 사람 탐지
            results = self.model(frame, classes=[0], imgsz=self.yolo_imgsz, 
                               conf=self.yolo_conf, iou=self.yolo_iou, verbose=False)
            dets_xywh_conf_cls = []
            
            if len(results):
                r0 = results[0]
                for box in r0.boxes:
                    x1, y1, x2, y2 = map(int, box.xyxy[0])
                    conf = float(box.conf[0]) if hasattr(box, "conf") else 0.0
                    w = x2 - x1
                    h = y2 - y1
                    dets_xywh_conf_cls.append([[x1, y1, w, h], conf, 0])
            
            # 2) DeepSORT 추적
            tracks = self.tracker.update_tracks(dets_xywh_conf_cls, frame=frame)
            
            visible_count = 0
            fallen_visible = 0
            
            for trk in tracks:
                if not trk.is_confirmed():
                    continue
                
                l, t, r, b = map(int, trk.to_ltrb())
                track_id = trk.track_id
                
                # 고유 ID 카운트(최초 1회만)
                if track_id not in self.unique_ids:
                    self.unique_ids.add(track_id)
                    self.stats["unique_person_count"] = len(self.unique_ids)
                
                visible_count += 1
                
                # 3) 쓰러짐 판정
                aspect, fallen = self.compute_aspect_fall(l, t, r, b, H)
                if fallen:
                    fallen_visible += 1
                    now = time.time()
                    if (track_id not in self.last_fall_alert) or \
                       (now - self.last_fall_alert[track_id] >= self.fall_cooldown_sec):
                        self.last_fall_alert[track_id] = now
                        self.stats["total_fall_alerts"] += 1
                
                # 4) 시각화
                label = f"ID:{track_id}"
                if fallen:
                    label += " FALL"
                self.draw_box_with_label(frame, l, t, r, b, label, fallen=fallen)
            
            self.stats["last_visible_count"] = visible_count
            self.stats["last_fallen_visible"] = fallen_visible
            
            # 우상단 요약 표시
            info = f"Visible: {visible_count} | Unique: {self.stats['unique_person_count']} | Fallen: {fallen_visible}"
            cv2.putText(frame, info, (20, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255,255,255), 2)
            
            # 혼잡도 정보 표시
            density_level = self.get_density_level(visible_count)
            density_color = (0, 255, 0) if density_level == "낮음" else \
                           (0, 255, 255) if density_level == "중간" else (0, 0, 255)
            
            cv2.putText(frame, f"Density: {density_level}", (20, 70), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.8, density_color, 2)
            
            analyzed_frame = frame.copy()
            break  # 첫 번째 프레임만 분석
        
        cap.release()
        
        if analyzed_frame is None:
            return None
        
        # base64로 인코딩
        frame_base64 = self.frame_to_base64(analyzed_frame)
        
        # 결과 반환
        result = {
            "beach_name": self.beach_name,
            "person_count": self.stats["last_visible_count"],
            "unique_person_count": self.stats["unique_person_count"],
            "fallen_count": self.stats["last_fallen_visible"],
            "density_level": self.get_density_level(self.stats["last_visible_count"]),
            "frame_base64": frame_base64,
            "timestamp": time.time(),
            "stats": self.stats
        }
        
        return result

def main():
    if len(sys.argv) != 3:
        print("Usage: python beach_crowd_analyzer.py <video_path> <beach_name>")
        sys.exit(1)
    
    video_path = sys.argv[1]
    beach_name = sys.argv[2]
    
    # 해변 이름을 한글로 변환
    beach_names = {
        "hamduck": "함덕해변",
        "iho": "이호해변",
        "walljeonglee": "월정리해변"
    }
    
    display_name = beach_names.get(beach_name, beach_name)
    
    # 동영상 파일 존재 여부 확인
    if not os.path.exists(video_path):
        print(f"Error: Video file not found: {video_path}")
        print("Please check the video path. Expected location: backend/src/main/resources/static/videos/")
        sys.exit(1)
    
    # 분석기 생성 및 실행
    analyzer = BeachCrowdAnalyzer(video_path, display_name)
    result = analyzer.process_video()
    
    if result:
        # JSON 형태로 출력 (WebSocket으로 전송할 수 있도록)
        print(json.dumps(result, ensure_ascii=False))
    else:
        print("Error: Failed to analyze video")

if __name__ == "__main__":
    main()
