# 🏖️ 제주 해변 실시간 혼잡도 분석 시스템

## 📋 프로젝트 개요

제주 해변의 실시간 혼잡도를 AI 모델을 통해 분석하고, 웹 애플리케이션으로 제공하는 시스템입니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   AI Model      │
│   (Vue.js 3)    │◄──►│   (Spring Boot) │◄──►│   (Python)      │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │    │   MySQL DB      │    │   YOLOv8 +      │
│   (실시간 통신)  │    │   (데이터 저장)  │    │   DeepSORT      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🚀 주요 기능

### 🔐 사용자 관리
- **ADMIN**: 전체 시스템 관리
- **MANAGER**: 지정된 해변만 관리
- **USER**: 일반 사용자
- **GUEST**: 게스트 (제한된 접근)

### 🏖️ 해변 관리
- **함덕해변**: manager1 전담
- **이호해변**: manager2 전담  
- **월정리해변**: manager3 전담

### 🤖 AI 모델
- **자동 실행**: 백엔드 시작 시 자동 실행
- **실시간 분석**: 30초 간격으로 해변 혼잡도 분석
- **상태 모니터링**: 실행/중지/재시작 제어
- **롤링 윈도우**: 최신 10개 데이터만 유지

### 📊 데이터 관리
- **실시간 탐지**: 인원 수, 쓰러진 사람 수
- **통계 분석**: 시간대별 혼잡도, 방문자 통계
- **비디오 관리**: 데이터베이스 기반 동적 비디오 로딩

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                          # Spring Boot 백엔드
│   ├── src/main/java/com/project/jejubeach/
│   │   ├── config/                   # 설정 클래스들
│   │   ├── controller/               # REST API 컨트롤러
│   │   ├── service/                  # 비즈니스 로직 서비스
│   │   ├── repository/               # 데이터 접근 계층
│   │   ├── entity/                   # JPA 엔티티
│   │   ├── dto/                      # 데이터 전송 객체
│   │   └── util/                     # 유틸리티 클래스
│   └── src/main/resources/
│       └── application.yml           # 애플리케이션 설정
├── frontend/                         # Vue.js 3 프론트엔드
│   ├── src/components/               # Vue 컴포넌트들
│   ├── src/router/                   # Vue Router 설정
│   ├── src/stores/                   # Pinia 상태 관리
│   └── src/main.js                   # 애플리케이션 진입점
├── beach_project/                    # Python AI 모델
│   ├── simple_detection_windows.py   # Windows용 탐지 스크립트 (메인)
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
- **Spring Boot 3.x**
- **Spring Security + JWT**
- **Spring Data JPA (Hibernate)**
- **MySQL 8.0+**
- **WebSocket (STOMP)**
- **Gradle**

### Frontend
- **Vue.js 3**
- **Vue Router 4**
- **Pinia (상태 관리)**
- **Bootstrap 5**
- **Vite**

### AI Model
- **Python 3.8+**
- **YOLOv8 (Ultralytics)**
- **DeepSORT**
- **OpenCV**
- **PyTorch**

## 🚀 빠른 시작

### 1. 환경 준비
```bash
# 필수 소프트웨어
- Java 17+
- Node.js 18+
- Python 3.8+
- MySQL 8.0+
```

### 2. 데이터베이스 설정
```sql
-- MySQL에서 실행
CREATE DATABASE jeju_beach_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

### 5. AI 모델 자동 실행
- 백엔드 시작 시 자동으로 AI 모델이 실행됩니다
- 수동 제어는 `/ai-model-status` 페이지에서 가능합니다

## 🔑 기본 계정

| 계정 | 이메일 | 비밀번호 | 역할 |
|------|--------|----------|------|
| admin | admin@jejubeach.com | admin123 | ADMIN |
| manager1 | manager1@jejubeach.com | manager1 | MANAGER |
| manager2 | manager2@jejubeach.com | manager2 | MANAGER |
| manager3 | manager3@jejubeach.com | manager3 | MANAGER |
| user1 | user1@jejubeach.com | user123 | USER |

## 📡 API 엔드포인트

### 인증
- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입

### 해변 관리
- `GET /api/beaches` - 해변 목록 조회
- `GET /api/beaches/{id}` - 해변 상세 정보
- `POST /api/beaches` - 해변 추가
- `PUT /api/beaches/{id}` - 해변 수정
- `DELETE /api/beaches/{id}` - 해변 삭제

### 탐지 데이터
- `GET /api/detections/latest` - 최신 탐지 데이터
- `GET /api/detections/latest10` - 최근 10개 탐지 데이터
- `POST /api/detections` - 탐지 데이터 저장

### AI 모델 제어
- `GET /api/ai-model/status` - AI 모델 상태 확인
- `POST /api/ai-model/start` - AI 모델 시작
- `POST /api/ai-model/stop` - AI 모델 중지
- `POST /api/ai-model/restart` - AI 모델 재시작

## 🔧 설정

### application.yml 주요 설정
```yaml
# 데이터베이스
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jeju_beach_db
    username: root
    password: 12345

# AI 모델
ai:
  model:
    enabled: true
    python-path: python
    working-dir: ../beach_project
    script-path: simple_detection_windows.py
```

## 📊 모니터링

### AI 모델 상태 페이지
- `/ai-model-status` 경로에서 AI 모델 상태 확인
- 실시간 로그 모니터링
- 시작/중지/재시작 제어

### Swagger UI
- `http://localhost:8080/swagger-ui.html`에서 API 문서 확인

## 🐛 문제 해결

### 일반적인 문제들
1. **Python 패키지 설치 오류**: `pip install -r requirements.txt` 실행
2. **데이터베이스 연결 오류**: MySQL 서비스 상태 및 설정 확인
3. **AI 모델 실행 오류**: Python 경로 및 스크립트 파일 확인

### 로그 확인
- 백엔드: `backend/logs/` 디렉토리
- 프론트엔드: 브라우저 개발자 도구 콘솔

## 📝 개발 가이드

### 코드 구조
- **Controller**: API 엔드포인트 정의
- **Service**: 비즈니스 로직 구현
- **Repository**: 데이터 접근 계층
- **Entity**: 데이터베이스 테이블 매핑
- **DTO**: API 요청/응답 데이터 구조

### 새로운 기능 추가
1. Entity 클래스 생성
2. Repository 인터페이스 정의
3. Service 클래스에 비즈니스 로직 구현
4. Controller에 API 엔드포인트 추가
5. 프론트엔드 컴포넌트 생성

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해주세요.

---

**제주 해변 실시간 혼잡도 분석 시스템** - AI 기술을 활용한 스마트 해변 관리 솔루션

