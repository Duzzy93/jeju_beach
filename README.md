# 🏖️ 해수욕장 인구 밀집 CCTV 프로젝트 (가제)

**Spring Boot + Vue.js + OpenCV + YOLO + MySQL** 기반  
실시간 해수욕장 인구 밀집 및 이상행동 탐지 시스템

---

## 📌 프로젝트 개요
여름철 해수욕장 안전 관리와 효율적인 인원 관리를 위해 CCTV 영상을 분석하여  
실시간 인구 밀집 상황, 지정 구역 이탈, 이상행동(10초 이상 누워있기 등)을 탐지하고  
관리자/담당자에게 알림(문자·메일)을 발송하는 시스템입니다.

---

## 🎯 주요 기능
1. **메인 화면 실시간 영상 출력**
   - CCTV 실시간 스트리밍
   - 영상 목록 및 해수욕장 목록 선택
2. **권한 기반 상세 영상 접근**
   - 로그인 유저별 담당 구역 영상 접근 권한 부여
3. **알림 전송**
   - 담당자에게 문자/메일 발송
   - 3회 무응답 시 해당 구역 외 담당자 및 관리자에게 자동 전송
4. **데이터 시각화**
   - 날짜/시간대별 인구 수 변화 그래프 제공
5. **이상행동 탐지**
   - 지정 구역 외 이동 탐지
   - 10초 이상 누워 있는 사람 탐지
6. **DB 저장 기능**
   - 탐지 이벤트 로그, 유저 정보, 영상 메타데이터 저장

---

## 🛠 기술 스택
| 구분 | 기술 |
|------|------|
| **Frontend** | Vue.js 3, Axios, Chart.js |
| **Backend** | Spring Boot 3.x, Spring Security, JPA(MyBatis 병행 가능) |
| **AI 분석** | OpenCV, YOLOv8 (PyTorch) |
| **Database** | MySQL 8.x |
| **메시징/메일** | Twilio(SMS), JavaMailSender(이메일) |
| **배포 환경** | AWS EC2, Nginx, Docker(선택) |

---

## 📂 시스템 아키텍처
[ CCTV ] → [ 영상 수집 서버(OpenCV) ] → [ YOLO 모델 분석 ]
↓
[ Spring Boot API 서버 ]
↓
[ MySQL DB ] ←→ [ Vue.js 프론트엔드 ] ←→ [ 사용자 ]

---

## 🗄️ DB 설계 (예시)

### user (유저)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| username | VARCHAR(50) | 로그인 아이디 |
| password | VARCHAR(100) | 비밀번호 |
| role | VARCHAR(20) | ROLE_ADMIN / ROLE_MANAGER / ROLE_USER |
| area_id | BIGINT | 담당 구역 |

### area (해수욕장 구역)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| name | VARCHAR(50) | 구역명 |
| cctv_url | VARCHAR(255) | 스트리밍 URL |

### event_log (탐지 이벤트)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGINT | PK |
| event_type | VARCHAR(50) | OUT_OF_ZONE / LYING_DOWN 등 |
| area_id | BIGINT | 관련 구역 |
| timestamp | DATETIME | 발생 시간 |
| status | VARCHAR(20) | pending / resolved |

---

## 📊 기능 흐름도
1. CCTV 영상 입력  
2. OpenCV & YOLO 분석 → 인원 수, 위치, 행동 파악  
3. 이상행동 발생 시 Event Log 저장  
4. Spring Boot에서 Event 처리 → 문자/메일 발송  
5. 프론트엔드에서 실시간 알림 + 시각화 제공

---

## 🚀 실행 방법
1. **YOLO 환경 준비**
   ```bash
   pip install ultralytics opencv-python


2. 백엔드 실행 (Spring Boot)

    bash
    복사
    편집
    ./mvnw spring-boot:run
   
3. 프론트엔드 실행 (Vue.js)

    bash
    복사
    편집
    npm install
    npm run serve

   
4. MySQL DB 생성

    sql
    복사
    편집
    CREATE DATABASE beach_cctv;

5. 환경 변수 설정

    .env 파일에 SMS API, 메일 계정, DB 정보 설정
