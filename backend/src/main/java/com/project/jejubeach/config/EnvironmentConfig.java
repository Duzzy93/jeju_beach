package com.project.jejubeach.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class EnvironmentConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    
    // Environment는 사용하지 않으므로 제거
    
    @PostConstruct
    public void loadEnvironmentVariables() {
        logger.info("🔧 환경 변수 로딩 시작...");
        
        // .env 파일에서 환경 변수 로드
        loadDotEnvFile();
        
        // OpenAI API 키 상태 확인
        String openaiApiKey = System.getProperty("OPENAI_API_KEY");
        if (openaiApiKey != null && !openaiApiKey.trim().isEmpty()) {
            logger.info("✅ OPENAI_API_KEY 시스템 프로퍼티 로드됨: {}...", 
                openaiApiKey.substring(0, Math.min(10, openaiApiKey.length())));
        } else {
            logger.warn("⚠️ OPENAI_API_KEY 시스템 프로퍼티가 설정되지 않음");
        }
        
        logger.info("🔧 환경 변수 로딩 완료");
    }
    
    private void loadDotEnvFile() {
        try {
            // 프로젝트 루트 디렉토리에서 .env 파일 찾기
            File envFile = new File(".env");
            if (!envFile.exists()) {
                // 백엔드 디렉토리에서 .env 파일 찾기
                envFile = new File("backend/.env");
            }
            if (!envFile.exists()) {
                // 상위 디렉토리에서 .env 파일 찾기
                envFile = new File("../.env");
            }
            
            if (envFile.exists()) {
                logger.info("📁 .env 파일 발견: {}", envFile.getAbsolutePath());
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(envFile)) {
                    props.load(fis);
                    
                                         // .env 파일의 내용을 시스템 프로퍼티로 설정
                     for (String key : props.stringPropertyNames()) {
                         String value = props.getProperty(key);
                         System.setProperty(key, value);
                         logger.debug("🔑 시스템 프로퍼티 설정: {} = {}", key, 
                             key.contains("KEY") ? "***" : value);
                     }
                    logger.info("✅ .env 파일에서 {}개의 환경 변수 로드됨", props.size());
                }
            } else {
                logger.warn("⚠️ .env 파일을 찾을 수 없습니다.");
                logger.info("📁 검색 경로:");
                logger.info("   - 현재 디렉토리: {}", new File(".").getAbsolutePath());
                logger.info("   - backend 디렉토리: {}", new File("backend").getAbsolutePath());
                logger.info("   - 상위 디렉토리: {}", new File("..").getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("❌ .env 파일 로드 중 오류 발생", e);
        }
    }
    
    // 환경 변수 로딩만 담당하므로 Bean 제거
    // ChatbotService에서 직접 System.getenv() 사용
}
