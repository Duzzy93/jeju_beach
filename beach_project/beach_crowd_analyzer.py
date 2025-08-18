import cv2
import numpy as np
import base64
import json
import time
import sys
import os
from ultralytics import YOLO
from PIL import ImageFont, ImageDraw, Image

class BeachCrowdAnalyzer:
    def __init__(self, video_path, beach_name):
        self.video_path = video_path
        self.beach_name = beach_name
        self.model = YOLO("yolov8n.pt")
        
        # 혼잡도 기준 (사람 수)
        self.density_thresholds = [5, 15]  # 0~4: 낮음, 5~14: 중간, 15+ : 높음
        
        # 혼잡도 레벨 색상 (BGR)
        self.color_map = {
            "낮음": (0, 255, 0),    # 초록
            "중간": (0, 255, 255),  # 노랑
            "높음": (0, 0, 255)     # 빨강
        }
        
        # 한글 폰트 설정
        try:
            # Windows 환경
            font_path = "C:/Windows/Fonts/malgun.ttf"
            if os.path.exists(font_path):
                self.font = ImageFont.truetype(font_path, 30)
            else:
                # Linux/Mac 환경
                self.font = ImageFont.load_default()
        except:
            self.font = ImageFont.load_default()
    
    def get_density_level(self, count):
        """사람 수에 따른 혼잡도 레벨 반환"""
        if count < self.density_thresholds[0]:
            return "낮음"
        elif count < self.density_thresholds[1]:
            return "중간"
        else:
            return "높음"
    
    def analyze_frame(self, frame):
        """프레임에서 사람 수를 분석하고 혼잡도 정보를 추가"""
        # YOLO 객체 탐지 (사람 클래스만 추출)
        results = self.model(frame, classes=[0], imgsz=1280, conf=0.2, iou=0.3)
        
        # 탐지된 박스 개수 = 사람 수
        person_count = 0
        for r in results:
            for box in r.boxes:
                person_count += 1
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                cv2.rectangle(frame, (x1, y1), (x2, y2), (255, 0, 0), 2)
        
        # 혼잡도 계산
        density_level = self.get_density_level(person_count)
        density_color = self.color_map[density_level]
        
        # OpenCV 이미지를 PIL 이미지로 변환 (BGR->RGB)
        frame_pil = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
        draw = ImageDraw.Draw(frame_pil)
        
        # PIL로 한글 텍스트 그리기
        try:
            draw.text((20, 40), f"인원 수: {person_count}", font=self.font, fill=(255, 255, 255))
            draw.text((20, 80), f"혼잡도: {density_level}", font=self.font, fill=density_color[::-1])
        except:
            # 폰트 로드 실패 시 기본 텍스트
            cv2.putText(frame, f"People: {person_count}", (20, 40), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
            cv2.putText(frame, f"Density: {density_level}", (20, 80), cv2.FONT_HERSHEY_SIMPLEX, 1, density_color, 2)
        
        # PIL 이미지를 다시 OpenCV 이미지로 변환 (RGB->BGR)
        try:
            frame = cv2.cvtColor(np.array(frame_pil), cv2.COLOR_RGB2BGR)
        except:
            pass
        
        return frame, person_count, density_level
    
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
        
        # 첫 번째 프레임만 분석 (실시간 스트리밍 시뮬레이션)
        ret, frame = cap.read()
        if not ret:
            cap.release()
            return None
        
        # 프레임 분석
        analyzed_frame, person_count, density_level = self.analyze_frame(frame)
        
        # base64로 인코딩
        frame_base64 = self.frame_to_base64(analyzed_frame)
        
        cap.release()
        
        # 결과 반환
        result = {
            "beach_name": self.beach_name,
            "person_count": person_count,
            "density_level": density_level,
            "frame_base64": frame_base64,
            "timestamp": time.time()
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
