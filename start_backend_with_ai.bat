@echo off
echo 🏖️ 제주 해변 프로젝트 - 백엔드 + AI 모델 자동 실행
echo ================================================

REM 현재 디렉토리 확인
echo 📁 현재 작업 디렉토리: %CD%

REM Python 환경 확인
echo 🔍 Python 환경 확인 중...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python이 설치되어 있지 않거나 PATH에 등록되지 않았습니다.
    echo    Python을 설치하고 PATH에 추가한 후 다시 시도하세요.
    pause
    exit /b 1
)

echo ✅ Python 환경 확인 완료

REM 필요한 Python 패키지 설치 확인
echo 📦 Python 패키지 설치 확인 중...
cd beach_project
if exist requirements.txt (
    echo 📋 requirements.txt 발견, 패키지 설치 중...
    pip install -r requirements.txt
    if %errorlevel% neq 0 (
        echo ⚠️ 일부 패키지 설치에 실패했습니다. 계속 진행합니다.
    )
) else (
    echo ⚠️ requirements.txt를 찾을 수 없습니다.
)

REM 프로젝트 루트로 돌아가기
cd ..

REM 백엔드 실행
echo 🚀 Spring Boot 백엔드 시작 중...
echo    백엔드가 시작되면 AI 모델이 자동으로 실행됩니다.
echo    브라우저에서 http://localhost:8080 으로 접속하세요.
echo.

cd backend
start "Spring Boot Backend" cmd /k "gradlew bootRun"

REM 잠시 대기
timeout /t 10 /nobreak >nul

echo.
echo ✅ 백엔드가 백그라운드에서 실행 중입니다.
echo 🔗 API 문서: http://localhost:8080/swagger-ui.html
echo 🤖 AI 모델 상태: http://localhost:8080/api/ai-model/status
echo.

REM 프론트엔드 실행 (선택사항)
echo 🌐 프론트엔드도 실행하시겠습니까? (y/n)
set /p choice=
if /i "%choice%"=="y" (
    echo 🚀 Vue.js 프론트엔드 시작 중...
    cd ../frontend
    start "Vue Frontend" cmd /k "npm run dev"
    echo ✅ 프론트엔드가 백그라운드에서 실행 중입니다.
    echo 🌐 프론트엔드: http://localhost:5173
)

echo.
echo 🎉 모든 서비스가 시작되었습니다!
echo.
echo 📋 실행 중인 서비스:
echo    - Spring Boot 백엔드: http://localhost:8080
echo    - AI 모델: 자동 실행됨
echo    - Vue.js 프론트엔드: http://localhost:5173 (선택사항)
echo.
echo 🛑 서비스를 중지하려면 각 터미널 창을 닫으세요.
echo.
pause
