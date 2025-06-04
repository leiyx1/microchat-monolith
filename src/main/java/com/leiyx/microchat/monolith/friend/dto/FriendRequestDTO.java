package com.leiyx.microchat.monolith.friend.dto;

import java.util.UUID;

import com.leiyx.microchat.monolith.friend.entity.FriendRequest;
import com.leiyx.microchat.monolith.friend.entity.User;

public class FriendRequestDTO {
    private UUID id;
    private String senderUsername;
    private String senderFullName;
    private String receiverUsername;
    private String receiverFullName;
    private String status;
    private String createdDate;

    public FriendRequestDTO() {
    }

    public static FriendRequestDTO fromRequest(FriendRequest request, User sender, User receiver) {
        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(request.getId());
        dto.setSenderUsername(sender.getUsername());
        dto.setSenderFullName(sender.getFullName());
        dto.setReceiverUsername(receiver.getUsername());
        dto.setReceiverFullName(receiver.getFullName());
        dto.setStatus(request.getStatus().toString());
        dto.setCreatedDate(request.getCreatedDate().toString());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
} 