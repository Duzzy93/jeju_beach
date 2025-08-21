@echo off
chcp 65001 >nul
echo ========================================
echo 환경 변수 및 API 키 상세 테스트
echo ========================================
echo.

echo 1. 현재 작업 디렉토리 확인...
echo 현재 위치: %CD%
echo.

echo 2. .env 파일 존재 여부 확인...
if exist .env (
    echo ✅ .env 파일이 존재합니다.
    echo 📄 .env 파일 내용:
    type .env
) else (
    echo ❌ .env 파일이 존재하지 않습니다.
    echo 💡 backend 폴더에 .env 파일을 생성해주세요.
)
echo.

echo 3. 환경 변수 확인...
echo OPENAI_API_KEY: %OPENAI_API_KEY%
echo.

echo 4. 시스템 프로퍼티 확인...
echo System Property OPENAI_API_KEY: %OPENAI_API_KEY%
echo.

echo 5. Java 시스템 프로퍼티 확인...
java -cp . -Dfile.encoding=UTF-8 -Djava.security.manager=allow -Djava.security.policy=allow -Djava.security.debug=all -Dopenai.api.key=test -version 2>nul
if errorlevel 1 (
    echo Java 시스템 프로퍼티 설정 테스트 실패
) else (
    echo Java 시스템 프로퍼티 설정 가능
)
echo.

echo 6. 챗봇 API 상태 확인 (Backend 실행 중인 경우)...
curl -X GET http://localhost:8080/api/chatbot/status 2>nul
if errorlevel 1 (
    echo ❌ Backend가 실행되지 않았거나 연결할 수 없습니다.
    echo 💡 Backend를 먼저 실행해주세요.
) else (
    echo ✅ Backend 연결 성공
)
echo.

echo 7. .env 파일 경로 테스트...
echo 현재 디렉토리: %CD%
echo 상위 디렉토리: %CD%\..
echo backend 폴더: %CD%\backend
echo.

echo ========================================
echo 테스트 완료
echo ========================================
echo.
echo 💡 다음 단계:
echo 1. backend 폴더에 .env 파일 생성
echo 2. OPENAI_API_KEY=sk-your_actual_key_here 추가
echo 3. Backend 재시작
echo 4. 이 스크립트 다시 실행
echo.
pause