package com.leiyx.microchat.monolith.messaging.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.leiyx.microchat.monolith.messaging.model.persistence.DynamoDbConversation;
import com.leiyx.microchat.monolith.messaging.model.persistence.DynamoDbMessage;

import io.awspring.cloud.dynamodb.DefaultDynamoDbTableNameResolver;
import io.awspring.cloud.dynamodb.DefaultDynamoDbTableSchemaResolver;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import io.awspring.cloud.dynamodb.DynamoDbTableSchemaResolver;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;

@Configuration
public class DynamoDbConfiguration {

    @Bean
    public DynamoDbTableNameResolver tableNameResolver() {
        return new DefaultDynamoDbTableNameResolver() {
            @Override
            public String resolve(Class clazz) {
                if (clazz == DynamoDbMessage.class) {
                    return "messages";
                } else if (clazz == DynamoDbConversation.class) {
                    return "conversations";
                } else {
                    return super.resolve(clazz);
                }
            }
        };
    }

    @Bean
    public DynamoDbTableSchemaResolver schemaResolver() {
        StaticTableSchema<DynamoDbMessage> messageSchema = StaticTableSchema.builder(DynamoDbMessage.class)
                .newItemSupplier(DynamoDbMessage::new)
                .addAttribute(String.class, a -> a.name("conversation_id")
                        .setter(DynamoDbMessage::setConversationId)
                        .getter(DynamoDbMessage::getConversationId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(Long.class, a -> a.name("message_id")
                        .setter(DynamoDbMessage::setMessageId)
                        .getter(DynamoDbMessage::getMessageId)
                        .tags(StaticAttributeTags.primarySortKey()))
                .addAttribute(String.class, a -> a.name("sender_id")
                        .setter(DynamoDbMessage::setSenderId)
                        .getter(DynamoDbMessage::getSenderId))
                .addAttribute(String.class, a -> a.name("receiver_id")
                        .setter(DynamoDbMessage::setReceiverId)
                        .getter(DynamoDbMessage::getReceiverId))
                .addAttribute(String.class, a -> a.name("content")
                        .setter(DynamoDbMessage::setContent)
                        .getter(DynamoDbMessage::getContent))
                .addAttribute(Long.class, a -> a.name("created_at")
                        .setter(DynamoDbMessage::setCreatedAt)
                        .getter(DynamoDbMessage::getCreatedAt))
                .build();

        StaticTableSchema<DynamoDbConversation> conversationSchema = StaticTableSchema.builder(DynamoDbConversation.class)
                .newItemSupplier(DynamoDbConversation::new)
                .addAttribute(String.class, a -> a.name("user_id")
                        .setter(DynamoDbConversation::setUserId)
                        .getter(DynamoDbConversation::getUserId)
                        .tags(StaticAttributeTags.primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("friend_id")
                        .setter(DynamoDbConversation::setFriendId)
                        .getter(DynamoDbConversation::getFriendId)
                        .tags(StaticAttributeTags.primarySortKey()))
                .addAttribute(Long.class, a -> a.name("last_read_message_id")
                        .setter(DynamoDbConversation::setLastReadMessageId)
                        .getter(DynamoDbConversation::getLastReadMessageId))
                .addAttribute(Integer.class, a -> a.name("unread_count")
                        .setter(DynamoDbConversation::setUnreadCount)
                        .getter(DynamoDbConversation::getUnreadCount))
                .addAttribute(Long.class, a -> a.name("latest_message_id")
                        .setter(DynamoDbConversation::setLatestMessageId)
                        .getter(DynamoDbConversation::getLatestMessageId))
                .addAttribute(String.class, a -> a.name("latest_message_content")
                        .setter(DynamoDbConversation::setLatestMessageContent)
                        .getter(DynamoDbConversation::getLatestMessageContent))
                .addAttribute(String.class, a -> a.name("latest_message_sender_id")
                        .setter(DynamoDbConversation::setLatestMessageSenderId)
                        .getter(DynamoDbConversation::getLatestMessageSenderId))
                .addAttribute(String.class, a -> a.name("latest_message_receiver_id")
                        .setter(DynamoDbConversation::setLatestMessageReceiverId)
                        .getter(DynamoDbConversation::getLatestMessageReceiverId))
                .addAttribute(Long.class, a -> a.name("latest_message_created_at")
                        .setter(DynamoDbConversation::setLatestMessageCreatedAt)
                        .getter(DynamoDbConversation::getLatestMessageCreatedAt))
                .build();

        return new DefaultDynamoDbTableSchemaResolver(List.of(messageSchema, conversationSchema));
    }

}
