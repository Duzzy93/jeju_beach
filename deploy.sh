#!/bin/bash

# JEJU_BEACH 프로젝트 AWS 배포 스크립트 (단일 JAR 배포)
# 사용법: ./deploy.sh

set -e

echo "🚀 JEJU_BEACH 프로젝트 배포 시작..."

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 환경 변수 확인
check_env() {
    log_info "환경 변수 확인 중..."
    
    if [ ! -f "env.prod" ]; then
        log_error "env.prod 파일이 없습니다!"
        exit 1
    fi
    
    log_success "환경 변수 파일 확인 완료"
}

# 백엔드 빌드
build_backend() {
    log_info "백엔드 빌드 시작..."
    
    cd backend
    
    # Gradle 래퍼 권한 설정
    chmod +x gradlew
    
    # 백엔드 빌드 (프론트엔드 + Python + 비디오 포함)
    ./gradlew clean build -x test
    
    if [ $? -eq 0 ]; then
        log_success "백엔드 빌드 완료"
    else
        log_error "백엔드 빌드 실패"
        exit 1
    fi
    
    cd ..
}

# 배포 패키지 생성
create_deployment_package() {
    log_info "배포 패키지 생성 중..."
    
    # 배포 디렉토리 생성
    mkdir -p deploy
    
    # JAR 파일 복사
    cp backend/build/libs/*.jar deploy/jejubeach.jar
    
    # 환경 변수 파일 복사
    cp env.prod deploy/.env
    
    # systemd 서비스 파일 생성
    cat > deploy/jejubeach.service << 'EOF'
[Unit]
Description=Jeju Beach Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/app
ExecStart=/usr/bin/java -jar /home/ubuntu/app/jejubeach.jar
EnvironmentFile=/home/ubuntu/app/.env
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

    # 배포 스크립트 생성
    cat > deploy/deploy_to_server.sh << 'EOF'
#!/bin/bash

# 서버 배포 스크립트 (단일 JAR 배포)
set -e

echo "🚀 서버에 배포 중..."

# 앱 디렉토리 생성
sudo mkdir -p /home/ubuntu/app

# 파일 복사 (JAR 파일과 환경 변수만)
sudo cp jejubeach.jar /home/ubuntu/app/
sudo cp .env /home/ubuntu/app/

# 권한 설정
sudo chown -R ubuntu:ubuntu /home/ubuntu/app
sudo chmod +x /home/ubuntu/app/jejubeach.jar

# systemd 서비스 설치
sudo cp jejubeach.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable jejubeach.service

echo "✅ 배포 완료!"
echo "📋 다음 명령어로 서비스 관리:"
echo "   시작: sudo systemctl start jejubeach.service"
echo "   상태: sudo systemctl status jejubeach.service"
echo "   중지: sudo systemctl stop jejubeach.service"
echo "   로그: sudo journalctl -u jejubeach.service -f"
EOF

    chmod +x deploy/deploy_to_server.sh
    
    log_success "배포 패키지 생성 완료"
}

# 배포 가이드 생성
create_deployment_guide() {
    log_info "배포 가이드 생성 중..."
    
    cat > DEPLOYMENT_GUIDE.md << 'EOF'
# JEJU_BEACH 프로젝트 AWS 배포 가이드 (단일 JAR 배포)

## 사전 준비사항

### 1. AWS EC2 인스턴스 설정
- Ubuntu 22.04 LTS 권장
- 최소 사양: t3.medium (2 vCPU, 4GB RAM)
- 보안 그룹에서 포트 80, 443, 8080 열기

### 2. 서버 환경 설정

```bash
# 시스템 업데이트
sudo apt update && sudo apt upgrade -y

# Java 21 설치
sudo apt install openjdk-21-jdk -y

# Python 3.10+ 설치
sudo apt install python3 python3-pip python3-venv -y

# Node.js 20+ 설치 (프론트엔드 빌드용)
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# MySQL 클라이언트 설치 (선택사항)
sudo apt install mysql-client -y
```

### 3. 환경 변수 설정

1. `env.prod` 파일을 편집하여 실제 값으로 변경:
   - `SPRING_DATASOURCE_USERNAME`: 실제 DB 사용자명
   - `SPRING_DATASOURCE_PASSWORD`: 실제 DB 비밀번호
   - `OPENAI_API_KEY`: 실제 OpenAI API 키
   - `JWT_SECRET`: 안전한 JWT 시크릿 키

2. 파일을 서버의 `/home/ubuntu/app/.env`로 업로드

## 배포 방법

### 1. 로컬에서 배포 패키지 생성
```bash
./deploy.sh
```

### 2. 서버에 파일 업로드
```bash
# SCP를 사용하여 배포 패키지 업로드
scp -r deploy/* ubuntu@your-server-ip:/tmp/

# 또는 rsync 사용
rsync -avz deploy/ ubuntu@your-server-ip:/tmp/
```

### 3. 서버에서 배포 실행
```bash
# 서버에 접속
ssh ubuntu@your-server-ip

# 배포 스크립트 실행
cd /tmp
chmod +x deploy_to_server.sh
./deploy_to_server.sh
```

### 4. 서비스 시작
```bash
# 서비스 시작
sudo systemctl start jejubeach.service

# 상태 확인
sudo systemctl status jejubeach.service

# 로그 확인
sudo journalctl -u jejubeach.service -f
```

## 서비스 관리

### 서비스 제어
```bash
# 서비스 시작
sudo systemctl start jejubeach.service

# 서비스 중지
sudo systemctl stop jejubeach.service

# 서비스 재시작
sudo systemctl restart jejubeach.service

# 서비스 상태 확인
sudo systemctl status jejubeach.service
```

### 로그 확인
```bash
# 실시간 로그 확인
sudo journalctl -u jejubeach.service -f

# 최근 로그 확인
sudo journalctl -u jejubeach.service -n 100

# 특정 시간 이후 로그 확인
sudo journalctl -u jejubeach.service --since "2024-01-01 00:00:00"
```

## 문제 해결

### 1. 서비스가 시작되지 않는 경우
```bash
# 상세한 오류 확인
sudo systemctl status jejubeach.service

# 환경 변수 파일 확인
cat /home/ubuntu/app/.env

# Java 버전 확인
java -version

# 포트 사용 확인
sudo netstat -tlnp | grep 8080
```

### 2. 데이터베이스 연결 오류
- RDS 보안 그룹에서 EC2 인스턴스 IP 허용
- 데이터베이스 접속 정보 확인
- 네트워크 연결 확인

### 3. AI 모델 실행 오류
```bash
# Python 패키지 설치 확인
python3 --version
pip3 --version

# 로그에서 AI 모델 오류 확인
sudo journalctl -u jejubeach.service -f
```

### 4. 포트 접속 문제
- AWS 보안 그룹에서 포트 8080 허용
- 방화벽 설정 확인

## 업데이트 방법

### 1. 새 버전 배포
```bash
# 로컬에서 새 패키지 생성
./deploy.sh

# 서버에 업로드 및 배포
scp -r deploy/* ubuntu@your-server-ip:/tmp/
ssh ubuntu@your-server-ip "cd /tmp && ./deploy_to_server.sh"
```

### 2. 서비스 재시작
```bash
sudo systemctl restart jejubeach.service
```

## 모니터링

### 1. 시스템 리소스 모니터링
```bash
# CPU 및 메모리 사용량
htop

# 디스크 사용량
df -h

# 네트워크 사용량
iftop
```

### 2. 애플리케이션 모니터링
- Swagger UI: http://your-server-ip:8080/swagger-ui.html
- API 문서: http://your-server-ip:8080/api-docs
- 메인 애플리케이션: http://your-server-ip:8080

## 보안 고려사항

1. 방화벽 설정
2. SSL/TLS 인증서 설정
3. 정기적인 보안 업데이트
4. 로그 모니터링
5. 백업 정책 수립
EOF

    log_success "배포 가이드 생성 완료"
}

# 메인 실행
main() {
    log_info "JEJU_BEACH 프로젝트 배포 준비 시작"
    
    check_env
    build_backend
    create_deployment_package
    create_deployment_guide
    
    log_success "배포 준비 완료!"
    log_info "다음 단계:"
    log_info "1. env.prod 파일을 실제 값으로 수정"
    log_info "2. deploy/ 폴더의 파일들을 서버에 업로드"
    log_info "3. 서버에서 deploy_to_server.sh 실행"
    log_info "4. systemctl start jejubeach.service로 서비스 시작"
}

# 스크립트 실행
main "$@"
