# 제주 해변 프로젝트 설정 가이드

이 가이드는 제주 해변 프로젝트를 로컬 환경에서 실행하기 위한 단계별 설정 방법을 제공합니다.

## 📋 사전 요구사항

### 필수 소프트웨어
- **Java 21** 이상
- **MySQL 8.0** 이상
- **Node.js 18** 이상
- **Git**

### 권장 사양
- **RAM**: 8GB 이상
- **저장공간**: 10GB 이상
- **OS**: Windows 10/11, macOS, Ubuntu 20.04+

## 🚀 1단계: 프로젝트 클론 및 의존성 설치

### 1.1 프로젝트 다운로드
```bash
git clone [your-repository-url]
cd jeju_beach
```

### 1.2 백엔드 의존성 설치
```bash
cd backend
./gradlew build --refresh-dependencies
```

### 1.3 프론트엔드 의존성 설치
```bash
cd ../frontend
npm install
```

## 🗄️ 2단계: 데이터베이스 설정

### 2.1 MySQL 서버 시작
Windows의 경우:
```bash
# MySQL 서비스 시작
net start mysql80

# 또는 MySQL Workbench에서 시작
```

macOS/Linux의 경우:
```bash
sudo systemctl start mysql
# 또는
sudo service mysql start
```

### 2.2 MySQL 접속
```bash
mysql -u root -p
```

### 2.3 데이터베이스 생성 및 초기화
```sql
-- MySQL에 접속한 후 실행
source database_init.sql;
```

또는 수동으로 실행:
```sql
-- 데이터베이스 생성
CREATE DATABASE jeju_beach_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE jeju_beach_db;

-- 테이블 생성 (database_init.sql 내용 실행)
-- ... (테이블 생성 SQL 실행)
```

### 2.4 기본 계정 확인
```sql
-- 사용자 테이블 확인
SELECT id, username, email, role, is_active FROM users;

-- 해변 테이블 확인
SELECT id, name, region, status FROM beaches;
```

## ⚙️ 3단계: 백엔드 설정

### 3.1 데이터베이스 연결 설정
`backend/src/main/resources/application.yml` 파일에서 데이터베이스 정보 확인:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jeju_beach_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: [your_mysql_password]  # 실제 MySQL 비밀번호로 변경
```

### 3.2 JWT 설정 확인
```yaml
jwt:
  secret: jejuBeachSecretKey2024ForJWTTokenGeneration  # 프로덕션에서는 변경 권장
  expiration: 86400000  # 24시간 (밀리초)
```

### 3.3 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

성공 시 다음 메시지가 표시됩니다:
```
Started JejubeachApplication in X.XXX seconds (process running for X.XXX)
```

## 🌐 4단계: 프론트엔드 실행

### 4.1 프론트엔드 실행
새 터미널에서:
```bash
cd frontend
npm run dev
```

성공 시 다음 메시지가 표시됩니다:
```
Local:   http://localhost:5173/
Network: http://192.168.x.x:5173/
```

## 🔐 5단계: 시스템 테스트

### 5.1 기본 접속 테스트
- **백엔드**: http://localhost:8080
- **프론트엔드**: http://localhost:5173

### 5.2 회원가입 테스트
1. 프론트엔드에서 `/login` 페이지 접속
2. "회원가입" 링크 클릭
3. 테스트 계정 생성:
   - 사용자명: `testuser`
   - 이메일: `test@example.com`
   - 비밀번호: `test123`

### 5.3 로그인 테스트
1. 생성한 계정으로 로그인
2. JWT 토큰이 localStorage에 저장되는지 확인
3. 네비게이션 바에 사용자 정보가 표시되는지 확인

### 5.4 해변 관리 테스트
1. `/beach-management` 페이지 접속
2. 새 해변 추가 테스트
3. 해변 수정/삭제 테스트

## 🚨 문제 해결

### 일반적인 문제들

#### 1. 포트 충돌
```bash
# Windows에서 포트 확인
netstat -ano | findstr :8080
netstat -ano | findstr :5173

# 포트 사용 중인 프로세스 종료
taskkill /PID [process_id] /F
```

#### 2. MySQL 연결 실패
```bash
# MySQL 서비스 상태 확인
net start mysql80

# MySQL 접속 테스트
mysql -u root -p -h localhost
```

#### 3. Java 버전 문제
```bash
# Java 버전 확인
java -version

# JAVA_HOME 설정 확인
echo %JAVA_HOME%
```

#### 4. Gradle 빌드 실패
```bash
# Gradle 캐시 정리
./gradlew clean
./gradlew build --refresh-dependencies
```

#### 5. Node.js 의존성 문제
```bash
# node_modules 삭제 후 재설치
rm -rf node_modules
npm install
```

### 로그 확인

#### 백엔드 로그
```bash
# Spring Boot 로그 확인
tail -f backend/logs/spring.log
```

#### 프론트엔드 로그
브라우저 개발자 도구의 Console 탭에서 에러 확인

## 📱 6단계: 모바일 테스트

### 6.1 네트워크 접근 허용
프론트엔드 실행 시 표시되는 Network URL을 사용하여 모바일에서 접근 테스트

### 6.2 반응형 디자인 확인
다양한 화면 크기에서 UI가 올바르게 표시되는지 확인

## 🔒 7단계: 보안 설정 (프로덕션)

### 7.1 JWT 시크릿 키 변경
```yaml
jwt:
  secret: [강력한_랜덤_문자열_생성]
```

### 7.2 데이터베이스 보안
```sql
-- 전용 사용자 생성
CREATE USER 'jeju_beach_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON jeju_beach_db.* TO 'jeju_beach_user'@'localhost';
FLUSH PRIVILEGES;
```

### 7.3 CORS 설정 제한
```java
// SecurityConfig.java에서 특정 도메인만 허용
.allowedOriginPatterns("https://yourdomain.com")
```

## 📊 8단계: 성능 모니터링

### 8.1 메모리 사용량 확인
```bash
# JVM 메모리 사용량
jstat -gc [process_id]

# 시스템 리소스
top
htop
```

### 8.2 데이터베이스 성능
```sql
-- 느린 쿼리 확인
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Slow_queries';
```

## 🎯 9단계: 기능 테스트 체크리스트

- [ ] 회원가입
- [ ] 로그인/로그아웃
- [ ] JWT 토큰 저장/사용
- [ ] 해변 목록 조회
- [ ] 해변 추가
- [ ] 해변 수정
- [ ] 해변 삭제
- [ ] 해변 상태 변경
- [ ] 권한별 메뉴 표시
- [ ] AI 챗봇 기능
- [ ] 해변 혼잡도 모니터링

## 📞 지원 및 문의

문제가 발생하거나 추가 지원이 필요한 경우:

1. **로그 확인**: 백엔드와 프론트엔드 로그를 먼저 확인
2. **에러 메시지**: 정확한 에러 메시지와 함께 이슈 등록
3. **환경 정보**: OS, Java 버전, MySQL 버전 등 환경 정보 포함

## 🎉 완료!

모든 단계를 완료했다면 제주 해변 프로젝트가 성공적으로 실행되고 있습니다!

- **백엔드 API**: http://localhost:8080
- **프론트엔드**: http://localhost:5173
- **기본 관리자 계정**: admin / admin123
