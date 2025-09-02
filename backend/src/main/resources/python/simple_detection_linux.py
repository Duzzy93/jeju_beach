#!/usr/bin/env python3
"""
Linux 환경용 해변 혼잡도 분석 스크립트
YOLO + DeepSORT를 사용하여 실제 비디오에서 사람과 쓰러진 사람을 탐지합니다.
"""

import os
import time
import json
import requests
import cv2
import numpy as np
from datetime import datetime
from ultralytics import YOLO
from deep_sort_realtime.deepsort_tracker import DeepSort

class RealBeachDetector:
    def __init__(self):
        self.backend_url = os.getenv("BACKEND_URL", "http://localhost:8080")
        self.analysis_interval = int(os.getenv("ANALYSIS_INTERVAL", "30"))
        
        # 현재 작업 디렉토리에서 비디오 파일 경로 설정 (임시 디렉토리)
        current_dir = os.getcwd()
        
        # 해변별 비디오 파일 설정
        self.beaches = [
            {
                "name": "Hamduck Beach", 
                "source": "hamduck_camera_01",
                "video_path": os.path.join(current_dir, "hamduck_beach.mp4")
            },
            {
                "name": "Iho Beach", 
                "source": "iho_camera_01",
                "video_path": os.path.join(current_dir, "iho_beach.mp4")
            },
            {
                "name": "Walljeonglee Beach", 
                "source": "walljeonglee_camera_01",
                "video_path": os.path.join(current_dir, "walljeonglee_beach.mp4")
            }
        ]
        
        # YOLO 설정 - 현재 디렉토리에서 yolov8n.pt 찾기
        self.yolo_weights = os.getenv("YOLO_WEIGHTS", os.path.join(current_dir, "yolov8n.pt"))
        self.yolo_conf = float(os.getenv("YOLO_CONF", "0.35"))
        self.yolo_iou = float(os.getenv("YOLO_IOU", "0.5"))
        self.yolo_imgsz = int(os.getenv("YOLO_IMGSZ", "640"))
        
        # 쓰러짐 탐지 설정
        self.fall_ratio = float(os.getenv("FALL_RATIO", "1.8"))
        self.fall_max_height_ratio = float(os.getenv("FALL_MAX_HEIGHT_RATIO", "0.35"))
        
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
        self.model = None
        self.tracker = None
        self.initialized = False
        
        self.running = False
        self.analysis_count = 0
        
    def initialize_models(self):
        """YOLO 모델과 DeepSORT 추적기 초기화"""
        try:
            print("[INFO] YOLO 모델 로딩 중...")
            if not os.path.exists(self.yolo_weights):
                print(f"[WARNING] YOLO 가중치 파일을 찾을 수 없습니다: {self.yolo_weights}")
                print("[INFO] 자동으로 다운로드를 시도합니다...")
                self.model = YOLO("yolov8n.pt")  # 자동 다운로드
            else:
                self.model = YOLO(self.yolo_weights)
            
            print("[INFO] DeepSORT 추적기 초기화 중...")
            self.tracker = DeepSort(**self.deepsort_cfg)
            
            self.initialized = True
            print("[SUCCESS] 모델 및 추적기 초기화 완료")
            
        except Exception as e:
            print(f"[ERROR] 모델 초기화 실패: {str(e)}")
            self.initialized = False
    
    def compute_aspect_fall(self, l, t, r, b, frame_h):
        """LTRB 박스 기준으로 aspect ratio와 쓰러짐 여부 계산"""
        w = max(1, r - l)
        h = max(1, b - t)
        aspect = w / h
        fallen = (aspect >= self.fall_ratio) and ((h / max(1, frame_h)) <= self.fall_max_height_ratio)
        return aspect, fallen
    
    def analyze_video(self, beach_info):
        """비디오 파일을 분석하여 사람 수와 쓰러진 사람 수를 탐지"""
        if not self.initialized:
            print(f"[ERROR] {beach_info['name']}: 모델이 초기화되지 않았습니다.")
            return 0, 0
        
        video_path = beach_info["video_path"]
        if not os.path.exists(video_path):
            print(f"[ERROR] {beach_info['name']}: 비디오 파일을 찾을 수 없습니다: {video_path}")
            return 0, 0
        
        try:
            cap = cv2.VideoCapture(video_path)
            if not cap.isOpened():
                print(f"[ERROR] {beach_info['name']}: 비디오를 열 수 없습니다: {video_path}")
                return 0, 0
            
            # 비디오 정보 확인
            total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
            fps = cap.get(cv2.CAP_PROP_FPS)
            print(f"[INFO] {beach_info['name']}: 총 {total_frames}프레임, {fps:.1f} FPS")
            
            unique_ids = set()
            last_fall_alert = dict()
            frame_index = 0
            frame_stride = max(1, total_frames // 100)  # 성능을 위해 100프레임 정도만 처리
            
            total_visible = 0
            total_fallen = 0
            processed_frames = 0
            
            while True:
                ret, frame = cap.read()
                if not ret:
                    break
                
                if (frame_index % frame_stride) != 0:
                    frame_index += 1
                    continue
                
                H, W = frame.shape[:2]
                
                # YOLO 사람 탐지
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
                
                # DeepSORT 추적
                tracks = self.tracker.update_tracks(dets_xywh_conf_cls, frame=frame)
                
                visible_count = 0
                fallen_visible = 0
                
                for trk in tracks:
                    if not trk.is_confirmed():
                        continue
                    
                    l, t, r, b = map(int, trk.to_ltrb())
                    track_id = trk.track_id
                    
                    if track_id not in unique_ids:
                        unique_ids.add(track_id)
                    
                    visible_count += 1
                    
                    # 쓰러짐 판정
                    aspect, fallen = self.compute_aspect_fall(l, t, r, b, H)
                    if fallen:
                        fallen_visible += 1
                        now = time.time()
                        if (track_id not in last_fall_alert) or (now - last_fall_alert[track_id] >= 5.0):
                            last_fall_alert[track_id] = now
                
                total_visible += visible_count
                total_fallen += fallen_visible
                processed_frames += 1
                frame_index += 1
                
                # 진행률 표시
                if processed_frames % 10 == 0:
                    progress = (frame_index / total_frames) * 100
                    print(f"[INFO] {beach_info['name']}: 진행률 {progress:.1f}% ({processed_frames}프레임 처리)")
                
                # 성능을 위해 최대 100프레임까지만 처리
                if processed_frames >= 100:
                    break
            
            cap.release()
            
            # 평균값 계산
            avg_visible = total_visible // max(1, processed_frames)
            avg_fallen = total_fallen // max(1, processed_frames)
            
            print(f"[INFO] {beach_info['name']}: 평균 {avg_visible}명, 쓰러진 사람 {avg_fallen}명")
            return avg_visible, avg_fallen
            
        except Exception as e:
            print(f"[ERROR] {beach_info['name']} 비디오 분석 오류: {str(e)}")
            return 0, 0
    
    def send_detection_data(self, beach_info, person_count, fallen_count):
        """탐지 데이터를 백엔드 API로 전송"""
        try:
            payload = {
                "personCount": person_count,
                "fallenCount": fallen_count,
                "source": beach_info["source"]
            }
            
            headers = {"Content-Type": "application/json"}
            
            response = requests.post(
                f"{self.backend_url}/api/detections",
                json=payload,
                headers=headers,
                timeout=10
            )
            
            if response.status_code == 200:
                print(f"[SUCCESS] {beach_info['name']} 데이터 전송 성공")
                print(f"   - 사람 수: {person_count}, 쓰러진 사람: {fallen_count}")
            else:
                print(f"[ERROR] {beach_info['name']} 데이터 전송 실패: {response.status_code}")
                
        except Exception as e:
            print(f"[ERROR] API 전송 오류 ({beach_info['name']}): {str(e)}")
    
    def analyze_all_beaches(self):
        """모든 해변 분석"""
        print(f"\n[ANALYSIS] 해변 분석 시작 - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        for beach_info in self.beaches:
            try:
                print(f"[INFO] {beach_info['name']} 분석 중...")
                
                # 실제 비디오 분석
                person_count, fallen_count = self.analyze_video(beach_info)
                
                # 백엔드로 데이터 전송
                self.send_detection_data(beach_info, person_count, fallen_count)
                
                # 분석 횟수 증가
                self.analysis_count += 1
                
                # API 부하 방지를 위한 대기
                time.sleep(2)
                
            except Exception as e:
                print(f"[ERROR] {beach_info['name']} 분석 오류: {str(e)}")
        
        print(f"[SUCCESS] 모든 해변 분석 완료 (총 {self.analysis_count}개)")
    
    def start_continuous_analysis(self):
        """연속 분석 시작"""
        print("=== Jeju Beach 실제 탐지 기반 혼잡도 분석 시작 ===")
        print("=" * 60)
        print(f"[INFO] 백엔드 URL: {self.backend_url}")
        print(f"[INFO] 분석 간격: {self.analysis_interval}초")
        print(f"[INFO] 작업 디렉토리: {os.getcwd()}")
        print("=" * 60)
        
        # 모델 초기화
        self.initialize_models()
        if not self.initialized:
            print("[ERROR] 모델 초기화 실패로 프로그램을 종료합니다.")
            return
        
        self.running = True
        
        try:
            while self.running:
                # 모든 해변 분석
                self.analyze_all_beaches()
                
                # 다음 분석까지 대기
                print(f"[WAIT] 다음 분석까지 {self.analysis_interval}초 대기 중...")
                time.sleep(self.analysis_interval)
                
        except KeyboardInterrupt:
            print("\n[STOP] 사용자에 의해 중지됨")
        except Exception as e:
            print(f"[ERROR] 분석 오류: {str(e)}")
        finally:
            self.running = False
            print("[SUCCESS] 분석기 중지됨")
    
    def stop(self):
        """분석 중지"""
        self.running = False

def main():
    """메인 함수"""
    try:
        # 실제 탐지기 생성 및 시작
        detector = RealBeachDetector()
        detector.start_continuous_analysis()
        
    except Exception as e:
        print(f"[ERROR] 프로그램 실행 오류: {str(e)}")
        print("[INFO] 문제 해결 방법:")
        print("   1. Python 환경 확인: python3 --version")
        print("   2. 필요한 패키지 설치: pip3 install -r requirements.txt")
        print("   3. 백엔드 서버 상태 확인")
        print("   4. 네트워크 연결 확인")
        print("   5. 비디오 파일 경로 확인")
        print("   6. YOLO 가중치 파일 확인")

if __name__ == "__main__":
    main()
