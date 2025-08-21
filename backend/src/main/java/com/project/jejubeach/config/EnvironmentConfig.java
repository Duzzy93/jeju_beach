package com.project.jejubeach.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class EnvironmentConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void loadEnvFile() {
        System.out.println("🔍 EnvironmentConfig 초기화 시작...");
        System.out.println("📁 현재 작업 디렉토리: " + System.getProperty("user.dir"));
        
        // 여러 경로에서 .env 파일 찾기
        File[] possiblePaths = {
            new File(".env"),                                    // 현재 디렉토리
            new File("backend/.env"),                           // backend 하위 디렉토리
            new File("../backend/.env"),                        // 상위 디렉토리의 backend
            new File(System.getProperty("user.dir") + "/.env"), // 절대 경로
            new File(System.getProperty("user.dir") + "/backend/.env") // 절대 경로 + backend
        };
        
        File envFile = null;
        for (File path : possiblePaths) {
            System.out.println("🔍 .env 파일 검색: " + path.getAbsolutePath());
            if (path.exists()) {
                envFile = path;
                System.out.println("✅ .env 파일 발견: " + path.getAbsolutePath());
                break;
            }
        }
        
        if (envFile != null) {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(envFile));
                
                System.out.println("📄 .env 파일 내용:");
                props.forEach((key, value) -> {
                    String keyStr = key.toString();
                    String valueStr = value.toString();
                    
                    // API 키는 보안을 위해 일부만 출력
                    if (keyStr.equals("OPENAI_API_KEY")) {
                        String maskedValue = valueStr.length() > 10 ? 
                            valueStr.substring(0, 10) + "..." : valueStr;
                        System.out.println("  " + keyStr + "=" + maskedValue);
                    } else {
                        System.out.println("  " + keyStr + "=" + valueStr);
                    }
                    
                    // 시스템 프로퍼티 설정
                    if (System.getProperty(keyStr) == null) {
                        System.setProperty(keyStr, valueStr);
                        System.out.println("✅ 시스템 프로퍼티 설정: " + keyStr);
                    } else {
                        System.out.println("⚠️ 시스템 프로퍼티 이미 존재: " + keyStr);
                    }
                });
                
                // API 키 상태 확인
                String apiKey = System.getProperty("OPENAI_API_KEY");
                if (apiKey != null && !apiKey.trim().isEmpty()) {
                    System.out.println("🔑 OpenAI API 키 상태: 설정됨");
                    System.out.println("🔑 API 키 확인: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                } else {
                    System.out.println("❌ OpenAI API 키 상태: 설정되지 않음");
                }
                
            } catch (IOException e) {
                System.err.println("❌ .env 파일 로딩 실패: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ .env 파일을 찾을 수 없습니다.");
            System.err.println("💡 다음 경로 중 하나에 .env 파일을 생성해주세요:");
            for (File path : possiblePaths) {
                System.err.println("   - " + path.getAbsolutePath());
            }
        }
        
        System.out.println("🔍 EnvironmentConfig 초기화 완료");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @Bean
    public String openaiApiKey() {
        System.out.println("🔑 openaiApiKey Bean 생성 시작...");
        
        // 여러 소스에서 API 키를 가져오는 로직
        String apiKey = getApiKeyFromMultipleSources();
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("❌ OPENAI_API_KEY가 설정되지 않았습니다.");
            System.err.println("💡 .env 파일을 생성하고 OPENAI_API_KEY를 설정해주세요.");
            return null;
        } else {
            System.out.println("✅ OpenAI API 키 로딩 성공");
            System.out.println("🔑 API 키 확인: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
            return apiKey;
        }
    }

    private String getApiKeyFromMultipleSources() {
        System.out.println("🔍 API 키 소스 검색 시작...");
        
        // 1. 시스템 프로퍼티 확인
        String systemProp = System.getProperty("OPENAI_API_KEY");
        if (systemProp != null && !systemProp.trim().isEmpty()) {
            System.out.println("✅ 시스템 프로퍼티에서 API 키 발견");
            return systemProp;
        }

        // 2. 환경 변수 확인
        String envVar = System.getenv("OPENAI_API_KEY");
        if (envVar != null && !envVar.trim().isEmpty()) {
            System.out.println("✅ 환경 변수에서 API 키 발견");
            return envVar;
        }

        // 3. .env 파일에서 직접 읽기
        try {
            File[] possiblePaths = {
                new File(".env"),
                new File("backend/.env"),
                new File("../backend/.env"),
                new File(System.getProperty("user.dir") + "/.env"),
                new File(System.getProperty("user.dir") + "/backend/.env")
            };
            
            for (File envFile : possiblePaths) {
                if (envFile.exists()) {
                    Properties props = new Properties();
                    props.load(new FileInputStream(envFile));
                    String fileKey = props.getProperty("OPENAI_API_KEY");
                    if (fileKey != null && !fileKey.trim().isEmpty()) {
                        System.out.println("✅ .env 파일에서 API 키 발견: " + envFile.getAbsolutePath());
                        return fileKey;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ .env 파일 읽기 실패: " + e.getMessage());
        }

        System.out.println("❌ 모든 소스에서 API 키를 찾을 수 없음");
        return null;
    }
}
