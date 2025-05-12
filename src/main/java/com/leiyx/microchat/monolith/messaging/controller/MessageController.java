package com.leiyx.microchat.monolith.messaging.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leiyx.microchat.monolith.auth.config.CurrentUserId;
import com.leiyx.microchat.monolith.auth.service.AuthService;
import com.leiyx.microchat.monolith.friend.entity.User;
import com.leiyx.microchat.monolith.friend.exception.UserNotFoundException;
import com.leiyx.microchat.monolith.messaging.model.dto.Conversation;
import com.leiyx.microchat.monolith.messaging.model.dto.Message;
import com.leiyx.microchat.monolith.messaging.service.MessageService;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1")
public class MessageController {
    @Autowired
    AuthService authService;
    @Autowired
    MessageService messageService;

    private UUID getIdByUsername(String username) {
        List<User> users = authService.getUsersByUsername(Map.of("username", username, "exact", "true"));
        if (users.isEmpty())
            throw new UserNotFoundException();
        return users.get(0).getId();
    }

    @GetMapping("/messages/{friendUsername}")
    public List<Message> getMessages(
            @CurrentUserId UUID userId,
            @PathVariable String friendUsername,
            @RequestParam(required = false) Long messageId,
            @RequestParam(defaultValue = "20") int limit) {
        UUID friendId = getIdByUsername(friendUsername);
        if (messageId != null) {
            return messageService.queryUpToMessageId(userId, friendId, messageId, limit);
        }
        return messageService.query(userId, friendId, limit);
    }

    @GetMapping(value = "conversations")
    List<Conversation> getConversations(@CurrentUserId UUID userId) {
        return messageService.getConversations(userId);
    }
}
