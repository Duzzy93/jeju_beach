package com.project.jejubeach.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("제주 해변 프로젝트 API")
                        .description("제주도의 해변을 관리하고 CCTV를 통한 혼잡도 분석 및 안전 모니터링을 제공하는 웹 애플리케이션 API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("제주 해변 프로젝트 팀")
                                .email("admin@jejubeach.com")
                                .url("https://github.com/jejubeach"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://api.jejubeach.com")
                                .description("프로덕션 서버")
                ));
    }
}
