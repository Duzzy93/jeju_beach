@echo off
echo ========================================
echo    제주 해변 혼잡도 시스템 실행
echo ========================================
echo.

echo 1. 백엔드 실행 중...
cd backend
start "Spring Boot Backend" cmd /k "gradlew.bat bootRun"
cd ..

echo 2. 10초 대기 후 프론트엔드 실행...
timeout /t 10 /nobreak > nul

echo 3. 프론트엔드 실행 중...
cd frontend
start "Vue.js Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo    모든 서비스가 실행되었습니다!
echo ========================================
echo.
echo 백엔드: http://localhost:8080
echo 프론트엔드: http://localhost:5173
echo.
echo 브라우저에서 http://localhost:5173 을 열어주세요.
echo.
pause
