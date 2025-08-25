package com.project.jejubeach.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 인증 요청")
public class AuthRequest {
    
    @Schema(description = "사용자명", example = "admin", required = true)
    @NotBlank(message = "사용자명은 필수입니다")
    private String username;
    
    @Schema(description = "비밀번호", example = "admin123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
