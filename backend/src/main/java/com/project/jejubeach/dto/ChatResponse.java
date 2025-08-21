package com.project.jejubeach.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatResponse {
  // Getters and Setters
  private String message;
    private String messageId;
    private long timestamp;

    public ChatResponse() {}

    public ChatResponse(String message) {
        this.message = message;
        this.messageId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

}
