package com.project.jejubeach.service;

import com.project.jejubeach.dto.ChatMessage;
import com.project.jejubeach.dto.ChatRequest;
import com.project.jejubeach.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openaiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String openaiApiKey;

    // 해변 정보 데이터
    private static final String BEACH_SYSTEM_PROMPT = """
        당신은 제주도 해변 전문 가이드입니다. 
        다음 정보를 바탕으로 사용자의 질문에 친절하고 정확하게 답변해주세요:

        해변 정보:
        - 함덕해변: 제주도 동부의 대표적인 해변, 맑은 바다와 백사장
        - 이호해변: 공항 근처에 위치해 접근성이 좋은 평화로운 해변  
        - 월정리해변: 맑은 바다와 카페 거리로 유명한 해변

        혼잡도 정보:
        - 낮음: 5명 미만
        - 중간: 5-15명
        - 높음: 15명 이상

        항상 한국어로 답변하고, 제주도 해변에 대한 구체적이고 유용한 정보를 제공해주세요.
        사용자가 해변 선택에 어려움을 겪고 있다면 개인적 취향을 물어보고 맞춤형 추천을 해주세요.
        """;

    @Autowired
    public ChatbotService(String openaiApiKey) {
        this.openaiApiKey = openaiApiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
        // API 키 상태 로깅
        if (this.openaiApiKey != null && !this.openaiApiKey.trim().isEmpty()) {
            logger.info("✅ ChatbotService 초기화 완료 - API 키 설정됨");
        } else {
            logger.error("❌ ChatbotService 초기화 실패 - API 키가 설정되지 않음");
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

            // OpenAI API 요청 구성
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            // 메시지 배열 구성
            ArrayNode messages = objectMapper.createArrayNode();
            
            // 시스템 메시지 추가
            ObjectNode systemMessage = objectMapper.createObjectNode();
            systemMessage.put("role", "system");
            systemMessage.put("content", BEACH_SYSTEM_PROMPT);
            messages.add(systemMessage);

            // 대화 히스토리 추가 (최근 5개만)
            List<ChatMessage> recentHistory = request.getConversationHistory();
            if (recentHistory != null && !recentHistory.isEmpty()) {
                int startIndex = Math.max(0, recentHistory.size() - 5);
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
            questions.add(new ChatMessage("user", question));
        }

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
