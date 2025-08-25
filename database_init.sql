-- 제주 해변 데이터베이스 초기화 스크립트
-- MySQL root 사용자로 실행

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS jeju_beach_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE jeju_beach_db;

-- 1) 사용자/권한 테이블
CREATE TABLE IF NOT EXISTS users (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          ENUM('ADMIN','MANAGER','USER') NOT NULL DEFAULT 'USER',
  is_active     TINYINT(1) NOT NULL DEFAULT 1,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2) 해변 테이블
CREATE TABLE IF NOT EXISTS beaches (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(100) NOT NULL,
  region        VARCHAR(100) NULL,          -- 행정구/읍면동 등
  latitude      DECIMAL(10,7) NULL,
  longitude     DECIMAL(10,7) NULL,
  description   TEXT NULL,
  status        ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_by    BIGINT NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_beaches_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

-- 3) 해변-매니저 매핑 테이블
CREATE TABLE IF NOT EXISTS beach_managers (
  beach_id   BIGINT NOT NULL,
  user_id    BIGINT NOT NULL,
  assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (beach_id, user_id),
  CONSTRAINT uq_beach_single_manager UNIQUE (beach_id),  -- 해변당 매니저 1명
  CONSTRAINT fk_bm_beach FOREIGN KEY (beach_id) REFERENCES beaches(id) ON DELETE CASCADE,
  CONSTRAINT fk_bm_user  FOREIGN KEY (user_id)  REFERENCES users(id)   ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4) CCTV 테이블
CREATE TABLE IF NOT EXISTS cctvs (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  beach_id      BIGINT NOT NULL,
  name          VARCHAR(100) NOT NULL,
  stream_url    VARCHAR(512) NOT NULL,       -- RTSP/HLS 등
  vendor        VARCHAR(50)  NULL,
  resolution    VARCHAR(20)  NULL,           -- 예: "1920x1080"
  fps           DECIMAL(6,2) NULL,
  install_dt    DATE NULL,
  location_note VARCHAR(255) NULL,           -- 위치 메모(동/서쪽 입구 등)
  is_active     TINYINT(1) NOT NULL DEFAULT 1,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_cctvs_beach FOREIGN KEY (beach_id) REFERENCES beaches(id) ON DELETE CASCADE,
  INDEX idx_cctvs_beach (beach_id),
  INDEX idx_cctvs_active (is_active)
) ENGINE=InnoDB;

-- 5) 보관 영상 세그먼트 테이블
CREATE TABLE IF NOT EXISTS video_segments (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  cctv_id       BIGINT NOT NULL,
  start_ts      DATETIME(3) NOT NULL,        -- 밀리초 단위까지
  end_ts        DATETIME(3) NOT NULL,
  storage_path  VARCHAR(512) NOT NULL,       -- S3/로컬 경로
  size_bytes    BIGINT NULL,
  checksum      VARCHAR(64) NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_vs_cctv FOREIGN KEY (cctv_id) REFERENCES cctvs(id) ON DELETE CASCADE,
  INDEX idx_vs_cctv_time (cctv_id, start_ts)
) ENGINE=InnoDB;

-- 6) 원시 탐지 결과 테이블
CREATE TABLE IF NOT EXISTS detections (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  cctv_id       BIGINT NOT NULL,
  segment_id    BIGINT NULL,
  ts            DATETIME(3) NOT NULL,                       -- 프레임 타임스탬프
  det_type      ENUM('FALL','PERSON','CROWD','OTHER') NOT NULL,
  confidence    DECIMAL(5,3) NOT NULL,
  bbox_x        INT NULL, bbox_y INT NULL, bbox_w INT NULL, bbox_h INT NULL, -- 픽셀 기준
  track_id      VARCHAR(64) NULL,                           -- 딥소트 등 트랙 아이디
  extra_json    JSON NULL,                                  -- 클래스, 키포인트 등
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_det_cctv     FOREIGN KEY (cctv_id)    REFERENCES cctvs(id) ON DELETE CASCADE,
  CONSTRAINT fk_det_segment  FOREIGN KEY (segment_id)  REFERENCES video_segments(id) ON DELETE SET NULL,
  INDEX idx_det_cctv_ts (cctv_id, ts),
  INDEX idx_det_type_ts (det_type, ts)
) ENGINE=InnoDB;

