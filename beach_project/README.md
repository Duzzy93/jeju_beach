# 제주 해변 AI 탐지 모델

이 프로젝트는 YOLO + DeepSORT를 사용하여 해변의 혼잡도와 쓰러진 사람을 실시간으로 탐지하는 AI 모델입니다.

## 🚀 주요 기능

- **실시간 사람 탐지**: YOLO v8을 사용한 정확한 사람 탐지
- **중복 카운트 방지**: DeepSORT를 통한 개별 ID 추적
- **쓰러진 사람 탐지**: 박스 비율 분석을 통한 쓰러짐 판정
- **해변별 분석**: 함덕, 이호, 월정리 해변의 개별 분석
- **백엔드 연동**: Spring Boot 백엔드로 탐지 데이터 자동 전송

## 📋 요구사항

- Python 3.8+
- CUDA 지원 GPU (선택사항, CPU에서도 동작)
- Spring Boot 백엔드 서버 실행 중

## 🛠️ 설치

### 1. 의존성 설치

```bash
pip install -r requirements.txt
```

### 2. YOLO 가중치 파일

- `yolov8n.pt` 파일이 `beach_project` 디렉토리에 있어야 합니다
- 자동으로 다운로드되지만, 수동으로 다운로드할 수도 있습니다

### 3. 비디오 파일

- `../backend/videos/` 디렉토리에 해변 비디오 파일들이 있어야 합니다:
  - `hamduck_beach.mp4`
  - `iho_beach.mp4`
  - `walljeonglee_beach.mp4`

## 🎯 사용법

### 수동 실행

```bash
cd beach_project
python simple_detection_windows.py
```

## ⚙️ 환경 변수 설정

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `BACKEND_URL` | `http://localhost:8080` | 백엔드 서버 URL |
| `ANALYSIS_INTERVAL` | `30` | 분석 간격 (초) |
| `YOLO_CONF` | `0.35` | YOLO 신뢰도 임계값 |
| `YOLO_IOU` | `0.5` | YOLO IOU 임계값 |
| `YOLO_IMGSZ` | `640` | YOLO 입력 이미지 크기 |
| `FALL_RATIO` | `1.8` | 쓰러짐 판정 비율 임계값 |
| `FALL_MAX_HEIGHT_RATIO` | `0.35` | 쓰러짐 판정 높이 비율 |

## 🔧 설정 조정

### 성능 최적화

- **CPU 사용 시**: `YOLO_IMGSZ`를 320으로 낮춤
- **GPU 사용 시**: `YOLO_IMGSZ`를 960으로 높임
- **실시간 처리**: `ANALYSIS_INTERVAL`을 10초로 설정

### 정확도 조정

- **높은 정확도**: `YOLO_CONF`를 0.5로 높임
- **낮은 정확도**: `YOLO_CONF`를 0.25로 낮춤
- **쓰러짐 탐지**: `FALL_RATIO`를 1.5로 조정

## 📊 출력 데이터

### 탐지 결과

```json
{
  "personCount": 25,
  "fallenCount": 1,
  "source": "hamduck_camera_01"
}
```

### 로그 예시

```
[INFO] Hamduck Beach 분석 중...
[INFO] Hamduck Beach: 총 150프레임, 30.0 FPS
[INFO] Hamduck Beach: 진행률 10.0% (10프레임 처리)
[INFO] Hamduck Beach: 평균 25명, 쓰러진 사람 1명
[SUCCESS] Hamduck Beach 데이터 전송 성공
   - 사람 수: 25, 쓰러진 사람: 1
```

## 🚨 문제 해결

### 일반적인 오류

1. **모델 초기화 실패**
   - `pip install -r requirements.txt` 실행
   - GPU 드라이버 업데이트 (GPU 사용 시)

2. **비디오 파일을 찾을 수 없음**
   - 비디오 파일 경로 확인
   - 파일 권한 확인

3. **백엔드 연결 실패**
   - Spring Boot 서버 실행 상태 확인
   - 포트 8080 사용 가능 여부 확인

4. **ModuleNotFoundError: No module named 'cv2'**
   - 가상환경이 활성화되어 있는지 확인
   - `set_ai_environment.bat` (Windows) 또는 `set_ai_environment.sh` (Linux/Mac) 실행
   - 또는 환경 변수 `AI_PYTHON_PATH`를 가상환경 Python 경로로 설정

### 성능 문제

1. **느린 처리 속도**
   - `YOLO_IMGSZ`를 320으로 낮춤
   - `FRAME_STRIDE`를 높여서 처리할 프레임 수 줄임

2. **메모리 부족**
   - `YOLO_IMGSZ`를 낮춤
   - `max_age` 값을 낮춤

## 🔗 API 연동

### 탐지 데이터 전송

- **엔드포인트**: `POST /api/detections`
- **데이터 형식**: JSON
- **자동 전송**: 30초마다 각 해변의 분석 결과 전송

### 백엔드 상태 확인

- **엔드포인트**: `GET /api/ai-model/status`
- **용도**: 백엔드 서버 연결 상태 확인

### 실시간 데이터 수신

- **WebSocket**: `ws://localhost:8080/ws/detections`
- **폴링 폴백**: 5초마다 API 호출로 최신 데이터 조회
- **데이터 형식**: 
  ```json
  {
    "type": "DETECTION_UPDATE",
    "beachName": "hamduck",
    "detection": {
      "personCount": 25,
      "fallenCount": 1,
      "source": "hamduck_camera_01"
    }
  }
  ```

## 📈 모니터링

### 실시간 로그

- 진행률 표시
- 탐지 결과 요약
- 오류 및 경고 메시지

### 성능 지표

- 처리된 프레임 수
- 탐지된 사람 수
- 쓰러진 사람 수
- API 전송 상태

## 🤝 기여

버그 리포트나 기능 제안은 이슈로 등록해 주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
