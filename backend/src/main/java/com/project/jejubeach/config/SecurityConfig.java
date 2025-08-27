package com.project.jejubeach.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Swagger UI 및 OpenAPI 관련 경로 허용
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                
                // 인증 관련 API는 모두 허용
                .requestMatchers("/api/auth/**").permitAll()
                
                // 홈페이지 및 정적 리소스 (비로그인 사용자도 접근 가능)
                .requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll()
                
                // 챗봇 API (비로그인 사용자도 접근 가능)
                .requestMatchers("/api/chatbot/**").permitAll()
                
                // 비디오 관련 API (비로그인 사용자도 접근 가능)
                .requestMatchers("/api/videos/**").permitAll()
                .requestMatchers("/videos/**").permitAll()
                
                // WebSocket (비로그인 사용자도 접근 가능)
                .requestMatchers("/ws/**").permitAll()
                
                // 탐지 데이터 조회 (비로그인 사용자도 접근 가능)
                .requestMatchers(HttpMethod.GET, "/api/detections/**").permitAll()
                
                // AI 모델에서 보내는 탐지 데이터 저장 (비로그인 사용자도 접근 가능)
                .requestMatchers(HttpMethod.POST, "/api/detections").permitAll()
                
                // 해변 조회는 모든 사용자에게 허용
                .requestMatchers(HttpMethod.GET, "/api/beaches").permitAll()      // 해변 목록 조회
                .requestMatchers(HttpMethod.GET, "/api/beaches/**").permitAll()  // 해변 상세 조회
                
                // 사용자 관련 API (로그인 사용자만)
                .requestMatchers("/api/user/profile").authenticated()                          // 본인 프로필
                .requestMatchers("/api/user/role").authenticated()                             // 본인 역할
                
                // MANAGER 역할 이상 접근 가능한 API
                .requestMatchers("/api/user/beaches").hasAnyRole("MANAGER", "ADMIN")          // 관리 가능한 해변 조회
                
                // ADMIN 역할만 접근 가능한 API
                .requestMatchers(HttpMethod.POST, "/api/beaches").hasRole("ADMIN")            // 해변 생성
                .requestMatchers(HttpMethod.PUT, "/api/beaches/**").hasRole("ADMIN")          // 해변 수정
                .requestMatchers(HttpMethod.DELETE, "/api/beaches/**").hasRole("ADMIN")       // 해변 삭제
                .requestMatchers(HttpMethod.PATCH, "/api/beaches/**").hasRole("ADMIN")        // 해변 상태 변경
                .requestMatchers("/api/ai-model/**").hasRole("ADMIN")                         // AI 모델 제어
                
                // 기타 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
