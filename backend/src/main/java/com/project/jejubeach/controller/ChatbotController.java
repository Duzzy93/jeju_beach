package com.project.jejubeach.controller;

import com.project.jejubeach.dto.ChatMessage;
import com.project.jejubeach.dto.ChatRequest;
import com.project.jejubeach.dto.ChatResponse;
import com.project.jejubeach.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            ChatResponse response = chatbotService.getChatResponse(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ChatResponse("오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/quick-questions")
    public ResponseEntity<List<ChatMessage>> getQuickQuestions() {
        try {
            List<ChatMessage> questions = chatbotService.getQuickQuestions();
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getApiKeyStatus() {
        try {
            String status = chatbotService.getApiKeyStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("상태 확인 실패: " + e.getMessage());
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("챗봇 서비스가 정상적으로 작동하고 있습니다!");
    }
}
