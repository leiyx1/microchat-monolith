package com.leiyx.microchat.monolith.friend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.util.UUID;

class BlockId implements Serializable {
    private int userId;
    private int blockedUserId;
}

@Entity
@IdClass(BlockId.class)
public class Block {
    @Id
    UUID userId;
    @Id
    UUID blockedUserId;

    public Block() {
    }

    public Block(UUID userId, UUID blockedUserId) {
        this.userId = userId;
        this.blockedUserId = blockedUserId;
    }
}
