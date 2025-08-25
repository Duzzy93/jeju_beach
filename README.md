# 제주 해변 프로젝트 (Jeju Beach Project)

제주도의 해변을 관리하고 CCTV를 통한 혼잡도 분석 및 안전 모니터링을 제공하는 웹 애플리케이션입니다.

## 🚀 주요 기능

### 🔐 사용자 인증
- 회원가입 및 로그인
- JWT 기반 인증
- 역할 기반 권한 관리 (ADMIN, MANAGER, USER)

### 🏖️ 해변 관리
- 해변 정보 CRUD (생성, 조회, 수정, 삭제)
- 지역별 해변 검색
- 해변 상태 관리 (활성/비활성)

### 📹 CCTV 모니터링
- 실시간 CCTV 스트리밍
- YOLO 기반 객체 탐지
- 혼잡도 분석 및 시각화

### 🤖 AI 챗봇
- OpenAI 기반 챗봇 서비스
- 해변 정보 및 안전 관련 문의 응답

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.4.8** - Java 21
- **Spring Security** - 인증 및 권한 관리
- **Spring Data JPA** - 데이터 접근 계층
- **MySQL 8.0** - 데이터베이스
- **JWT** - 토큰 기반 인증
- **Gradle** - 빌드 도구

### Frontend
- **Vue.js 3** - 프론트엔드 프레임워크
- **Vue Router** - 라우팅
- **Vite** - 빌드 도구

### AI/ML
- **YOLOv8** - 객체 탐지
- **DeepSORT** - 객체 추적
- **OpenAI API** - 챗봇 서비스

## 📋 시스템 요구사항

- **Java**: 21 이상
- **MySQL**: 8.0 이상
- **Node.js**: 18 이상
- **Python**: 3.8 이상 (AI 모델용)

## 🚀 설치 및 실행

### 1. 데이터베이스 설정

MySQL에 root 사용자로 접속하여 다음 명령을 실행:

```sql
-- 데이터베이스 생성
CREATE DATABASE jeju_beach_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 초기화 스크립트 실행
source database_init.sql;
```

### 2. 백엔드 실행

```bash
# Windows
run_backend.bat

# 또는 수동 실행
cd backend
./gradlew bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://localhost:5173`에서 실행됩니다.

## 🔧 설정

### 백엔드 설정 (`backend/src/main/resources/application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jeju_beach_db
    username: root
    password: [your_password]
```

### JWT 설정

```yaml
jwt:
  secret: [your_jwt_secret_key]
  expiration: 86400000  # 24시간
```

## 📊 API 엔드포인트

### 인증
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/login` - 로그인

### 해변 관리
- `GET /api/beaches` - 모든 해변 조회
- `GET /api/beaches/active` - 활성 해변만 조회
- `GET /api/beaches/{id}` - 특정 해변 조회
- `POST /api/beaches` - 해변 생성
- `PUT /api/beaches/{id}` - 해변 수정
- `DELETE /api/beaches/{id}` - 해변 삭제
- `PATCH /api/beaches/{id}/toggle-status` - 해변 상태 변경

### 챗봇
- `POST /api/chatbot/chat` - 챗봇 대화

## 👥 기본 계정

- **관리자**: `admin` / `admin123`
- **이메일**: `admin@jejubeach.com`

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/project/jejubeach/
│   │       ├── config/      # 설정 클래스
│   │       ├── controller/  # REST API 컨트롤러
│   │       ├── dto/         # 데이터 전송 객체
│   │       ├── entity/      # JPA 엔티티
│   │       ├── repository/  # 데이터 접근 계층
│   │       └── service/     # 비즈니스 로직
│   └── src/main/resources/
│       └── application.yml  # 설정 파일
├── frontend/                # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/      # Vue 컴포넌트
│   │   └── router/          # 라우팅 설정
│   └── package.json
├── database_init.sql        # 데이터베이스 초기화 스크립트
└── README.md               # 프로젝트 문서
```

## 🔒 보안

- Spring Security를 통한 인증 및 권한 관리
- JWT 토큰 기반 세션 관리
- BCrypt를 통한 비밀번호 암호화
- CORS 설정으로 프론트엔드와의 안전한 통신

## 🤝 기여하기

1. 이 저장소를 포크합니다
2. 기능 브랜치를 생성합니다 (`git checkout -b feature/AmazingFeature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add some AmazingFeature'`)
4. 브랜치에 푸시합니다 (`git push origin feature/AmazingFeature`)
5. Pull Request를 생성합니다

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
