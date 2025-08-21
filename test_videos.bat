@echo off
chcp 65001 >nul
echo ========================================
echo 동영상 파일 접근 테스트
echo ========================================
echo.

echo 1. Backend 동영상 API 테스트...
curl -X GET http://localhost:8080/api/videos
echo.
echo.

echo 2. 동영상 파일 직접 접근 테스트...
echo 함덕해변 동영상:
curl -I http://localhost:8080/videos/hamduck_beach.mp4
echo.

echo 이호해변 동영상:
curl -I http://localhost:8080/videos/iho_beach.mp4
echo.

echo 월정리해변 동영상:
curl -I http://localhost:8080/videos/walljeonglee_beach.mp4
echo.

echo 3. 동영상 파일 크기 확인...
echo 함덕해변: 
dir "backend\src\main\resources\static\videos\hamduck_beach.mp4"
echo.

echo 이호해변:
dir "backend\src\main\resources\static\videos\iho_beach.mp4"
echo.

echo 월정리해변:
dir "backend\src\main\resources\static\videos\walljeonglee_beach.mp4"
echo.

echo ========================================
echo 테스트 완료
echo ========================================
echo.
echo 💡 다음 단계:
echo 1. Backend가 실행 중인지 확인
echo 2. 웹 브라우저에서 http://localhost:8080/videos/hamduck_beach.mp4 접근 테스트
echo 3. Frontend에서 동영상 재생 테스트
echo.
pause
