package com.leiyx.microchat.monolith.messaging.model.persistence;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoDbConversation {
    private String userId;
    private String friendId;
    private Long lastReadMessageId;
    private int unreadCount;
    private Long latestMessageId;
    private String latestMessageContent;
    private String latestMessageSenderId;
    private String latestMessageReceiverId;
    private Long latestMessageCreatedAt;

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbSortKey
    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getLatestMessageId() {
        return latestMessageId;
    }

    public void setLatestMessageId(Long latestMessageId) {
        this.latestMessageId = latestMessageId;
    }

    public String getLatestMessageContent() {
        return latestMessageContent;
    }

    public void setLatestMessageContent(String latestMessageContent) {
        this.latestMessageContent = latestMessageContent;
    }

    public String getLatestMessageSenderId() {
        return latestMessageSenderId;
    }

    public void setLatestMessageSenderId(String latestMessageSenderId) {
        this.latestMessageSenderId = latestMessageSenderId;
    }

    public String getLatestMessageReceiverId() {
        return latestMessageReceiverId;
    }

    public void setLatestMessageReceiverId(String latestMessageReceiverId) {
        this.latestMessageReceiverId = latestMessageReceiverId;
    }

    public Long getLatestMessageCreatedAt() {
        return latestMessageCreatedAt;
    }

    public void setLatestMessageCreatedAt(Long latestMessageCreatedAt) {
        this.latestMessageCreatedAt = latestMessageCreatedAt;
    }
} 