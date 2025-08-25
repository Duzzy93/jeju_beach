package com.project.jejubeach.controller;


import com.project.jejubeach.dto.BeachVideo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")  // Vue.js 개발 서버 주소 맞춰주세요
@Tag(name = "해변 동영상", description = "해변 CCTV 동영상 정보 제공 API")
public class BeachVideoController {

  @GetMapping("/api/videos")
  @Operation(summary = "동영상 목록 조회", description = "등록된 모든 해변 동영상 정보를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = BeachVideo.class)))
  })
  public List<BeachVideo> getVideos() {
    return List.of(
            new BeachVideo(
                    "hamduck",
                    "hamduck_beach.mp4",
                    "함덕 해수욕장",
                    "제주도 대표적인 해변 중 하나인 함덕 해수욕장입니다.",
                    "/videos/hamduck_beach.mp4"
            ),
            new BeachVideo(
                    "iho",
                    "iho_beach.mp4",
                    "이호 해수욕장",
                    "이호 해수욕장은 공항 근처에 위치해 있어 접근성이 좋습니다.",
                    "/videos/iho_beach.mp4"
            ),
            new BeachVideo(
                    "walljeonglee",
                    "walljeonglee_beach.mp4",
                    "월정리 해수욕장",
                    "월정리 해수욕장은 맑은 바다와 카페 거리로 유명합니다.",
                    "/videos/walljeonglee_beach.mp4"
            )
    );
  }
}
