package com.project.jejubeach.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.jejubeach.dto.ChatMessage;
import com.project.jejubeach.dto.ChatRequest;
import com.project.jejubeach.dto.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    @Value("${OPENAI_API_KEY:}")
    private String openaiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 해변 정보 데이터 (토큰 사용량 최적화)
    private static final String BEACH_SYSTEM_PROMPT = """
        제주도 해변 가이드. 함덕해변(동부, 맑은 바다), 이호해변(서부, 공항근처), 월정리해변(동부, 카페거리). 
        혼잡도: 낮음(5명미만), 중간(5-15명), 높음(15명이상). 
        한국어로 간단명료하게 답변. 100자 이내로 답변.
        """;

    @Autowired
    public ChatbotService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // API 키 상태 로깅 및 상세 정보
        if (this.openaiApiKey != null && !this.openaiApiKey.trim().isEmpty() && 
            !this.openaiApiKey.equals("your_openai_api_key_here")) {
            logger.info("✅ ChatbotService 초기화 완료 - 환경 변수에서 API 키 로드됨");
            logger.debug("API 키 확인: {}...", this.openaiApiKey.substring(0, Math.min(10, this.openaiApiKey.length())));
        } else {
            logger.warn("⚠️ ChatbotService 초기화 완료 - API 키가 설정되지 않음 (기본 정보만 제공)");
            logger.info("환경 변수 OPENAI_API_KEY를 설정해주세요.");
            logger.info("현재 API 키 값: {}", this.openaiApiKey);
            this.openaiApiKey = null;
        }
    }

    public ChatResponse getChatResponse(ChatRequest request) {
        // API 키 검증
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            logger.error("OpenAI API 키가 설정되지 않았습니다.");
            return new ChatResponse("죄송합니다. OpenAI API 키가 설정되지 않았습니다. 관리자에게 문의해주세요.");
        }

        try {
            logger.info("OpenAI API 호출 시작 - 메시지: {}", request.getMessage());
            logger.debug("API 키 확인: {}", openaiApiKey.substring(0, Math.min(10, openaiApiKey.length())) + "...");

            // OpenAI API 요청 구성 (토큰 사용량 최적화)
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 150);  // 1000 → 150으로 대폭 감소
            requestBody.put("temperature", 0.3);  // 0.7 → 0.3으로 감소 (더 일관된 응답)

            // 메시지 배열 구성
            ArrayNode messages = objectMapper.createArrayNode();
            
            // 시스템 메시지 추가
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", BEACH_SYSTEM_PROMPT);
            messages.add(systemMessage);

            // 대화 히스토리 추가 (최근 2개만 - 토큰 사용량 최적화)
            List<ChatMessage> recentHistory = request.getConversationHistory();
            if (recentHistory != null && !recentHistory.isEmpty()) {
                int startIndex = Math.max(0, recentHistory.size() - 2);  // 5개 → 2개로 감소
                for (int i = startIndex; i < recentHistory.size(); i++) {
                    ChatMessage msg = recentHistory.get(i);
                    ObjectNode historyMessage = objectMapper.createObjectNode();
                    historyMessage.put("role", msg.getRole());
                    historyMessage.put("content", msg.getContent());
                    messages.add(historyMessage);
                }
            }

            // 사용자 메시지 추가
            ObjectNode userMessage = objectMapper.createObjectNode();
            userMessage.put("role", "user");
            userMessage.put("content", request.getMessage());
            messages.add(userMessage);

            requestBody.set("messages", messages);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            logger.debug("API 요청 URL: {}", openaiApiUrl);
            logger.debug("API 요청 헤더: {}", headers);

            // API 요청 전송
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            String response = restTemplate.postForObject(openaiApiUrl, entity, String.class);

            logger.debug("API 응답: {}", response);

            // 응답 파싱
            ObjectNode responseJson = objectMapper.readValue(response, ObjectNode.class);
            String aiMessage = responseJson
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();

            logger.info("OpenAI API 호출 성공");
            return new ChatResponse(aiMessage);

        } catch (HttpClientErrorException e) {
            logger.error("OpenAI API HTTP 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return new ChatResponse("OpenAI API 인증 오류입니다. API 키를 확인해주세요.");
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                return new ChatResponse("API 호출 한도를 초과했습니다. 잠시 후 다시 시도해주세요.");
            } else {
                return new ChatResponse("OpenAI API 오류가 발생했습니다: " + e.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("OpenAI API 호출 중 예외 발생", e);
            return new ChatResponse("죄송합니다. 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public List<ChatMessage> getQuickQuestions() {
        List<ChatMessage> questions = new ArrayList<>();
        String[] quickQuestions = {
            "함덕해변 어때요?",
            "이호해변 혼잡도는?",
            "월정리해변 추천해줘",
            "공항 근처 해변 추천",
            "카페가 많은 해변은?",
            "혼잡도가 낮은 해변은?",
            "제주도 대표 해변은?",
            "해변 선택 도와줘"
        };

        for (String question : quickQuestions) {
            ChatMessage chatMessage = new ChatMessage("user", question);
            questions.add(chatMessage);
        }

        logger.info("빠른 질문 목록 생성 완료: {}개", questions.size());
        return questions;
    }

    // API 키 상태 확인 메서드
    public String getApiKeyStatus() {
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            return "API 키가 설정되지 않음";
        } else if (openaiApiKey.equals("your_openai_api_key_here")) {
            return "기본값 API 키 (실제 키로 변경 필요)";
        } else {
            return "API 키 설정됨 (" + openaiApiKey.substring(0, Math.min(10, openaiApiKey.length())) + "...)";
        }
    }
}