-- 7) 혼잡도 요약 테이블
CREATE TABLE IF NOT EXISTS crowd_metrics (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  cctv_id        BIGINT NOT NULL,
  ts_bucket      DATETIME NOT NULL,         -- 예: 10초/1분 버킷 시작 시각
  window_seconds INT NOT NULL,              -- 윈도우 폭(초)
  people_count   INT NOT NULL,
  density_score  DECIMAL(6,3) NULL,         -- 0~1 정규화 등
  heatmap_path   VARCHAR(512) NULL,         -- 히트맵 이미지 경로(옵션)
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cm_cctv FOREIGN KEY (cctv_id) REFERENCES cctvs(id) ON DELETE CASCADE,
  UNIQUE KEY uq_cm_unique (cctv_id, ts_bucket, window_seconds),
  INDEX idx_cm_cctv_time (cctv_id, ts_bucket)
) ENGINE=InnoDB;

-- 8) 쓰러짐 사건 테이블
CREATE TABLE IF NOT EXISTS fall_incidents (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  cctv_id        BIGINT NOT NULL,
  detection_id   BIGINT NULL,                               -- 근원 detection
  occurred_at    DATETIME(3) NOT NULL,
  status         ENUM('NEW','ACK','FALSE_POSITIVE','RESOLVED') NOT NULL DEFAULT 'NEW',
  snapshot_path  VARCHAR(512) NULL,                         -- 스냅샷 이미지 경로
  notes          TEXT NULL,
  acked_by       BIGINT NULL,
  resolved_by    BIGINT NULL,
  acked_at       DATETIME NULL,
  resolved_at    DATETIME NULL,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_fi_cctv  FOREIGN KEY (cctv_id)      REFERENCES cctvs(id) ON DELETE CASCADE,
  CONSTRAINT fk_fi_det   FOREIGN KEY (detection_id)  REFERENCES detections(id) ON DELETE SET NULL,
  CONSTRAINT fk_fi_ack   FOREIGN KEY (acked_by)      REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_fi_res   FOREIGN KEY (resolved_by)   REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_fi_cctv_time (cctv_id, occurred_at),
  INDEX idx_fi_status (status)
) ENGINE=InnoDB;

-- 기본 관리자 계정 생성 (admin : admin123, manager : manager123, user1 : user123)
-- 올바른 BCrypt 해시값 (Java에서 생성됨)
INSERT INTO users (username, email, password_hash, role, is_active) VALUES 
('admin', 'admin@jejubeach.com', '$2a$10$JpjBBfAlQHPfj1rUIzNO7uoKhhBJnkDtXBaNH4xs1nBhhENqzdUcS', 'ADMIN', 1),
('manager', 'manager@jejubeach.com', '$2a$10$mGa00Ss8myUldsZnbfO2p.QMtwrWj3hBvTGh5yBoRCC5Yhanxj8ru', 'MANAGER', 1),
('user1', 'user1@jejubeach.com', '$2a$10$CGzZltLuFNZStOQCxLWRfuClbrsUmOo354Tl92f4mZ39uVS53ZAlW', 'USER', 1);

-- 샘플 해변 데이터 추가
INSERT INTO beaches (name, region, latitude, longitude, description, created_by) VALUES 
('함덕해변', '제주시 구좌읍', 33.5431, 126.6674, '제주 동부의 아름다운 백사장과 에메랄드빛 바다가 어우러진 해변', 1),
('이호테우해변', '제주시 이도1동', 33.4991, 126.5312, '제주시내에서 가까운 접근성이 좋은 해변', 1),
('월정리해변', '제주시 구좌읍', 33.5589, 126.7856, '제주 최동단에 위치한 아름다운 해변', 1);

-- 샘플 CCTV 데이터 추가
INSERT INTO cctvs (beach_id, name, stream_url, vendor, resolution, fps, location_note) VALUES 
(1, '함덕해변_동쪽', 'rtsp://example.com/hamduck_east', 'VendorA', '1920x1080', 30.0, '동쪽 입구'),
(1, '함덕해변_서쪽', 'rtsp://example.com/hamduck_west', 'VendorA', '1920x1080', 30.0, '서쪽 입구'),
(2, '이호테우_중앙', 'rtsp://example.com/iho_center', 'VendorB', '1920x1080', 25.0, '해변 중앙'),
(3, '월정리_전경', 'rtsp://example.com/walljeonglee', 'VendorC', '1920x1080', 30.0, '전경 촬영');

-- 테이블 생성 확인
SHOW TABLES;

-- 사용자 데이터 확인
SELECT id, username, email, role, is_active, created_at FROM users;

-- 해변 데이터 확인
SELECT id, name, region, latitude, longitude, status, created_at FROM beaches;

-- CCTV 데이터 확인
SELECT id, beach_id, name, stream_url, vendor, resolution, fps FROM cctvs;
