package com.leiyx.microchat.monolith.friend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leiyx.microchat.monolith.auth.service.AuthService;
import com.leiyx.microchat.monolith.friend.dao.BlockRepository;
import com.leiyx.microchat.monolith.friend.dao.FriendRequestRepository;
import com.leiyx.microchat.monolith.friend.dao.FriendshipRepository;
import com.leiyx.microchat.monolith.friend.entity.Block;
import com.leiyx.microchat.monolith.friend.entity.FriendRequest;
import com.leiyx.microchat.monolith.friend.entity.FriendRequestStatus;
import com.leiyx.microchat.monolith.friend.entity.Friendship;
import com.leiyx.microchat.monolith.friend.entity.User;
import com.leiyx.microchat.monolith.friend.service.FriendService;

@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    private AuthService authService;

    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private BlockRepository blockRepository;


    @Override
    public List<User> getFriends(UUID userId) {
        return friendshipRepository.getFriendshipByUserId(userId)
                .stream()
                .map(Friendship::getFriendId)
                .map(authService::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public boolean isFriend(UUID userId, UUID friendId) {
        return friendshipRepository.existsByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public boolean deleteFriend(UUID userId, UUID friendId) {
        friendshipRepository.deleteByUserIdAndFriendId(userId, friendId);
        return true;
    }

    public boolean addFriend(UUID userId, UUID friendId) {
        // If is blocked, don't create friend request
        if (isBlocked(userId, friendId))
            return false;

        if (isFriend(friendId, userId)) {
            if (isFriend(userId, friendId)) {
                // If there is already a mutual friendship, don't create friend request
                return false;
            } else {
                // If you deleted a user and add back, resume friendship without creating request
                friendshipRepository.save(new Friendship(userId, friendId));
                return true;
            }
        } else {
            // Create friend request if you are not the user's friend
            return addFriendRequest(userId, friendId);
        }
    }

    private boolean addFriendRequest(UUID userId, UUID friendId) {
        friendRequestRepository.save(new FriendRequest(userId, friendId));
        return true;
    }


    public List<FriendRequest> getFriendRequests(UUID userId) {
        return friendRequestRepository.getAllRequestsByUserId(userId);
    }

    public boolean acceptFriendRequest(UUID userId, UUID requestId) {
        FriendRequest friendRequest = friendRequestRepository.getById(requestId);
        System.out.println(friendRequest);
        if (friendRequest == null)
            return false;

        if (!friendRequest.getReceiverId().equals(userId))
            return false;

        if (!friendRequest.getStatus().equals(FriendRequestStatus.PENDING))
            return false;

        friendRequest.setStatus(FriendRequestStatus.APPROVED);
        friendRequestRepository.save(friendRequest);
        friendshipRepository.save(new Friendship(userId, friendRequest.getSenderId()));
        if (!isFriend(friendRequest.getSenderId(), userId))
            friendshipRepository.save(new Friendship(friendRequest.getSenderId(), userId));
        return true;
    }

    public boolean declineFriendRequest(UUID userId, UUID requestId) {
        FriendRequest friendRequest = friendRequestRepository.getById(requestId);
        if (friendRequest == null)
            return false;

        if (!friendRequest.getReceiverId().equals(userId))
            return false;

        if (!friendRequest.getStatus().equals(FriendRequestStatus.PENDING))
            return false;

        friendRequest.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(friendRequest);
        return true;
    }

    public List<User> getBlockedUsers(UUID userId) {
        return List.of();
    }

    public boolean isBlocked(UUID userId, UUID friendId) {
        return blockRepository.existsByUserIdAndBlockedUserId(userId, friendId);
    }

    @Override
    public boolean blockUser(UUID userId, UUID friendId) {
        return blockRepository.save(new Block(userId, friendId)) > 0;
    }

    @Override
    public boolean unblockUser(UUID userId, UUID friendId) {
        return blockRepository.deleteByUserIdAndBlockedUserId(userId, friendId) > 0;
    }
}
