# 동영상 파일 디렉토리

이 디렉토리는 해변 CCTV 동영상 파일들을 저장하는 곳입니다.

## 파일 구조
```
videos/
├── hamduck_beach.mp4    # 함덕 해변 CCTV
├── iho_beach.mp4        # 이호 해변 CCTV
└── walljeonglee_beach.mp4 # 월정리 해변 CCTV
```

## 접근 방법
- URL: `http://localhost:8080/videos/{filename}`
- 예: `http://localhost:8080/videos/hamduck_beach.mp4`

## 주의사항
- 동영상 파일은 MP4 형식을 권장합니다
- 파일 크기가 너무 크면 로딩 시간이 길어질 수 있습니다
- 보안을 위해 인증된 사용자만 접근할 수 있도록 설정할 수 있습니다
