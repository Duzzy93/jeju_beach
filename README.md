# 🏖️ 제주 해변 실시간 혼잡도 분석 시스템

## 📋 프로젝트 개요

제주 해변의 실시간 혼잡도를 AI 모델을 통해 분석하고, 웹 애플리케이션으로 제공하는 시스템입니다. YOLOv8과 DeepSORT를 활용한 객체 탐지 및 추적, OpenAI API 기반 챗봇, 실시간 WebSocket 통신을 통해 스마트한 해변 관리 솔루션을 제공합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   AI Model      │
│   (Vue.js 3)    │◄──►│   (Spring Boot) │◄──►│   (Python)      │
│   + Pinia       │    │   + JWT         │    │   + YOLOv8      │
│   + Axios       │    │   + Security    │    │   + DeepSORT    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │    │   MySQL DB      │    │   OpenAI API    │
│   (실시간 통신)  │    │  (데이터 저장)   │    │   (챗봇)        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 주요 기능

### 🔐 사용자 관리 및 인증
- **JWT 기반 인증**: 보안성 높은 토큰 기반 로그인
- **역할 기반 권한 관리**: ADMIN, MANAGER, USER, GUEST 구분
- **BCrypt 암호화**: 안전한 비밀번호 저장
- **자동 토큰 갱신**: Axios 인터셉터를 통한 자동 인증

### 🏖️ 해변 관리 시스템
- **해변 정보 CRUD**: 생성, 조회, 수정, 삭제 (ADMIN 전용)
- **지역별 검색**: 행정구역별 해변 정보 제공
- **상태 관리**: 활성/비활성 해변 구분
- **담당자 할당**: MANAGER별 담당 해변 지정

### 🤖 AI 모델 및 탐지
- **자동 실행**: 백엔드 시작 시 자동 실행
- **실시간 분석**: 30초 간격으로 해변 혼잡도 분석
- **YOLOv8 객체 탐지**: 사람, 쓰러짐 등 실시간 감지
- **DeepSORT 추적**: 연속 프레임에서 객체 추적
- **상태 모니터링**: 실행/중지/재시작 제어

### 💬 AI 챗봇 서비스
- **OpenAI GPT-3.5-turbo**: 자연어 기반 대화
- **해변 정보 제공**: 위치, 특성, 안전 수칙 등
- **빠른 질문**: 자주 묻는 질문에 대한 빠른 응답
- **24시간 응답**: 언제든지 문의 가능

