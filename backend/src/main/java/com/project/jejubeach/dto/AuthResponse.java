package com.project.jejubeach.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 인증 응답")
public class AuthResponse {
    
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "사용자명", example = "admin")
    private String username;
    
    @Schema(description = "사용자 이메일", example = "admin@jejubeach.com")
    private String email;
    
    @Schema(description = "사용자 역할", example = "ADMIN")
    private String role;
}
