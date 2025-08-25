package com.project.jejubeach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeachRequest {
    
    @NotBlank(message = "해변명은 필수입니다")
    private String name;
    
    private String region;
    
    @NotNull(message = "위도는 필수입니다")
    private BigDecimal latitude;
    
    @NotNull(message = "경도는 필수입니다")
    private BigDecimal longitude;
    
    private String description;
}
