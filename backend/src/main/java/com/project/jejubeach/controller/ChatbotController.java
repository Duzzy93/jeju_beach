package com.project.jejubeach.controller;

import com.project.jejubeach.dto.ChatMessage;
import com.project.jejubeach.dto.ChatRequest;
import com.project.jejubeach.dto.ChatResponse;
import com.project.jejubeach.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "AI 챗봇", description = "OpenAI 기반 챗봇 서비스 API")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    @Operation(summary = "챗봇 대화", description = "사용자 메시지에 대한 AI 챗봇 응답을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "응답 생성 성공",
            content = @Content(schema = @Schema(implementation = ChatResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 API 오류")
    })
    public ResponseEntity<ChatResponse> chat(
            @Parameter(description = "챗봇 대화 요청", required = true)
            @RequestBody ChatRequest request) {
        try {
            ChatResponse response = chatbotService.getChatResponse(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ChatResponse("오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/quick-questions")
    @Operation(summary = "빠른 질문 목록", description = "자주 묻는 질문들의 목록을 제공합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ChatMessage.class))),
        @ApiResponse(responseCode = "400", description = "조회 실패")
    })
    public ResponseEntity<List<ChatMessage>> getQuickQuestions() {
        try {
            logger.info("빠른 질문 목록 요청 받음");
            List<ChatMessage> questions = chatbotService.getQuickQuestions();
            logger.info("빠른 질문 목록 반환: {}개", questions.size());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("빠른 질문 목록 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status")
    @Operation(summary = "API 키 상태 확인", description = "OpenAI API 키의 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 확인 성공"),
        @ApiResponse(responseCode = "400", description = "상태 확인 실패")
    })
    public ResponseEntity<String> getApiKeyStatus() {
        try {
            String status = chatbotService.getApiKeyStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("상태 확인 실패: " + e.getMessage());
        }
    }

    @PostMapping("/test")
    @Operation(summary = "연결 테스트", description = "챗봇 서비스의 연결 상태를 테스트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "연결 성공")
    })
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("챗봇 서비스가 정상적으로 작동하고 있습니다!");
    }
}
