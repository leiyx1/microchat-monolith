package com.leiyx.microchat.monolith.friend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leiyx.microchat.monolith.auth.config.CurrentUserId;
import com.leiyx.microchat.monolith.auth.service.AuthService;
import com.leiyx.microchat.monolith.friend.dto.FriendRequestDTO;
import com.leiyx.microchat.monolith.friend.dto.UserDTO;
import com.leiyx.microchat.monolith.friend.entity.FriendRequest;
import com.leiyx.microchat.monolith.friend.entity.User;
import com.leiyx.microchat.monolith.friend.exception.UserNotFoundException;
import com.leiyx.microchat.monolith.friend.service.FriendService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1")
public class FriendController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final FriendService friendService;

    private final AuthService authService;

    @Autowired
    public FriendController(FriendService friendService, AuthService authService) {
        this.friendService = friendService;
        this.authService = authService;
    }

    private UUID getIdByUsername(String username) {
        List<User> users = authService.getUsersByUsername(Map.of("username", username, "exact", "true"));
        if (users.isEmpty())
            throw new UserNotFoundException();
        return users.get(0).getId();
    }
    
    private User getUserById(UUID id) {
        Optional<User> user = authService.getUserById(id);
        if (user.isEmpty())
            throw new UserNotFoundException();
        else
            return user.get();
    }

    @GetMapping(value = "friends")
    public ResponseEntity<?> getFriends(@CurrentUserId UUID userId) {
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @GetMapping(value = "friends/{friendUsername}")
    public ResponseEntity<?> isFriend(
            @CurrentUserId UUID userId,
            @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.isFriend(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "friends/{friendUsername}")
    public ResponseEntity<?> addFriend(
            @CurrentUserId UUID userId,
            @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.addFriend(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    @DeleteMapping(value = "friends/{friendUsername}")
    public ResponseEntity<?> deleteFriend(
            @CurrentUserId UUID userId,
            @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.deleteFriend(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "friend_requests")
    public ResponseEntity<?> getFriendRequests(@CurrentUserId UUID userId) {
        List<FriendRequest> requests = friendService.getFriendRequests(userId);
        List<FriendRequestDTO> dtos = requests.stream()
            .map(request -> {
                User sender = getUserById(request.getSenderId());
                User receiver = getUserById(request.getReceiverId());
                return FriendRequestDTO.fromRequest(request, sender, receiver);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping(value = "friend_requests/{requestId}")
    public ResponseEntity<?> acceptFriendRequest(
            @CurrentUserId UUID userId,
            @PathVariable UUID requestId) {
        if (friendService.acceptFriendRequest(userId, requestId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping(value = "friend_request/{requestId}")
    public ResponseEntity<?> declineFriendRequest(
            @RequestParam UUID username,
            @PathVariable UUID requestId) {
        if (friendService.declineFriendRequest(username, requestId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "blocks")
    public ResponseEntity<?> getBlockedUsers(@CurrentUserId UUID userId) {
        return ResponseEntity.ok(friendService.getBlockedUsers(userId));
    }

    @GetMapping(value = "blocks/{friendUsername}")
    public ResponseEntity<?> isBlocked(@CurrentUserId UUID userId,
                                       @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.isBlocked(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "blocks/{friendUsername}")
    public ResponseEntity<?> blockUser(@CurrentUserId UUID userId,
                                       @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.blockUser(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "blocks/{friendUsername}")
    public ResponseEntity<?> unblockUser(
            @CurrentUserId UUID userId,
            @PathVariable String friendUsername) {
        UUID friendId = getIdByUsername(friendUsername);
        if (friendService.unblockUser(userId, friendId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "users/{username}")
    public ResponseEntity<?> getUsersByUsername(@PathVariable String username) {
        List<User> users = authService.getUsersByUsername(Map.of("username", username, "exact", "true"));
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::fromUser)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}
