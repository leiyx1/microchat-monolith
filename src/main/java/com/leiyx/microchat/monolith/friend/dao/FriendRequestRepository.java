package com.leiyx.microchat.monolith.friend.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.leiyx.microchat.monolith.friend.entity.FriendRequest;

public interface FriendRequestRepository extends Repository<FriendRequest, UUID> {
    FriendRequest getById(UUID id);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.senderId = :userId OR fr.receiverId = :userId ORDER BY fr.createdDate DESC")
    List<FriendRequest> getAllRequestsByUserId(@Param("userId") UUID userId);

    FriendRequest save(FriendRequest friendRequest);
}
