package com.leiyx.microchat.monolith.friend.entity;


import com.leiyx.microchat.monolith.friend.entity.FriendRequestStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class FriendRequest {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private Instant createdDate;
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    public FriendRequest() {

    }

    public FriendRequest(UUID senderId, UUID receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdDate = Instant.now();
        this.status = FriendRequestStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }
}