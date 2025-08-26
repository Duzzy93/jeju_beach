package com.project.jejubeach.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 각 계정별 비밀번호 해시 생성
        String adminPassword = "admin123";
        String manager1Password = "manager123";
        String manager2Password = "manager123";
        String manager3Password = "manager123";
        String user1Password = "user123";
        
        String adminHash = encoder.encode(adminPassword);
        String manager1Hash = encoder.encode(manager1Password);
        String manager2Hash = encoder.encode(manager2Password);
        String manager3Hash = encoder.encode(manager3Password);
        String user1Hash = encoder.encode(user1Password);
        
        System.out.println("=== 비밀번호 해시 생성 ===");
        System.out.println("admin123: " + adminHash);
        System.out.println("manager1 (manager123): " + manager1Hash);
        System.out.println("manager2 (manager123): " + manager2Hash);
        System.out.println("manager3 (manager123): " + manager3Hash);
        System.out.println("user1 (user123): " + user1Hash);
        System.out.println();
        
        // 해시값 검증
        System.out.println("=== 해시값 검증 ===");
        System.out.println("admin 검증: " + encoder.matches(adminPassword, adminHash));
        System.out.println("manager1 검증: " + encoder.matches(manager1Password, manager1Hash));
        System.out.println("manager2 검증: " + encoder.matches(manager2Password, manager2Hash));
        System.out.println("manager3 검증: " + encoder.matches(manager3Password, manager3Hash));
        System.out.println("user1 검증: " + encoder.matches(user1Password, user1Hash));
        System.out.println();
        
        // SQL INSERT 문 생성
        System.out.println("=== SQL INSERT 문 ===");
        System.out.println("-- 기존 계정들 삭제");
        System.out.println("DELETE FROM users WHERE username IN ('admin', 'manager1', 'manager2', 'manager3', 'user1');");
        System.out.println();
        System.out.println("-- 새로운 계정들 생성");
        System.out.println("INSERT INTO users (username, email, password_hash, role, is_active, created_at, updated_at) VALUES ");
        System.out.println("('admin', 'admin@jejubeach.com', '" + adminHash + "', 'ADMIN', 1, NOW(), NOW()),");
        System.out.println("('manager1', 'manager1@jejubeach.com', '" + manager1Hash + "', 'MANAGER', 1, NOW(), NOW()),");
        System.out.println("('manager2', 'manager2@jejubeach.com', '" + manager2Hash + "', 'MANAGER', 1, NOW(), NOW()),");
        System.out.println("('manager3', 'manager3@jejubeach.com', '" + manager3Hash + "', 'MANAGER', 1, NOW(), NOW()),");
        System.out.println("('user1', 'user1@jejubeach.com', '" + user1Hash + "', 'USER', 1, NOW(), NOW());");
        System.out.println();
        
        // 데이터베이스 업데이트용 SQL
        System.out.println("=== 데이터베이스 업데이트용 SQL ===");
        System.out.println("-- admin 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("-- manager1 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + manager1Hash + "' WHERE username = 'manager1';");
        System.out.println("-- manager2 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + manager2Hash + "' WHERE username = 'manager2';");
        System.out.println("-- manager3 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + manager3Hash + "' WHERE username = 'manager3';");
        System.out.println("-- user1 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + user1Hash + "' WHERE username = 'user1';");
        System.out.println();
        
        // 해변-매니저 매핑 SQL
        System.out.println("=== 해변-매니저 매핑 SQL ===");
        System.out.println("-- 해변-매니저 매핑 삭제");
        System.out.println("DELETE FROM beach_managers;");
        System.out.println();
        System.out.println("-- 해변-매니저 매핑 생성");
        System.out.println("INSERT INTO beach_managers (beach_id, user_id, assigned_at, is_active) VALUES ");
        System.out.println("(1, (SELECT id FROM users WHERE username = 'manager1'), NOW(), 1),");
        System.out.println("(2, (SELECT id FROM users WHERE username = 'manager2'), NOW(), 1),");
        System.out.println("(3, (SELECT id FROM users WHERE username = 'manager3'), NOW(), 1);");
        System.out.println();
        
        // 계정 정보 요약
        System.out.println("=== 계정 정보 요약 ===");
        System.out.println("ADMIN 계정:");
        System.out.println("  - 사용자명: admin");
        System.out.println("  - 비밀번호: admin123");
        System.out.println("  - 권한: 모든 해변 관리");
        System.out.println();
        System.out.println("MANAGER 계정들:");
        System.out.println("  - manager1 (manager123): 함덕해변 관리");
        System.out.println("  - manager2 (manager123): 이호테우해변 관리");
        System.out.println("  - manager3 (manager123): 월정리해변 관리");
        System.out.println();
        System.out.println("USER 계정:");
        System.out.println("  - user1 (user123): 모든 해변 조회만 가능");
    }
}
