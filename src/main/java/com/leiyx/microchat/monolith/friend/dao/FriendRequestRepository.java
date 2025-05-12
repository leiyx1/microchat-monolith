package com.leiyx.microchat.monolith.friend.dao;

import com.leiyx.microchat.monolith.friend.entity.FriendRequest;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface FriendRequestRepository extends Repository<FriendRequest, UUID> {
    FriendRequest getById(UUID id);
    List<FriendRequest> getByReceiverId(UUID id);

    FriendRequest save(FriendRequest friendRequest);
}
