package com.project.jejubeach.dto;

public class ChatResponse {
    private String message;
    private String messageId;
    private long timestamp;

    public ChatResponse() {}

    public ChatResponse(String message) {
        this.message = message;
        this.messageId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
