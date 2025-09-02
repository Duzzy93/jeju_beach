package com.project.jejubeach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 정적 리소스 처리 (프론트엔드 빌드 파일) - 최우선 처리
    registry
            .addResourceHandler("/assets/**", "/favicon.ico", "/manifest.json", "/robots.txt")
            .addResourceLocations("classpath:/static/assets/", "classpath:/static/")
            .setCachePeriod(3600); // 1시간 캐시
    
    // /videos/** URL 경로를 classpath:/videos/ 폴더에 매핑 (JAR 내부)
    registry
            .addResourceHandler("/videos/**")
            .addResourceLocations("classpath:/videos/");
    
    // 동영상 파일 직접 접근 허용
    registry
            .addResourceHandler("/*.mp4")
            .addResourceLocations("classpath:/videos/");
    
    // 기타 정적 리소스 처리 (fallback)
    registry
            .addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600); // 1시간 캐시
  }

  @Bean
  public WebMvcConfigurer mvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true);
        // MIME 타입 설정
        configurer.mediaType("css", MediaType.valueOf("text/css"));
        configurer.mediaType("js", MediaType.valueOf("application/javascript"));
        configurer.mediaType("ico", MediaType.valueOf("image/x-icon"));
        configurer.mediaType("json", MediaType.valueOf("application/json"));
      }

      @Override
      public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
      }
    };
  }
}
