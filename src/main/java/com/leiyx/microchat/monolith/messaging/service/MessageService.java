package com.leiyx.microchat.monolith.messaging.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leiyx.microchat.monolith.auth.service.CachedAuthService;
import com.leiyx.microchat.monolith.friend.entity.User;
import com.leiyx.microchat.monolith.friend.service.FriendService;
import com.leiyx.microchat.monolith.messaging.model.dto.Conversation;
import com.leiyx.microchat.monolith.messaging.model.dto.Message;
import com.leiyx.microchat.monolith.messaging.model.persistence.DynamoDbConversation;
import com.leiyx.microchat.monolith.messaging.model.persistence.DynamoDbMessage;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@Component
public class MessageService {
    @Autowired
    DynamoDbTemplate dynamoDbTemplate;
    @Autowired
    CachedAuthService authService;
    @Autowired
    FriendService friendService;

    public void insert(UUID senderId, UUID receiverId, com.leiyx.microchat.monolith.messaging.model.dto.Message message) {
        String conversationId = generateChatId(senderId.toString(), receiverId.toString());
        
        // Save the message
        DynamoDbMessage persistentMessage = new DynamoDbMessage();
        persistentMessage.setMessageId(message.getMessageId());
        persistentMessage.setConversationId(conversationId);
        persistentMessage.setSenderId(senderId.toString());
        persistentMessage.setReceiverId(receiverId.toString());
        persistentMessage.setContent(message.getContent());
        persistentMessage.setCreatedAt(message.getCreatedAt().toEpochMilli());
        dynamoDbTemplate.save(persistentMessage);

        // Update conversation state for both users
        updateConversationForNewMessage(senderId, receiverId, persistentMessage);
    }

    private void updateConversationForNewMessage(UUID senderId, UUID receiverId, DynamoDbMessage message) {
        // Update sender's conversation state
        DynamoDbConversation senderState = getOrCreateConversationState(senderId, receiverId);
        updateLatestMessageInfo(senderState, message);
        senderState.setLastReadMessageId(message.getMessageId());
        senderState.setUnreadCount(0);
        dynamoDbTemplate.save(senderState);

        // Update receiver's conversation state
        DynamoDbConversation receiverState = getOrCreateConversationState(receiverId, senderId);
        updateLatestMessageInfo(receiverState, message);
        receiverState.setUnreadCount(receiverState.getUnreadCount() + 1);
        dynamoDbTemplate.save(receiverState);
    }

    private DynamoDbConversation getOrCreateConversationState(UUID userId, UUID friendId) {
        Key key = Key.builder()
                .partitionValue(userId.toString())
                .sortValue(friendId.toString())
                .build();
        DynamoDbConversation conversation = dynamoDbTemplate.load(key, DynamoDbConversation.class);
        if (conversation == null) {
            conversation = new DynamoDbConversation();
            conversation.setUserId(userId.toString());
            conversation.setFriendId(friendId.toString());
        }
        return conversation;
    }

    public List<Message> query(UUID userId, UUID friendId, int limit) {
        String conversationId = generateChatId(userId.toString(), friendId.toString());

        Key key = Key.builder().partitionValue(conversationId).build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false) // descending order
                .limit(limit)
                .build();

        PageIterable<DynamoDbMessage> results = dynamoDbTemplate.query(query, DynamoDbMessage.class);
        List<Message> messages = results.stream()
                .flatMap(page -> page.items().stream())
                .map(persistentMessage -> convertPersistentMessageToDto(persistentMessage, userId))
                .collect(Collectors.toList());

        // Update conversation state using the latest message from the query
        if (!messages.isEmpty()) {
            updateConversationState(userId, friendId, results.stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst()
                    .orElse(null));
        }

