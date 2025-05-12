package com.leiyx.microchat.monolith.friend.dao;

import com.leiyx.microchat.monolith.friend.entity.Friendship;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface FriendshipRepository extends Repository<Friendship, String> {

    boolean existsByUserIdAndFriendId(UUID userId, UUID friendId);

    List<Friendship> getFriendshipByUserId(UUID userId);

    Long deleteByUserIdAndFriendId(UUID userId, UUID friendId);

    void save(Friendship friendship);

}
