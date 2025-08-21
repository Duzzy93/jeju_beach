# 제주 해변 AI 가이드 시스템

AI 기술을 활용한 제주 해변 실시간 혼잡도 모니터링 및 AI 챗봇 가이드 시스템입니다.

## 🏖️ 주요 기능

- **실시간 해변 혼잡도 분석**: YOLO + DeepSORT를 사용한 정확한 사람 수 탐지
- **AI 챗봇 가이드**: OpenAI GPT 기반 제주 해변 전문 가이드
- **중복 카운트 방지**: DeepSORT 추적으로 고유 방문자 수 계산
- **쓰러짐 감지**: AI 기반 이상 상황 자동 감지 및 알림
- **WebSocket 실시간 통신**: 실시간 데이터 업데이트
- **3개 해변 모니터링**: 함덕해변, 이호해변, 월정리해변
- **반응형 웹 인터페이스**: Vue.js와 Bootstrap을 활용한 모던한 UI

## 🚀 기술 스택

### 백엔드
- **Spring Boot 3.2.0**: Java 17 기반
- **WebSocket**: 실시간 통신
- **OpenAI API**: AI 챗봇 서비스
- **Gradle**: 빌드 도구

### 프론트엔드
- **Vue.js 3**: 프론트엔드 프레임워크
- **Bootstrap 5**: UI 컴포넌트
- **SockJS + STOMP**: WebSocket 클라이언트

### AI/ML
- **YOLOv8**: 객체 탐지 모델
- **DeepSORT**: 실시간 객체 추적
- **OpenCV**: 이미지 처리
- **OpenAI GPT-3.5**: 자연어 처리 및 챗봇

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   ├── config/         # WebSocket 설정
│   │   ├── controller/     # 컨트롤러 (BeachVideo, Chatbot)
│   │   ├── service/        # 비즈니스 로직
│   │   └── dto/           # 데이터 전송 객체
│   ├── src/main/resources/
│   │   ├── static/videos/  # 해변 동영상 데이터
│   │   └── application.yml # 설정 파일
│   └── build.gradle
├── frontend/               # Vue.js 프론트엔드
│   ├── src/components/     # Vue 컴포넌트
│   │   ├── HomePage.vue           # 메인 홈페이지
│   │   ├── BeachCrowdPage.vue     # 해변 혼잡도 메인
│   │   ├── BeachDetailPage.vue    # 개별 해변 상세
│   │   ├── ChatbotPage.vue        # AI 챗봇 페이지
│   │   ├── AdminPage.vue          # 관리자 페이지
│   │   └── Navbar.vue             # 네비게이션 바
│   └── package.json
├── beach_project/          # AI 분석 프로젝트
│   ├── beach_crowd_analyzer.py           # 혼잡도 분석 스크립트
│   ├── beach_cv_yolo_deepsort_refactored.ipynb  # Jupyter 노트북
│   └── yolov8n.pt                          # YOLO 모델
└── run_project.bat         # 통합 실행 스크립트
```

## 🛠️ 설치 및 실행

### 1. 환경 설정

**Backend 환경 변수 설정:**
`backend/` 폴더에 `.env` 파일을 생성하고 OpenAI API 키를 설정하세요:

```bash
OPENAI_API_KEY=sk-your_actual_openai_api_key_here
```

### 2. 백엔드 실행

```bash
cd backend
gradlew.bat bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.

### 4. 통합 실행 (권장)

프로젝트 루트에서 `run_project.bat`을 실행하면 백엔드와 프론트엔드를 자동으로 시작합니다.

## 🎯 사용 방법

1. **메인 페이지**: `/` - 전체 시스템 개요 및 AI 챗봇 소개
2. **해변 혼잡도**: `/beach-crowd` - 3개 해변의 혼잡도 요약
3. **AI 챗봇**: `/chatbot` - 제주 해변 전문 AI 가이드와 대화
4. **개별 해변**: `/beach-crowd/{beachName}` - 특정 해변의 상세 정보
5. **관리자**: `/admin` - 시스템 관리

### AI 챗봇 기능

- **자연어 대화**: 제주 해변에 대한 모든 질문에 답변
- **빠른 질문**: 미리 정의된 질문으로 즉시 답변
- **맞춤형 추천**: 개인 취향에 따른 해변 추천
- **실시간 혼잡도 정보**: 현재 해변 상황 정보 제공

## 🔧 API 엔드포인트

### 챗봇 API
- `POST /api/chatbot/chat` - 챗봇 대화
- `GET /api/chatbot/quick-questions` - 빠른 질문 목록
- `GET /api/chatbot/status` - API 키 상태 확인
- `POST /api/chatbot/test` - 연결 테스트

### 해변 API
- `GET /api/videos` - 해변 동영상 목록

## 🚨 문제 해결

### OpenAI API 오류 (401 Unauthorized)
1. `backend/.env` 파일에 올바른 API 키 설정
2. Backend 재시작
3. `http://localhost:8080/api/chatbot/status`로 상태 확인

### 의존성 문제
- Backend: `gradlew.bat build`로 빌드 확인
- Frontend: `npm install`로 의존성 재설치

## 📝 라이선스

이 프로젝트는 교육 및 연구 목적으로 제작되었습니다.
