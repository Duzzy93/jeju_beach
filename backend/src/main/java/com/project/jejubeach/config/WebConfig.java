package com.project.jejubeach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // /videos/** URL 경로를 backend/videos/ 폴더에 매핑
    registry
            .addResourceHandler("/videos/**")
            .addResourceLocations("file:./videos/");
    
    // 동영상 파일 직접 접근 허용
    registry
            .addResourceHandler("/*.mp4")
            .addResourceLocations("file:./videos/");
  }

  @Bean
  public WebMvcConfigurer mvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true);
      }

      @Override
      public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
      }
    };
  }
}
