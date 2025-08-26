-- 제주 해변 시스템 초기 데이터 삽입 스크립트
-- Spring Boot가 테이블을 자동 생성한 후 실행

USE jeju_beach_db;

-- ========================================
-- 1. 기본 사용자 계정 생성
-- ========================================
-- admin: admin123, manager1: manager123, manager2: manager123, manager3: manager123, user1: user123
INSERT INTO users (username, email, password_hash, role, is_active, created_at, updated_at) VALUES 
('admin', 'admin@jejubeach.com', '$2a$10$36eIc5ZHs0HTRpQp6Z3MVOnLtRkxN3Fe0HOAGO1e/Tedr9FbeeZI2', 'ADMIN', 1, NOW(), NOW()),
('manager1', 'manager1@jejubeach.com', '$2a$10$WJ9wJUMGRCPMZG5WKPsoF.v4fGjrjNNqkqcMC9KucQwaUAVMim9F2', 'MANAGER', 1, NOW(), NOW()),
('manager2', 'manager2@jejubeach.com', '$2a$10$M.9j3OG1YKcvWq//1Dgaa.u/cBuowTwJm0SiCtEVe.NsWodA9jnTK', 'MANAGER', 1, NOW(), NOW()),
('manager3', 'manager3@jejubeach.com', '$2a$10$.RD6QcgAtSo2w6aZzsXvhOHOAi25lwCwIqnJzIGYeaQpM9kX6X7BK', 'MANAGER', 1, NOW(), NOW()),
('user1', 'user1@jejubeach.com', '$2a$10$oRRk/C/3egU/UQMALlSxOu1fwCV1C9bSF8j64jWhqzapV3tRSmztG', 'USER', 1, NOW(), NOW());

-- ========================================
-- 2. 샘플 해변 데이터 추가 (video_path 포함)
-- ========================================
INSERT INTO beaches (name, region, latitude, longitude, description, video_path, created_by) VALUES 
('함덕해변', '제주시 구좌읍', 33.5431, 126.6674, '제주 동부의 아름다운 백사장과 에메랄드빛 바다가 어우러진 해변', '/videos/hamduck_beach.mp4', 1),
('이호테우해변', '제주시 이도1동', 33.4991, 126.5312, '제주시내에서 가까운 접근성이 좋은 해변', '/videos/iho_beach.mp4', 1),
('월정리해변', '제주시 구좌읍', 33.5589, 126.7856, '제주 최동단에 위치한 아름다운 해변', '/videos/walljeonglee_beach.mp4', 1);

-- ========================================
-- 3. 해변-매니저 매핑 데이터 추가
-- ========================================
-- manager1 -> 함덕해변
INSERT INTO beach_managers (beach_id, user_id, assigned_at, is_active) VALUES 
(1, 2, NOW(), 1);

-- manager2 -> 이호테우해변  
INSERT INTO beach_managers (beach_id, user_id, assigned_at, is_active) VALUES 
(2, 3, NOW(), 1);

-- manager3 -> 월정리해변
INSERT INTO beach_managers (beach_id, user_id, assigned_at, is_active) VALUES 
(3, 4, NOW(), 1);


-- ========================================
-- 4. 데이터 확인
-- ========================================
SELECT '=== 사용자 데이터 ===' as info;
SELECT id, username, email, role, is_active, created_at FROM users;

SELECT '=== 해변 데이터 ===' as info;
SELECT id, name, region, video_path, status, created_at FROM beaches;

SELECT '=== 해변-매니저 매핑 ===' as info;
SELECT bm.id, b.name as beach_name, u.username as manager_name, bm.assigned_at 
FROM beach_managers bm 
JOIN beaches b ON bm.beach_id = b.id 
JOIN users u ON bm.user_id = u.id 
WHERE bm.is_active = 1;

SELECT '초기 데이터 삽입 완료!' as status;