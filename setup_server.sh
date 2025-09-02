#!/bin/bash

# AWS EC2 서버 환경 설정 스크립트
# Ubuntu 22.04 LTS 기준

set -e

echo "🔧 AWS EC2 서버 환경 설정 시작..."

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

# 시스템 업데이트
update_system() {
    log_info "시스템 업데이트 중..."
    sudo apt update && sudo apt upgrade -y
    log_success "시스템 업데이트 완료"
}

# Java 21 설치
install_java() {
    log_info "Java 21 설치 중..."
    sudo apt install openjdk-21-jdk -y
    
    # Java 버전 확인
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_success "Java 설치 완료: $java_version"
}

# Python 3.10+ 설치
install_python() {
    log_info "Python 3.10+ 설치 중..."
    sudo apt install python3 python3-pip python3-venv -y
    
    # Python 버전 확인
    python_version=$(python3 --version)
    log_success "Python 설치 완료: $python_version"
}

# Node.js 20+ 설치
install_nodejs() {
    log_info "Node.js 20+ 설치 중..."
    
    # NodeSource 저장소 추가
    curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
    sudo apt-get install -y nodejs
    
    # Node.js 버전 확인
    node_version=$(node --version)
    npm_version=$(npm --version)
    log_success "Node.js 설치 완료: Node $node_version, npm $npm_version"
}

# MySQL 클라이언트 설치
install_mysql_client() {
    log_info "MySQL 클라이언트 설치 중..."
    sudo apt install mysql-client -y
    log_success "MySQL 클라이언트 설치 완료"
}

# 추가 유틸리티 설치
install_utilities() {
    log_info "유틸리티 설치 중..."
    sudo apt install -y htop curl wget git unzip
    log_success "유틸리티 설치 완료"
}

# 방화벽 설정
configure_firewall() {
    log_info "방화벽 설정 중..."
    
    # UFW 활성화
    sudo ufw --force enable
    
    # SSH 허용
    sudo ufw allow ssh
    
    # HTTP/HTTPS 허용
    sudo ufw allow 80
    sudo ufw allow 443
    
    # 애플리케이션 포트 허용
    sudo ufw allow 8080
    
    log_success "방화벽 설정 완료"
}

# 시스템 최적화
optimize_system() {
    log_info "시스템 최적화 중..."
    
    # 스왑 파일 생성 (메모리 부족 시)
    if [ ! -f /swapfile ]; then
        sudo fallocate -l 2G /swapfile
        sudo chmod 600 /swapfile
        sudo mkswap /swapfile
        sudo swapon /swapfile
        echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
        log_success "스왑 파일 생성 완료"
    fi
    
    # 시스템 제한 설정
    echo '* soft nofile 65536' | sudo tee -a /etc/security/limits.conf
    echo '* hard nofile 65536' | sudo tee -a /etc/security/limits.conf
    
    log_success "시스템 최적화 완료"
}

# 앱 디렉토리 생성
create_app_directory() {
    log_info "애플리케이션 디렉토리 생성 중..."
    
    sudo mkdir -p /home/ubuntu/app
    sudo mkdir -p /home/ubuntu/app/python
    sudo mkdir -p /home/ubuntu/app/videos
    sudo mkdir -p /home/ubuntu/app/logs
    
    # 권한 설정
    sudo chown -R ubuntu:ubuntu /home/ubuntu/app
    sudo chmod -R 755 /home/ubuntu/app
    
    log_success "애플리케이션 디렉토리 생성 완료"
}

# 로그 로테이션 설정
configure_log_rotation() {
    log_info "로그 로테이션 설정 중..."
    
    cat > /tmp/jejubeach-logrotate << 'EOF'
/home/ubuntu/app/logs/*.log {
    daily
    missingok
    rotate 7
    compress
    delaycompress
    notifempty
    create 644 ubuntu ubuntu
    postrotate
        systemctl reload jejubeach.service
    endscript
}
EOF

    sudo cp /tmp/jejubeach-logrotate /etc/logrotate.d/jejubeach
    log_success "로그 로테이션 설정 완료"
}

# 메인 실행
main() {
    log_info "AWS EC2 서버 환경 설정 시작"
    
    update_system
    install_java
    install_python
    install_nodejs
    install_mysql_client
    install_utilities
    configure_firewall
    optimize_system
    create_app_directory
    configure_log_rotation
    
    log_success "서버 환경 설정 완료!"
    log_info "다음 단계:"
    log_info "1. 배포 패키지를 서버에 업로드"
    log_info "2. deploy_to_server.sh 실행"
    log_info "3. systemctl start jejubeach.service로 서비스 시작"
    
    # 시스템 정보 출력
    echo ""
    echo "📊 시스템 정보:"
    echo "   OS: $(lsb_release -d | cut -f2)"
    echo "   Java: $(java -version 2>&1 | head -n 1 | cut -d'"' -f2)"
    echo "   Python: $(python3 --version)"
    echo "   Node.js: $(node --version)"
    echo "   메모리: $(free -h | grep Mem | awk '{print $2}')"
    echo "   디스크: $(df -h / | tail -1 | awk '{print $4}') 사용 가능"
}

# 스크립트 실행
main "$@"