### 📊 데이터 관리 및 모니터링
- **실시간 탐지**: 인원 수, 쓰러진 사람 수
- **통계 분석**: 시간대별 혼잡도, 방문자 통계
- **비디오 관리**: 데이터베이스 기반 동적 비디오 로딩
- **WebSocket**: 실시간 데이터 전송 및 알림

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                          # Spring Boot 백엔드
│   ├── src/main/java/com/project/jejubeach/
│   │   ├── config/                   # 설정 클래스들
│   │   │   ├── SecurityConfig.java   # Spring Security 설정
│   │   │   ├── JwtAuthenticationFilter.java # JWT 필터
│   │   │   └── WebSocketConfig.java  # WebSocket 설정
│   │   ├── controller/               # REST API 컨트롤러
│   │   │   ├── AuthController.java   # 인증 관련
│   │   │   ├── BeachController.java  # 해변 관리
│   │   │   ├── ChatbotController.java # 챗봇
│   │   │   └── DetectionController.java # 탐지 데이터
│   │   ├── service/                  # 비즈니스 로직 서비스
│   │   │   ├── AuthService.java      # 인증 서비스
│   │   │   ├── BeachService.java     # 해변 관리 서비스
│   │   │   ├── ChatbotService.java   # 챗봇 서비스
│   │   │   └── AIModelService.java   # AI 모델 제어
│   │   ├── repository/               # 데이터 접근 계층
│   │   ├── entity/                   # JPA 엔티티
│   │   ├── dto/                      # 데이터 전송 객체
│   │   └── util/                     # 유틸리티 클래스
│   ├── src/main/resources/
│   │   └── application.yml           # 애플리케이션 설정
│   ├── videos/                       # 해변 비디오 파일
│   └── build.gradle                  # Gradle 의존성 관리
├── frontend/                         # Vue.js 3 프론트엔드
│   ├── src/
│   │   ├── components/               # Vue 컴포넌트들
│   │   │   ├── HomePage.vue          # 홈페이지
│   │   │   ├── LoginPage.vue         # 로그인 페이지
│   │   │   ├── BeachManagementPage.vue # 해변 관리
│   │   │   ├── BeachCrowdPage.vue    # 해변 혼잡도
│   │   │   ├── ChatbotPage.vue       # 챗봇
│   │   │   └── AIModelStatusPage.vue # AI 모델 상태
│   │   ├── router/                   # Vue Router 설정
│   │   │   └── index.js              # 라우터 설정
│   │   ├── stores/                   # Pinia 상태 관리
│   │   │   ├── auth.js               # 인증 상태
│   │   │   ├── beach.js              # 해변 데이터
│   │   │   └── chatbot.js            # 챗봇 상태
│   │   ├── api/                      # API 호출 모듈
│   │   │   ├── config.js             # Axios 설정
│   │   │   ├── authApi.js            # 인증 API
│   │   │   ├── beachApi.js           # 해변 API
│   │   │   └── chatbotApi.js         # 챗봇 API
│   │   └── main.js                   # 애플리케이션 진입점
│   ├── package.json                  # npm 의존성
│   └── vite.config.js                # Vite 설정
├── beach_project/                    # Python AI 모델
│   ├── simple_detection_windows.py   # Windows용 탐지 스크립트
│   ├── requirements.txt              # Python 의존성
│   ├── yolov8n.pt                    # YOLOv8 모델 파일
│   └── README.md                     # AI 모델 문서
├── initial_data.sql                  # 초기 데이터베이스 데이터
├── start_backend_with_ai.bat         # Windows 시작 스크립트
├── start_backend_with_ai.sh          # Linux/Mac 시작 스크립트
├── SETUP_GUIDE.md                    # 상세 설정 가이드
├── 프로젝트_발표자료.md                # 프로젝트 발표 자료
└── README.md                         # 프로젝트 문서
```

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.x** - 메인 프레임워크
- **Spring Security + JWT** - 인증 및 권한 관리
- **Spring Data JPA (Hibernate)** - 데이터 접근 계층
- **MySQL 8.0+** - 관계형 데이터베이스
- **WebSocket (STOMP)** - 실시간 통신
- **Gradle** - 빌드 도구
- **Java 21+** - 개발 언어

### Frontend
- **Vue.js 3** - 프론트엔드 프레임워크
- **Vue Router 4** - 클라이언트 사이드 라우팅
- **Pinia** - 상태 관리 라이브러리
- **Axios** - HTTP 클라이언트
- **Bootstrap 5** - UI 컴포넌트 프레임워크
- **Vite** - 빌드 도구 및 개발 서버
- **Node.js 18+** - 런타임 환경

### AI Model
- **Python 3.8+** - 개발 언어
- **YOLOv8 (Ultralytics)** - 객체 탐지 모델
- **DeepSORT** - 객체 추적 알고리즘
- **OpenCV** - 컴퓨터 비전 라이브러리
- **PyTorch** - 딥러닝 프레임워크
- **OpenAI API** - 자연어 처리

### Database & Infrastructure
- **MySQL 8.0+** - 메인 데이터베이스
- **WebSocket** - 실시간 양방향 통신
- **JWT** - JSON Web Token 인증
- **CORS** - Cross-Origin Resource Sharing

## 🚀 빠른 시작

### 1. 환경 준비
```bash
# 필수 소프트웨어
- Java 21+
- Node.js 18+
- Python 3.8+
- MySQL 8.0+
- Git
```

### 2. 프로젝트 클론
```bash
git clone [repository-url]
cd jeju_beach
```

### 3. 데이터베이스 설정
```sql
-- MySQL에서 실행
CREATE DATABASE jeju_beach_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jeju_beach_db;
SOURCE initial_data.sql;
```

### 4. 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 5. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

### 6. AI 모델 자동 실행
- 백엔드 시작 시 자동으로 AI 모델이 실행됩니다
- 수동 제어는 `/ai-model-status` 페이지에서 가능합니다

## 🔑 기본 계정

| 계정 | 이메일 | 비밀번호 | 역할 | 담당 해변 |
|------|--------|----------|------|-----------|
| admin | admin@jejubeach.com | admin123 | ADMIN | 모든 해변 |
| manager1 | manager1@jejubeach.com | manager1 | MANAGER | 함덕해변 |
| manager2 | manager2@jejubeach.com | manager2 | MANAGER | 이호테우해변 |
| manager3 | manager3@jejubeach.com | manager3 | MANAGER | 월정리해변 |
| user1 | user1@jejubeach.com | user123 | USER | 조회만 가능 |

## 📡 API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입

### 해변 관리
- `GET /api/beaches` - 해변 목록 조회 (모든 사용자)
- `GET /api/beaches/{id}` - 해변 상세 정보 (모든 사용자)
- `POST /api/beaches` - 해변 추가 (ADMIN만)
- `PUT /api/beaches/{id}` - 해변 수정 (ADMIN만)
- `DELETE /api/beaches/{id}` - 해변 삭제 (ADMIN만)

### 탐지 데이터
- `GET /api/detections/latest` - 최신 탐지 데이터 (모든 사용자)
- `GET /api/detections/latest10` - 최근 10개 탐지 데이터 (모든 사용자)
- `POST /api/detections` - 탐지 데이터 저장 (AI 모델용)

### AI 모델 제어
- `GET /api/ai-model/status` - AI 모델 상태 확인 (ADMIN만)
- `POST /api/ai-model/start` - AI 모델 시작 (ADMIN만)
- `POST /api/ai-model/stop` - AI 모델 중지 (ADMIN만)
- `POST /api/ai-model/restart` - AI 모델 재시작 (ADMIN만)

### 사용자 관리
- `GET /api/user/profile` - 사용자 프로필 (로그인 사용자)
- `GET /api/user/role` - 사용자 역할 (로그인 사용자)
- `GET /api/user/beaches` - 접근 가능한 해변 (MANAGER, ADMIN)

### 챗봇
- `POST /api/chatbot/message` - 챗봇 메시지 전송 (모든 사용자)
- `GET /api/chatbot/quick-questions` - 빠른 질문 목록 (모든 사용자)

## 🔧 설정

### application.yml 주요 설정
```yaml
# 데이터베이스
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jeju_beach_db
    username: {username}
    password: {password}

