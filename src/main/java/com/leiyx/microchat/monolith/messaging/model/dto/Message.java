package com.leiyx.microchat.monolith.messaging.model.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private Long messageId;
    private String type;
    private String sender;
    private String receiver;
    private String content;
    private Instant createdAt = null;
    private String role;

    public Message() {}

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", senderUsername='" + sender + '\'' +
                ", receiverUsername='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + createdAt +
                '}';
    }
}
