package com.project.jejubeach.controller;

import com.project.jejubeach.entity.Detection;
import com.project.jejubeach.service.DetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/detections")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "탐지 데이터", description = "인원 수 및 쓰러진 사람 수 탐지 데이터 API")
public class DetectionController {
    
    private final DetectionService service;
    
    @PostMapping
    @Operation(summary = "탐지 데이터 저장", description = "새로운 탐지 데이터를 저장하고 최신 10개만 유지합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<Detection> create(@RequestBody DetectionCreateReq req) {
        Detection saved = service.saveAndTrim(req.personCount(), req.fallenCount(), req.source());
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/latest")
    @Operation(summary = "최신 탐지 데이터 조회", description = "가장 최근에 저장된 탐지 데이터를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "데이터 없음")
    })
    public ResponseEntity<Detection> latest() {
        Detection d = service.getLatest();
        return d == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(d);
    }
    
    @GetMapping("/latest10")
    @Operation(summary = "최신 10건 탐지 데이터 조회", description = "최근 10건의 탐지 데이터를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public List<Detection> latest10() {
        return service.getLatest10();
    }

    @GetMapping("/beach/{beachName}/latest")
    @Operation(summary = "특정 해변의 최신 탐지 데이터 조회", description = "지정된 해변의 가장 최근 탐지 데이터를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "데이터 없음")
    })
    public ResponseEntity<Detection> getLatestByBeach(@PathVariable String beachName) {
        Detection d = service.getLatestByBeach(beachName);
        return d == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(d);
    }
    
    public record DetectionCreateReq(int personCount, int fallenCount, String source) {}
}
