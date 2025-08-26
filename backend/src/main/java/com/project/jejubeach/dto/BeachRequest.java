package com.project.jejubeach.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해변 생성/수정 요청")
public class BeachRequest {
    
    @Schema(description = "해변명", example = "함덕 해수욕장", required = true)
    @NotBlank(message = "해변명은 필수입니다")
    private String name;
    
    @Schema(description = "지역", example = "제주시 조천읍")
    private String region;
    
    @Schema(description = "위도", example = "33.5589")
    private BigDecimal latitude;
    
    @Schema(description = "경도", example = "126.7944")
    private BigDecimal longitude;
    
    @Schema(description = "해변 설명", example = "제주도 대표적인 해변 중 하나입니다.")
    private String description;
    
    @Schema(description = "동영상 경로", example = "/videos/hamduck_beach.mp4")
    private String videoPath;
}
