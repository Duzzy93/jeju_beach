package com.project.jejubeach.controller;

import com.project.jejubeach.dto.BeachRequest;
import com.project.jejubeach.entity.Beach;
import com.project.jejubeach.repository.BeachRepository;
import com.project.jejubeach.service.BeachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/beaches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "해변 관리", description = "해변 정보 CRUD 및 검색 API")
public class BeachController {
    
    private final BeachService beachService;
    private final BeachRepository beachRepository;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Beach API is working!");
    }

    @GetMapping
    @Operation(summary = "모든 해변 조회", description = "등록된 모든 해변 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Beach.class)))
    })
    public ResponseEntity<List<Beach>> getAllBeaches() {
        List<Beach> beaches = beachService.getAllBeaches();
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/active")
    @Operation(summary = "활성 해변 조회", description = "활성 상태인 해변만 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Beach.class)))
    })
    public ResponseEntity<List<Beach>> getActiveBeaches() {
        List<Beach> beaches = beachService.getActiveBeaches();
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/{id}")
    @Operation(summary = "해변 상세 조회", description = "ID로 특정 해변 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Beach.class))),
        @ApiResponse(responseCode = "404", description = "해변을 찾을 수 없음")
    })
    public ResponseEntity<Beach> getBeachById(
            @Parameter(description = "해변 ID", required = true)
            @PathVariable Long id) {
        return beachService.getBeachById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/region/{region}")
    @Operation(summary = "지역별 해변 조회", description = "특정 지역의 해변들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Beach.class)))
    })
    public ResponseEntity<List<Beach>> getBeachesByRegion(
            @Parameter(description = "지역명", required = true)
            @PathVariable String region) {
        List<Beach> beaches = beachService.getBeachesByRegion(region);
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/my-beaches")
    @Operation(summary = "내가 관리하는 해변 조회", description = "현재 로그인한 사용자가 관리할 수 있는 해변들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Beach.class))),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<List<Beach>> getMyBeaches() {
        String username = getCurrentUsername();
        List<Beach> beaches = beachService.getBeachesByManager(username);
        return ResponseEntity.ok(beaches);
    }

    @GetMapping("/debug")
    @Operation(summary = "데이터베이스 디버깅", description = "데이터베이스의 해변 데이터 상태를 확인합니다.")
    public ResponseEntity<Map<String, Object>> debugBeachData() {
        List<Beach> allBeaches = beachRepository.findAll();
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("totalBeaches", allBeaches.size());
        debugInfo.put("beaches", allBeaches.stream()
            .map(beach -> Map.of(
                "id", beach.getId(),
                "name", beach.getName(),
                "region", beach.getRegion(),
                "status", beach.getStatus()
            ))
            .collect(Collectors.toList()));
        return ResponseEntity.ok(debugInfo);
    }

    @GetMapping("/search")
    @Operation(summary = "해변명 검색", description = "해변명으로 해변을 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(schema = @Schema(implementation = Beach.class)))
    })
    public ResponseEntity<List<Beach>> searchBeachesByName(
            @Parameter(description = "검색할 해변명", required = true)
            @RequestParam String name) {
        List<Beach> beaches = beachService.searchBeachesByName(name);
        System.out.println("검색 요청: " + name + ", 결과: " + beaches.size() + "개");
        return ResponseEntity.ok(beaches);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "해변 생성", description = "새로운 해변을 등록합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = Beach.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    public ResponseEntity<Beach> createBeach(
            @Parameter(description = "해변 생성 정보", required = true)
            @Valid @RequestBody BeachRequest request) {
        String username = getCurrentUsername();
        Beach beach = beachService.createBeach(request, username);
        return ResponseEntity.ok(beach);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "해변 수정", description = "기존 해변 정보를 수정합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = Beach.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "해변을 찾을 수 없음")
    })
    public ResponseEntity<Beach> updateBeach(
            @Parameter(description = "해변 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "해변 수정 정보", required = true)
            @Valid @RequestBody BeachRequest request) {
        String username = getCurrentUsername();
        Beach beach = beachService.updateBeach(id, request, username);
        return ResponseEntity.ok(beach);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "해변 삭제", description = "해변을 삭제합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "해변을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteBeach(
            @Parameter(description = "해변 ID", required = true)
            @PathVariable Long id) {
        String username = getCurrentUsername();
        beachService.deleteBeach(id, username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "해변 상태 변경", description = "해변의 활성/비활성 상태를 토글합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 변경 성공",
            content = @Content(schema = @Schema(implementation = Beach.class))),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "해변을 찾을 수 없음")
    })
    public ResponseEntity<Beach> toggleBeachStatus(
            @Parameter(description = "해변 ID", required = true)
            @PathVariable Long id) {
        String username = getCurrentUsername();
        Beach beach = beachService.toggleBeachStatus(id, username);
        return ResponseEntity.ok(beach);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
