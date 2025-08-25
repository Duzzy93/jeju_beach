package com.project.jejubeach.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 각 계정별 비밀번호 해시 생성
        String adminPassword = "admin123";
        String managerPassword = "manager123";
        String userPassword = "user123";
        
        String adminHash = encoder.encode(adminPassword);
        String managerHash = encoder.encode(managerPassword);
        String userHash = encoder.encode(userPassword);
        
        System.out.println("=== 비밀번호 해시 생성 ===");
        System.out.println("admin123: " + adminHash);
        System.out.println("manager123: " + managerHash);
        System.out.println("user123: " + userHash);
        System.out.println();
        
        // 해시값 검증
        System.out.println("=== 해시값 검증 ===");
        System.out.println("admin 검증: " + encoder.matches(adminPassword, adminHash));
        System.out.println("manager 검증: " + encoder.matches(managerPassword, managerHash));
        System.out.println("user 검증: " + encoder.matches(userPassword, userHash));
        System.out.println();
        
        // SQL INSERT 문 생성
        System.out.println("=== SQL INSERT 문 ===");
        System.out.println("-- 기존 계정들 삭제");
        System.out.println("DELETE FROM users WHERE username IN ('admin', 'manager', 'user1');");
        System.out.println();
        System.out.println("-- 새로운 계정들 생성");
        System.out.println("INSERT INTO users (username, email, password_hash, role, is_active, created_at, updated_at) VALUES ");
        System.out.println("('admin', 'admin@jejubeach.com', '" + adminHash + "', 'ADMIN', 1, NOW(), NOW()),");
        System.out.println("('manager', 'manager@jejubeach.com', '" + managerHash + "', 'MANAGER', 1, NOW(), NOW()),");
        System.out.println("('user1', 'user1@jejubeach.com', '" + userHash + "', 'USER', 1, NOW(), NOW());");
        System.out.println();
        
        // 데이터베이스 업데이트용 SQL
        System.out.println("=== 데이터베이스 업데이트용 SQL ===");
        System.out.println("-- admin 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println("-- manager 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + managerHash + "' WHERE username = 'manager';");
        System.out.println("-- user1 계정 업데이트");
        System.out.println("UPDATE users SET password_hash = '" + userHash + "' WHERE username = 'user1';");
    }
}
