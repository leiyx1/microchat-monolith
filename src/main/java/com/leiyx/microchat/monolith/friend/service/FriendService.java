package com.leiyx.microchat.monolith.friend.service;

import com.leiyx.microchat.monolith.friend.entity.FriendRequest;
import com.leiyx.microchat.monolith.friend.entity.User;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    List<User> getFriends(UUID friendId);

    boolean isFriend(UUID userId, UUID friendId);

    boolean addFriend(UUID userId, UUID friendId);

    boolean deleteFriend(UUID userId, UUID friendId);

    List<FriendRequest> getFriendRequests(UUID userId);

    boolean acceptFriendRequest(UUID userId, UUID requestId);

    boolean declineFriendRequest(UUID userId, UUID requestId);

    List<User> getBlockedUsers(UUID userId);

    boolean isBlocked(UUID userId, UUID friendId);

    boolean blockUser(UUID userId, UUID friendId);

    boolean unblockUser(UUID userId, UUID friendId);
}
