#!/usr/bin/env python3
"""
Windows 환경용 해변 혼잡도 분석 테스트 스크립트
인코딩 문제를 방지하고 안전하게 실행됩니다.
"""

import os
import time
import json
import random
import requests
from datetime import datetime

# Windows 환경에서 인코딩 문제 방지
import sys
if sys.platform.startswith('win'):
    import codecs
    sys.stdout = codecs.getwriter('utf-8')(sys.stdout.detach())
    sys.stderr = codecs.getwriter('utf-8')(sys.stderr.detach())

class SimpleBeachDetector:
    def __init__(self):
        self.backend_url = os.getenv("BACKEND_URL", "http://localhost:8080")
        self.analysis_interval = int(os.getenv("ANALYSIS_INTERVAL", "30"))
        
        # 해변별 설정
        self.beaches = [
            {"name": "Hamduck Beach", "source": "hamduck_camera_01"},
            {"name": "Iho Beach", "source": "iho_camera_01"},
            {"name": "Walljeonglee Beach", "source": "walljeonglee_camera_01"}
        ]
        
        self.running = False
        self.analysis_count = 0
        
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
                timeout=5
            )
            
            if response.status_code == 200:
                print(f"[SUCCESS] {beach_info['name']} data sent successfully")
                print(f"   - People: {person_count}, Fallen: {fallen_count}")
            else:
                print(f"[ERROR] {beach_info['name']} data send failed: {response.status_code}")
                
        except Exception as e:
            print(f"[ERROR] API send error ({beach_info['name']}): {str(e)}")
    
    def generate_simulation_data(self, beach_info):
        """시뮬레이션 데이터 생성"""
        # 시간대별로 다른 혼잡도 생성
        hour = datetime.now().hour
        
        if 6 <= hour <= 9:  # 아침
            base_count = random.randint(5, 15)
        elif 10 <= hour <= 16:  # 낮
            base_count = random.randint(20, 50)
        elif 17 <= hour <= 20:  # 저녁
            base_count = random.randint(15, 35)
        else:  # 밤
            base_count = random.randint(0, 10)
        
        # 해변별 특성 반영
        if "Hamduck" in beach_info["name"]:
            base_count = int(base_count * 1.2)  # Hamduck Beach is more crowded
        elif "Walljeonglee" in beach_info["name"]:
            base_count = int(base_count * 0.8)  # Walljeonglee Beach is relatively quiet
        
        # 랜덤 변동 추가
        person_count = max(0, base_count + random.randint(-5, 5))
        fallen_count = random.randint(0, min(3, person_count // 20))  # 인원 대비 쓰러짐 비율
        
        return person_count, fallen_count
    
    def analyze_all_beaches(self):
        """모든 해변 분석"""
        print(f"\n[ANALYSIS] Beach analysis started - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        for beach_info in self.beaches:
            try:
                # 시뮬레이션 데이터 생성
                person_count, fallen_count = self.generate_simulation_data(beach_info)
                
                # 백엔드로 데이터 전송
                self.send_detection_data(beach_info, person_count, fallen_count)
                
                # 분석 횟수 증가
                self.analysis_count += 1
                
                # 잠시 대기 (API 부하 방지)
                time.sleep(1)
                
            except Exception as e:
                print(f"[ERROR] {beach_info['name']} analysis error: {str(e)}")
        
        print(f"[SUCCESS] All beach analysis completed (Total: {self.analysis_count})")
    
    def start_continuous_analysis(self):
        """연속 분석 시작"""
        print("=== Jeju Beach Simple Crowd Analysis Started ===")
        print("=" * 50)
        print(f"[INFO] Backend URL: {self.backend_url}")
        print(f"[INFO] Analysis Interval: {self.analysis_interval} seconds")
        print(f"[INFO] Working Directory: {os.getcwd()}")
        print("=" * 50)
        
        self.running = True
        
        try:
            while self.running:
                # 모든 해변 분석
                self.analyze_all_beaches()
                
                # 다음 분석까지 대기
                print(f"[WAIT] Waiting {self.analysis_interval} seconds for next analysis...")
                time.sleep(self.analysis_interval)
                
        except KeyboardInterrupt:
            print("\n[STOP] Stopped by user")
        except Exception as e:
            print(f"[ERROR] Analysis error: {str(e)}")
        finally:
            self.running = False
            print("[SUCCESS] Analyzer stopped")
    
    def stop(self):
        """분석 중지"""
        self.running = False

def main():
    """메인 함수"""
    try:
        # 간단한 탐지기 생성 및 시작
        detector = SimpleBeachDetector()
        detector.start_continuous_analysis()
        
    except Exception as e:
        print(f"[ERROR] Program execution error: {str(e)}")
        print("[INFO] Troubleshooting:")
        print("   1. Check Python environment: python --version")
        print("   2. Install required packages: pip install requests")
        print("   3. Check backend server status")
        print("   4. Check network connection")

if __name__ == "__main__":
    main()
