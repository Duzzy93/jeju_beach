#!/bin/bash

echo "🏖️ 제주 해변 프로젝트 - 백엔드 + AI 모델 자동 실행"
echo "================================================"

# 현재 디렉토리 확인
echo "📁 현재 작업 디렉토리: $(pwd)"

# Python 환경 확인
echo "🔍 Python 환경 확인 중..."
if command -v python3 &> /dev/null; then
    PYTHON_CMD="python3"
    echo "✅ Python3 발견: $(python3 --version)"
elif command -v python &> /dev/null; then
    PYTHON_CMD="python"
    echo "✅ Python 발견: $(python --version)"
else
    echo "❌ Python이 설치되어 있지 않습니다."
    echo "   Python을 설치한 후 다시 시도하세요."
    exit 1
fi

# 필요한 Python 패키지 설치 확인
echo "📦 Python 패키지 설치 확인 중..."
cd beach_project
if [ -f "requirements.txt" ]; then
    echo "📋 requirements.txt 발견, 패키지 설치 중..."
    $PYTHON_CMD -m pip install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "⚠️ 일부 패키지 설치에 실패했습니다. 계속 진행합니다."
    fi
else
    echo "⚠️ requirements.txt를 찾을 수 없습니다."
fi

# 프로젝트 루트로 돌아가기
cd ..

# 백엔드 실행
echo "🚀 Spring Boot 백엔드 시작 중..."
echo "   백엔드가 시작되면 AI 모델이 자동으로 실행됩니다."
echo "   브라우저에서 http://localhost:8080 으로 접속하세요."
echo

cd backend

# 백그라운드에서 백엔드 실행
echo "🔄 백엔드 시작 중..."
nohup ./gradlew bootRun > backend.log 2>&1 &
BACKEND_PID=$!

echo "✅ 백엔드가 백그라운드에서 실행 중입니다. (PID: $BACKEND_PID)"
echo "📋 백엔드 로그: backend/backend.log"

# 백엔드 시작 대기
echo "⏳ 백엔드 시작 대기 중..."
sleep 15

# 백엔드 상태 확인
if curl -s http://localhost:8080/api/ai-model/status > /dev/null; then
    echo "✅ 백엔드가 정상적으로 시작되었습니다."
else
    echo "⚠️ 백엔드 시작에 시간이 더 필요할 수 있습니다."
fi

# 프론트엔드 실행 (선택사항)
echo
echo "🌐 프론트엔드도 실행하시겠습니까? (y/n)"
read -r choice
if [[ "$choice" =~ ^[Yy]$ ]]; then
    echo "🚀 Vue.js 프론트엔드 시작 중..."
    cd ../frontend
    
    # 백그라운드에서 프론트엔드 실행
    nohup npm run dev > frontend.log 2>&1 &
    FRONTEND_PID=$!
    
    echo "✅ 프론트엔드가 백그라운드에서 실행 중입니다. (PID: $FRONTEND_PID)"
    echo "📋 프론트엔드 로그: frontend/frontend.log"
fi

echo
echo "🎉 모든 서비스가 시작되었습니다!"
echo
echo "📋 실행 중인 서비스:"
echo "   - Spring Boot 백엔드: http://localhost:8080 (PID: $BACKEND_PID)"
echo "   - AI 모델: 자동 실행됨"
if [[ "$choice" =~ ^[Yy]$ ]]; then
    echo "   - Vue.js 프론트엔드: http://localhost:5173 (PID: $FRONTEND_PID)"
fi
echo
echo "🔗 유용한 링크:"
echo "   - API 문서: http://localhost:8080/swagger-ui.html"
echo "   - AI 모델 상태: http://localhost:8080/api/ai-model/status"
echo
echo "📋 로그 파일:"
echo "   - 백엔드: backend/backend.log"
if [[ "$choice" =~ ^[Yy]$ ]]; then
    echo "   - 프론트엔드: frontend/frontend.log"
fi
echo
echo "🛑 서비스를 중지하려면:"
echo "   kill $BACKEND_PID"
if [[ "$choice" =~ ^[Yy]$ ]]; then
    echo "   kill $FRONTEND_PID"
fi
echo
echo "프로그램을 종료하려면 Ctrl+C를 누르세요."
echo "백그라운드에서 계속 실행됩니다."

# 프로세스 ID를 파일에 저장
echo "$BACKEND_PID" > .backend.pid
if [[ "$choice" =~ ^[Yy]$ ]]; then
    echo "$FRONTEND_PID" > .frontend.pid
fi

# 사용자가 Ctrl+C를 누를 때까지 대기
trap 'echo "프로그램을 종료합니다..."; exit 0' INT
while true; do
    sleep 1
done
