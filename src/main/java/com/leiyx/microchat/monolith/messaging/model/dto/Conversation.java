package com.leiyx.microchat.monolith.messaging.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conversation {
    private String friendUsername;
    private String friendFirstName;
    private String friendLastName;
    private Message latestMessage;
    private int unreadCount;

    public Conversation() {}

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    public String getFriendFullName() {
        return friendFirstName + " " + friendLastName;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
} 