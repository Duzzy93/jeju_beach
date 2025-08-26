# 🏖️ 제주 해변 AI 모델

Spring Boot 백엔드에서 자동으로 실행되는 AI 모델로, 제주 해변의 실시간 혼잡도를 분석하고 탐지 데이터를 백엔드로 전송합니다.

## 🚀 주요 기능

- **실시간 사람 탐지**: YOLOv8을 사용한 고정밀 사람 탐지
- **쓰러짐 감지**: DeepSORT를 활용한 쓰러짐 상황 실시간 감지
- **백엔드 자동 연동**: Spring Boot 백엔드에서 자동 실행 및 데이터 전송
- **Windows 호환성**: Windows 환경에서의 인코딩 문제 해결

## 📋 시스템 요구사항

- Python 3.8+
- CUDA 지원 GPU (선택사항, CPU에서도 동작)
- 최소 8GB RAM
- 웹캠 또는 동영상 파일

## 🛠️ 설치 방법

### 1. 저장소 클론
```bash
git clone <repository-url>
cd beach_project
```

### 2. 가상환경 생성 및 활성화
```bash
# Windows
python -m venv venv
venv\Scripts\activate

# Linux/Mac
python3 -m venv venv
source venv/bin/activate
```

### 3. 필요한 패키지 설치
```bash
pip install -r requirements.txt
```

### 4. YOLOv8 모델 다운로드
```bash
# YOLOv8n 모델이 자동으로 다운로드됩니다
# 또는 수동으로 다운로드:
wget https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov8n.pt
```

## 🔧 환경 설정

### 환경변수 설정

```bash
# Windows
set BACKEND_URL=http://localhost:8080
set ANALYSIS_INTERVAL=30

# Linux/Mac
export BACKEND_URL=http://localhost:8080
export ANALYSIS_INTERVAL=30
```

### 설정 가능한 환경변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `BACKEND_URL` | `http://localhost:8080` | 백엔드 API 서버 URL |
| `ANALYSIS_INTERVAL` | `30` | 해변 분석 간격 (초) |
| `YOLO_WEIGHTS` | `yolov8n.pt` | YOLO 모델 가중치 파일 경로 |
| `YOLO_CONF` | `0.35` | YOLO 탐지 신뢰도 임계값 |
| `YOLO_IOU` | `0.5` | YOLO IoU 임계값 |
| `FALL_RATIO` | `1.8` | 쓰러짐 판정 비율 임계값 |

## 🚀 실행 방법

### 자동 실행 (권장)
Spring Boot 백엔드가 시작되면 자동으로 AI 모델이 실행됩니다.

### 수동 실행
```bash
# Windows 환경에서 실행
python simple_detection_windows.py
```

### 백엔드에서 AI 모델 제어
- **시작**: `POST /api/ai-model/start`
- **중지**: `POST /api/ai-model/stop`
- **재시작**: `POST /api/ai-model/restart`
- **상태 확인**: `GET /api/ai-model/status`

## 📊 출력 데이터 형식

### 탐지 결과 JSON
```json
{
  "beach_name": "함덕해변",
  "person_count": 15,
  "unique_person_count": 23,
  "fallen_count": 0,
  "density_level": "중간",
  "timestamp": 1703123456.789,
  "stats": {
    "unique_person_count": 23,
    "last_visible_count": 15,
    "last_fallen_visible": 0,
    "total_fall_alerts": 0
  }
}
```

### 백엔드 전송 데이터
```json
{
  "personCount": 15,
  "fallenCount": 0,
  "source": "hamduck_camera_01"
}
```

## 🔍 문제 해결

### 1. 백엔드 연결 실패
- 백엔드 서버가 실행 중인지 확인
- `BACKEND_URL` 환경변수 확인
- 방화벽 설정 확인

### 2. 동영상 파일을 찾을 수 없음
- 동영상 파일 경로 확인
- `../backend/videos/` 디렉토리에 동영상 파일 존재 확인

### 3. GPU 메모리 부족
- `YOLO_IMGSZ` 값을 줄이기 (기본값: 960 → 640)
- `YOLO_WEIGHTS`를 `yolov8n.pt`로 변경

### 4. 탐지 정확도 향상
- `YOLO_CONF` 값을 조정 (0.3 ~ 0.7)
- `YOLO_IOU` 값을 조정 (0.3 ~ 0.7)
- `FALL_RATIO` 값을 조정 (1.5 ~ 2.2)

## 📝 로그 파일

실행 중 생성되는 로그 파일:
- `beach_detection.log`: 상세한 분석 로그
- 콘솔 출력: 실시간 상태 정보

## 🔄 백엔드 연동

### API 엔드포인트
- `POST /api/detections`: 탐지 데이터 저장
- `GET /api/detections/latest`: 최신 탐지 데이터 조회
- `GET /api/detections/beach/{beachName}/latest`: 해변별 최신 탐지 데이터

### 데이터 흐름
```
Spring Boot 시작 → AI 모델 자동 실행 → 탐지 데이터 생성 → 
백엔드 API 전송 → 데이터베이스 저장 → 프론트엔드 실시간 업데이트
```

### 파일 구조
```
beach_project/
├── simple_detection_windows.py  # Windows용 AI 모델 (메인)
├── requirements.txt             # Python 의존성
├── yolov8n.pt                  # YOLO 모델 파일
└── README.md                   # 이 파일
```

## 📞 지원

문제가 발생하거나 개선 사항이 있으면 이슈를 등록해 주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