        Collections.reverse(messages);
        return messages;
    }

    public List<Message> queryUpToMessageId(UUID userId, UUID friendId, Long messageId, int limit) {
        String conversationId = generateChatId(userId.toString(), friendId.toString());

        Key key = Key.builder()
                .partitionValue(conversationId)
                .sortValue(messageId)
                .build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortLessThan(key))
                .scanIndexForward(false) // descending order to get latest messages first
                .limit(limit)
                .build();

        PageIterable<DynamoDbMessage> results = dynamoDbTemplate.query(query, DynamoDbMessage.class);
        List<Message> messages = results.stream()
                .flatMap(page -> page.items().stream())
                .map(persistentMessage -> convertPersistentMessageToDto(persistentMessage, userId))
                .collect(Collectors.toList());
        Collections.reverse(messages);
        return messages;
    }

    private void updateConversationState(UUID userId, UUID friendId, DynamoDbMessage latestMessage) {
        if (latestMessage != null) {
            Key conversationKey = Key.builder()
                    .partitionValue(userId.toString())
                    .sortValue(friendId.toString())
                    .build();
            DynamoDbConversation conversation = dynamoDbTemplate.load(conversationKey, DynamoDbConversation.class);
            if (conversation == null) {
                conversation = new DynamoDbConversation();
                conversation.setUserId(userId.toString());
                conversation.setFriendId(friendId.toString());
            }
            conversation.setLastReadMessageId(latestMessage.getMessageId());
            conversation.setUnreadCount(0);
            updateLatestMessageInfo(conversation, latestMessage);
            dynamoDbTemplate.save(conversation);
        }
    }

    private Message convertPersistentMessageToDto(DynamoDbMessage persistentMessage, UUID requestingUserId) {
        Message message = new Message();
        message.setMessageId(persistentMessage.getMessageId());
        message.setContent(persistentMessage.getContent());
        message.setSender(getUsernameById(UUID.fromString(persistentMessage.getSenderId())));
        message.setReceiver(getUsernameById(UUID.fromString(persistentMessage.getReceiverId())));
        message.setCreatedAt(Instant.ofEpochMilli(persistentMessage.getCreatedAt()));
        message.setRole(persistentMessage.getSenderId().equals(requestingUserId.toString()) ? "sender" : "receiver");
        return message;
    }

    private String generateChatId(String userA, String userB) {
        return (userA.compareTo(userB) < 0) ? userA + "_" + userB : userB + "_" + userA;
    }

    private String getUsernameById(UUID id) {
        return authService.getUserById(id)
                .map(User::getUsername)
                .orElse("");
    }

    public List<Conversation> getConversations(UUID userId) {
        // Get all friends first
        List<User> friends = friendService.getFriends(userId);
        Map<UUID, User> friendMap = friends.stream()
                .collect(Collectors.toMap(User::getId, friend -> friend));

        // Query all conversations for the user
        Key key = Key.builder()
                .partitionValue(userId.toString())
                .build();
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        PageIterable<DynamoDbConversation> results = dynamoDbTemplate.query(query, DynamoDbConversation.class);
        List<Conversation> conversations = new ArrayList<>();

        // Process all conversations and filter out non-friends
        results.stream()
                .flatMap(page -> page.items().stream())
                .forEach(conversationState -> {
                    UUID friendId = UUID.fromString(conversationState.getFriendId());
                    User friend = friendMap.get(friendId);
                    
                    // Only include conversations with current friends
                    if (friend != null && conversationState.getLatestMessageId() != null) {
                        Conversation conversation = new Conversation();
                        conversation.setFriendUsername(friend.getUsername());
                        conversation.setFriendFirstName(friend.getFirstName());
                        conversation.setFriendLastName(friend.getLastName());
                        
                        // Create latest message from conversation state
                        Message latestMessage = new Message();
                        latestMessage.setMessageId(conversationState.getLatestMessageId());
                        latestMessage.setContent(conversationState.getLatestMessageContent());
                        latestMessage.setSender(getUsernameById(UUID.fromString(conversationState.getLatestMessageSenderId())));
                        latestMessage.setReceiver(getUsernameById(UUID.fromString(conversationState.getLatestMessageReceiverId())));
                        latestMessage.setCreatedAt(Instant.ofEpochMilli(conversationState.getLatestMessageCreatedAt()));
                        conversation.setLatestMessage(latestMessage);
                        
                        conversation.setUnreadCount(conversationState.getUnreadCount());
                        conversations.add(conversation);
                    }
                });

        // Sort conversations by latest message timestamp
        conversations.sort((c1, c2) -> {
            if (c1.getLatestMessage() == null) return 1;
            if (c2.getLatestMessage() == null) return -1;
            return c2.getLatestMessage().getCreatedAt().compareTo(c1.getLatestMessage().getCreatedAt());
        });

        return conversations;
    }

    private void updateLatestMessageInfo(DynamoDbConversation conversation, DynamoDbMessage message) {
        conversation.setLatestMessageId(message.getMessageId());
        conversation.setLatestMessageContent(message.getContent());
        conversation.setLatestMessageSenderId(message.getSenderId());
        conversation.setLatestMessageReceiverId(message.getReceiverId());
        conversation.setLatestMessageCreatedAt(message.getCreatedAt());
    }
}
