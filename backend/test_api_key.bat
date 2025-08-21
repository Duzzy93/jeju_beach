@echo off
echo ========================================
echo OpenAI API 키 상태 확인
echo ========================================
echo.

echo 1. 챗봇 서비스 상태 확인...
curl -X GET http://localhost:8080/api/chatbot/status
echo.
echo.

echo 2. 챗봇 연결 테스트...
curl -X POST http://localhost:8080/api/chatbot/test
echo.
echo.

echo 3. 환경 변수 확인...
echo OPENAI_API_KEY: %OPENAI_API_KEY%
echo.

echo ========================================
echo 테스트 완료
echo ========================================
pause
