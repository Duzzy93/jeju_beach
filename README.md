# 제주 해변 혼잡도 시스템

AI 기술을 활용한 제주 해변 실시간 혼잡도 모니터링 시스템입니다.

## 🏖️ 주요 기능

- **실시간 해변 혼잡도 분석**: YOLO + DeepSORT를 사용한 정확한 사람 수 탐지
- **중복 카운트 방지**: DeepSORT 추적으로 고유 방문자 수 계산
- **쓰러짐 감지**: AI 기반 이상 상황 자동 감지 및 알림
- **WebSocket 실시간 통신**: 실시간 데이터 업데이트
- **3개 해변 모니터링**: 함덕해변, 이호해변, 월정리해변
- **반응형 웹 인터페이스**: Vue.js와 Bootstrap을 활용한 모던한 UI

## 🚀 기술 스택

### 백엔드
- **Spring Boot 3.4.8**: Java 21 기반
- **WebSocket**: 실시간 통신
- **Gradle**: 빌드 도구

### 프론트엔드
- **Vue.js 3**: 프론트엔드 프레임워크
- **Bootstrap 5**: UI 컴포넌트
- **SockJS + STOMP**: WebSocket 클라이언트

### AI/ML
- **YOLOv8**: 객체 탐지 모델
- **DeepSORT**: 실시간 객체 추적
- **OpenCV**: 이미지 처리
- **Python**: AI 분석 스크립트

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   ├── config/         # WebSocket 설정
│   │   ├── controller/     # 컨트롤러
│   │   ├── service/        # 비즈니스 로직
│   │   └── dto/           # 데이터 전송 객체
│   └── build.gradle
├── frontend/               # Vue.js 프론트엔드
│   ├── src/components/     # Vue 컴포넌트
│   │   ├── BeachCrowdPage.vue      # 해변 혼잡도 메인
│   │   ├── BeachDetailPage.vue     # 개별 해변 상세
│   │   ├── Navbar.vue              # 네비게이션 바
│   │   └── ...
│   └── package.json
├── beach_project/          # AI 분석 프로젝트
│   ├── beach_crowd_analyzer.py    # 혼잡도 분석 스크립트
│   └── yolov8n.pt                 # YOLO 모델
└── data/                   # 해변 동영상 데이터
    ├── hamduck_beach.mp4
    ├── iho_beach.mp4
    └── walljeonglee_beach.mp4
```

## 🛠️ 설치 및 실행

### 1. 백엔드 실행

```bash
cd backend
gradlew.bat bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### 2. 프론트엔드 실행

```bash
cd frontend
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.

### 3. Python 의존성 설치

```bash
cd beach_project
pip install -r requirements.txt
```

또는 개별 설치:

```bash
pip install ultralytics deep-sort-realtime opencv-python pillow numpy
```

## 🎯 사용 방법

1. **메인 페이지**: `/` - 전체 시스템 개요
2. **해변 혼잡도**: `/beach-crowd` - 3개 해변의 혼잡도 요약
3. **개별 해변**: `/beach-crowd/{beachName}` - 특정 해변의 상세 정보
4. **관리자**: `/admin` - 시스템 관리

### 네비게이션

- **담당해변** 드롭다운 메뉴에서 원하는 해변을 선택할 수 있습니다:
  - 함덕해변
  - 이호해변
  - 월정리해변

## 🔧 AI 분석 과정

1. **동영상 입력**: 각 해변의 CCTV 영상 또는 녹화된 동영상
2. **YOLO 탐지**: YOLOv8 모델로 사람 객체 탐지
3. **DeepSORT 추적**: 고유 ID 할당으로 중복 카운트 방지
4. **쓰러짐 감지**: 박스 비율 분석으로 이상 상황 판정
5. **혼잡도 계산**: 현재 인원과 총 방문자 수 기반 혼잡도 레벨 분류
6. **WebSocket 전송**: 실시간으로 프론트엔드에 결과 전송
7. **UI 업데이트**: 5-10초마다 자동으로 화면 업데이트

## 📊 혼잡도 기준

- **낮음**: 0-4명 (초록색)
- **중간**: 5-14명 (노란색)
- **높음**: 15명 이상 (빨간색)

## 🚨 쓰러짐 감지 기준

- **박스 비율**: 가로/세로 ≥ 1.8
- **높이 비율**: 박스 높이/프레임 높이 ≤ 0.35
- **쿨다운**: 5초 간격으로 중복 알림 방지

## 🌐 API 엔드포인트

- `GET /beach-crowd` - 해변 혼잡도 메인 페이지
- `GET /beach-crowd/{beachName}` - 개별 해변 상세 페이지
- `WS /ws` - WebSocket 연결점
- `STOMP /topic/beach-crowd/{beachName}` - 해변별 혼잡도 데이터

## 🔒 보안 및 성능

- CORS 설정으로 프론트엔드와 백엔드 통신 허용
- WebSocket 연결 실패 시 폴백으로 시뮬레이션 데이터 사용
- Python 스크립트 실행 실패 시 Java 기반 시뮬레이션으로 대체

## 🚧 제한사항

- 현재는 시뮬레이션 데이터와 정적 동영상 파일 사용
- 실제 CCTV 연동을 위해서는 추가 개발 필요
- YOLO 모델 로딩 시간으로 인해 10초 간격으로 업데이트
- DeepSORT 추적 정확도는 영상 품질과 사람 밀도에 따라 달라질 수 있음

## 🔮 향후 개발 계획

- [ ] 실시간 CCTV 스트리밍 연동
- [ ] 데이터베이스 연동 및 히스토리 저장
- [ ] 모바일 앱 개발
- [ ] 알림 시스템 구현
- [ ] 예측 분석 기능 추가

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해 주세요.

---

**제주 해변 혼잡도 시스템** - AI 기술로 더 안전하고 편리한 해변 이용을 제공합니다. 🏖️🤖