# JWT 설정
jwt:
  secret: [Base64 인코딩된 시크릿 키]
  expiration: 86400000 # 24시간

# AI 모델
ai:
  model:
    enabled: true
    python-path: python
    working-dir: ../beach_project
    script-path: simple_detection_windows.py
```

### 환경 변수
```bash
# .env 파일 (backend 디렉토리에 생성)
OPENAI_API_KEY=your_openai_api_key_here
```

## 📊 모니터링

### AI 모델 상태 페이지
- `/ai-model-status` 경로에서 AI 모델 상태 확인
- 실시간 로그 모니터링
- 시작/중지/재시작 제어

### Swagger UI
- `http://localhost:8080/swagger-ui.html`에서 API 문서 확인

### 로그 확인
- 백엔드: 콘솔 출력 및 로그 파일
- 프론트엔드: 브라우저 개발자 도구 콘솔

## 🐛 문제 해결

### 일반적인 문제들
1. **Python 패키지 설치 오류**: `pip install -r requirements.txt` 실행
2. **데이터베이스 연결 오류**: MySQL 서비스 상태 및 설정 확인
3. **AI 모델 실행 오류**: Python 경로 및 스크립트 파일 확인
4. **JWT 토큰 오류**: 시크릿 키 설정 및 토큰 만료 시간 확인

### 로그 확인
- 백엔드: 콘솔 출력 및 로그 파일
- 프론트엔드: 브라우저 개발자 도구 콘솔
- AI 모델: Python 스크립트 콘솔 출력

## 📝 개발 가이드

### 코드 구조
- **Controller**: API 엔드포인트 정의 및 요청/응답 처리
- **Service**: 비즈니스 로직 구현 및 트랜잭션 관리
- **Repository**: 데이터 접근 계층 및 쿼리 처리
- **Entity**: 데이터베이스 테이블 매핑 및 관계 정의
- **DTO**: API 요청/응답 데이터 구조 정의

### 새로운 기능 추가
1. Entity 클래스 생성 및 JPA 어노테이션 설정
2. Repository 인터페이스 정의 및 쿼리 메서드 작성
3. Service 클래스에 비즈니스 로직 구현
4. Controller에 API 엔드포인트 추가
5. 프론트엔드 컴포넌트 생성 및 API 연동
6. Pinia store에 상태 관리 로직 추가

### 보안 고려사항
- JWT 토큰의 안전한 저장 및 전송
- Spring Security를 통한 엔드포인트 보호
- 사용자 입력 데이터 검증 및 sanitization
- CORS 설정을 통한 크로스 오리진 요청 제어

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 GitHub 이슈를 생성해주세요.

---

**🏖️ 제주 해변 실시간 혼잡도 분석 시스템** - AI 기술을 활용한 스마트 해변 관리 솔루션

