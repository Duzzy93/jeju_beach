# 🏖️ 제주 해변 프로젝트 (Jeju Beach Project)

**AI 기반 해변 안전 모니터링 및 관리 시스템**

제주도의 해변을 실시간으로 모니터링하고, YOLO 기반 객체 탐지로 혼잡도 분석 및 안전 사고를 예방하는 스마트 해변 관리 플랫폼입니다.

## ✨ 핵심 기능

| 기능 | 설명 |
|------|------|
| 🔐 **사용자 인증** | JWT 기반 로그인, 역할별 권한 관리 (ADMIN/MANAGER/USER) |
| 🏖️ **해변 관리** | 해변 정보 CRUD, 지역별 검색, 상태 관리 |
| 📹 **CCTV 모니터링** | 실시간 스트리밍, YOLOv8 객체 탐지 |
| 🤖 **AI 챗봇** | OpenAI 기반 해변 정보 및 안전 문의 응답 |
| 📊 **혼잡도 분석** | 실시간 인원 수 집계, 히트맵 시각화 |
| 🚨 **안전 모니터링** | 쓰러짐 감지, 이상 상황 알림 |

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.4.8** + Java 21
- **Spring Security** + JWT 인증
- **Spring Data JPA** + MySQL 8.0
- **WebSocket** 실시간 통신

### Frontend  
- **Vue.js 3** + Composition API
- **Vue Router** + Pinia 상태 관리
- **Bootstrap 5** 반응형 UI

### AI/ML
- **YOLOv8** 객체 탐지
- **DeepSORT** 객체 추적
- **OpenAI API** 챗봇

## 🚀 빠른 시작

### 1. 환경 설정
```bash
# 필수 요구사항
Java 21+, MySQL 8.0+, Node.js 18+
```

### 2. 데이터베이스 설정
```sql
source database_init.sql
```

### 3. 백엔드 실행
```bash
cd backend
./gradlew bootRun
# http://localhost:8080
```

### 4. 프론트엔드 실행
```bash
cd frontend
npm install && npm run dev
# http://localhost:5173
```

## 🔑 기본 계정

| 역할 | 사용자명 | 비밀번호 | 관리 해변 |
|------|----------|----------|-----------|
| **관리자** | `admin` | `admin123` | 모든 해변 |
| **매니저1** | `manager1` | `manager123` | 함덕해변 |
| **매니저2** | `manager2` | `manager123` | 이호테우해변 |
| **매니저3** | `manager3` | `manager123` | 월정리해변 |
| **일반사용자** | `user1` | `user123` | 조회만 가능 |

## 🏖️ 해변별 매니저 할당

- **함덕해변**: manager1이 전담 관리
- **이호테우해변**: manager2가 전담 관리  
- **월정리해변**: manager3가 전담 관리
- **ADMIN**: 모든 해변 관리 가능
- **USER**: 모든 해변 조회 가능 (수정/삭제 불가)

## 📁 프로젝트 구조

```
jeju_beach/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/project/jejubeach/
│   │       ├── controller/  # REST API
│   │       ├── service/     # 비즈니스 로직
│   │       ├── entity/      # JPA 엔티티
│   │       └── config/      # 설정 클래스
│   └── build.gradle
├── frontend/                # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/      # Vue 컴포넌트
│   │   ├── router/          # 라우팅
│   │   └── stores/          # Pinia 스토어
│   └── package.json
└── database_init.sql        # DB 초기화 스크립트
```

## 📊 주요 API

- **인증**: `/api/auth/login`, `/api/auth/register`
- **해변**: `/api/beaches` (CRUD)
- **CCTV**: `/api/cctvs` (스트림 관리)
- **챗봇**: `/api/chatbot/chat`
- **모니터링**: `/api/monitoring/crowd`, `/api/monitoring/falls`

## 🔒 보안 기능

- Spring Security + JWT 토큰 인증
- BCrypt 비밀번호 암호화
- 역할 기반 접근 제어 (RBAC)
- CORS 설정으로 안전한 통신

## 📱 지원 환경

- **웹**: Chrome, Firefox, Safari, Edge
- **모바일**: 반응형 디자인 지원
- **OS**: Windows, macOS, Linux

## 🤝 기여하기

1. Fork → Feature Branch → Commit → Push → Pull Request

## 📄 라이선스

MIT License
---

**📞 문의**: 이슈 등록 또는 프로젝트 팀에 연락

