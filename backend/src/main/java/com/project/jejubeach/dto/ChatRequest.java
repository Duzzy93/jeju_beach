package com.project.jejubeach.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ChatRequest {
  // Getters and Setters
  private String message;
    private List<ChatMessage> conversationHistory;

    public ChatRequest() {}

    public ChatRequest(String message, List<ChatMessage> conversationHistory) {
        this.message = message;
        this.conversationHistory = conversationHistory;
    }

}
