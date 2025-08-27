package com.project.jejubeach.controller;

import com.project.jejubeach.service.AIModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-model")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI 모델 제어", description = "AI 모델 상태 확인 및 제어 API")
public class AIModelController {

    private final AIModelService aiModelService;

    @GetMapping("/status")
    @Operation(summary = "AI 모델 상태 확인", description = "현재 AI 모델의 실행 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 확인 성공")
    })
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", aiModelService.getAIModelStatus());
        status.put("running", aiModelService.isAIModelRunning());
        status.put("timestamp", System.currentTimeMillis());
        status.put("runningTime", aiModelService.getRunningTime());
        status.put("analysisCount", aiModelService.getAnalysisCount());
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/info")
    @Operation(summary = "AI 모델 정보", description = "AI 모델의 상세 정보를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정보 확인 성공")
    })
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("pythonPath", "python");
        info.put("workingDir", "../beach_project");
        info.put("scriptPath", "simple_detection_windows.py");
        info.put("analysisInterval", 30);
        info.put("enabled", true);
        info.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(info);
    }

    // 공통 응답 생성 메서드
    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "AI 모델 시작", description = "AI 모델을 수동으로 시작합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "시작 성공"),
        @ApiResponse(responseCode = "400", description = "이미 실행 중이거나 시작 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    public ResponseEntity<Map<String, Object>> startAIModel() {
        if (aiModelService.isAIModelRunning()) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "AI 모델이 이미 실행 중입니다.")
            );
        }
        
        try {
            aiModelService.startAIModelManually();
            return ResponseEntity.ok(
                createResponse(true, "AI 모델 시작 요청이 완료되었습니다.")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "AI 모델 시작 중 오류 발생: " + e.getMessage())
            );
        }
    }

    @PostMapping("/stop")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "AI 모델 중지", description = "실행 중인 AI 모델을 중지합니다. (최고관리자만)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "중지 성공"),
        @ApiResponse(responseCode = "400", description = "실행 중인 AI 모델이 없음"),
        @ApiResponse(responseCode = "403", description = "권한 부족")
    })
    public ResponseEntity<Map<String, Object>> stopAIModel() {
        if (!aiModelService.isAIModelRunning()) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "실행 중인 AI 모델이 없습니다.")
            );
        }
        
        try {
            aiModelService.stopAIModel();
            return ResponseEntity.ok(
                createResponse(true, "AI 모델 중지 요청이 완료되었습니다.")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                createResponse(false, "AI 모델 중지 중 오류 발생: " + e.getMessage())
            );
        }
    }

    @PostMapping("/restart")
    @Operation(summary = "AI 모델 재시작", description = "AI 모델을 중지하고 다시 시작합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "재시작 성공"),
        @ApiResponse(responseCode = "500", description = "재시작 실패")
    })
    public ResponseEntity<Map<String, Object>> restartAIModel() {
        try {
            if (aiModelService.isAIModelRunning()) {
                aiModelService.stopAIModel();
                Thread.sleep(2000);
            }
            
            aiModelService.startAIModelManually();
            return ResponseEntity.ok(
                createResponse(true, "AI 모델 재시작이 완료되었습니다.")
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                createResponse(false, "AI 모델 재시작 중 오류 발생: " + e.getMessage())
            );
        }
    }
}
