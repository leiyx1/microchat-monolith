package com.leiyx.microchat.monolith.messaging.websocket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leiyx.microchat.monolith.auth.service.AuthService;
import com.leiyx.microchat.monolith.friend.entity.User;
import com.leiyx.microchat.monolith.friend.service.FriendService;
import com.leiyx.microchat.monolith.messaging.model.dto.Message;
import com.leiyx.microchat.monolith.messaging.service.MessageService;
import com.leiyx.microchat.monolith.messaging.util.IdGenerator;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

@Component
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    AuthService userService;

    FriendService friendService;

    MessageService messageService;

    ObjectMapper mapper;

    JwtDecoder jwtDecoder;

    IdGenerator idGenerator;

    @Autowired
    public TextWebSocketFrameHandler(
            AuthService userService,
            FriendService friendService,
            MessageService messageService,
            ObjectMapper mapper,
            JwtDecoder jwtDecoder,
            IdGenerator idGenerator) {
        this.userService = userService;
        this.friendService = friendService;
        this.messageService = messageService;
        this.mapper = mapper;
        this.jwtDecoder = jwtDecoder;
        this.idGenerator = idGenerator;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        logger.debug("incoming message: {}", msg.text());
        try {
            Message message = mapper.readValue(msg.text(), Message.class);
            logger.debug("message parsed: {}", message.toString());
            handleDirectMessage(message, ctx);
        } catch (Exception e) {
            logger.warn("error when handling message: {}", e.getMessage());
            Message errorMessage = new Message();
            errorMessage.setContent(e.getMessage());
            ctx.writeAndFlush(errorMessage);
        }
    }


    private void handleDirectMessage(Message message, ChannelHandlerContext ctx) throws JsonProcessingException {
        message.setCreatedAt(Instant.now());
        message.setMessageId(idGenerator.get().get());

        UUID senderId = UUID.fromString(MessagingSession.getId(ctx));
        String receiverUsername = message.getReceiver();
        logger.debug("Receiver username: {}", receiverUsername);

        UUID receiverId = getIdByUsername(receiverUsername);
        if (receiverId == null) {
            logger.debug("Receiver {}: no such user, or not friend, or blocked, drop", receiverUsername);
            return;
        }
        logger.debug("Receiver id {}", receiverId);

//        if (!isFriend(senderId, receiverId) || !isFriend(receiverId, senderId)) {
//            logger.debug("Receiver id {}: not friend", receiverId);
//            return;
//        }
//
//        if (isBlock(receiverId, senderId)) {
//            logger.debug("Receiver id {}: blocked", receiverUsername);
//            return;
//        }

        Optional<ChannelHandlerContext> outCtx = MessagingSession.getChannelHandlerContext(receiverId.toString());

        if (outCtx.isPresent() && outCtx.get().channel().isActive()) {
            String senderUsername = getUsernameById(senderId);
            message.setSender(senderUsername);
            message.setRole("receiver");
            outCtx.get().writeAndFlush(new TextWebSocketFrame(mapper.writeValueAsString(message)));
            logger.debug("{} online, written to {}", receiverId, outCtx.get());
        }

        message.setRole("sender");
        messageService.insert(senderId, receiverId, message);
        ctx.writeAndFlush(new TextWebSocketFrame(mapper.writeValueAsString(message)));
    }



    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        System.out.println(evt.toString());
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            try {
                System.out.printf("%s: [HANDSHAKE COMPLETE] connection established with %s, now performing authorization%n", LocalDateTime.now(), ctx.toString());
                WebSocketServerProtocolHandler.HandshakeComplete handshake = (WebSocketServerProtocolHandler.HandshakeComplete) evt;

                String uri = handshake.requestUri();
                QueryStringDecoder decoder = new QueryStringDecoder(uri);
                Map<String, List<String>> params = decoder.parameters();
                System.out.println(params);

                if (!params.containsKey("token"))
                    throw new RuntimeException("no access token");

                String token = params.get("token").get(0);

                Jwt jwt = jwtDecoder.decode(token);
                String userId = jwt.getSubject();

                // Remove the HTTP handler since it is no longer needed
                ctx.pipeline().remove(HTTPRequestHandler.class);
                MessagingSession.addSession(userId, ctx);
                System.out.printf("%s: [ESTABLISHED] connection established with %s(%s)%n", LocalDateTime.now(), userId, ctx);
            } catch (Exception e) {
                ctx.channel().close();
                System.out.printf("%s: [REMOVED] connection with %s closed due to some error: %s%n", LocalDateTime.now(), ctx, e.getMessage());
            }

        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MessagingSession.removeSession(ctx);
    }

    private UUID getIdByUsername(String username) {
        List<com.leiyx.microchat.monolith.friend.entity.User> users = userService.getUsersByUsername(Map.of("username", username, "exact", "true"));
        if (users.isEmpty())
            throw new RuntimeException("No such user");
        return users.get(0).getId();
    }

    private String getUsernameById(UUID id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent())
            return user.get().getUsername();
        else
            return "";
    }

    private boolean isFriend(UUID id, UUID friendId) {
        return friendService.isFriend(id, friendId);
    }

    private boolean isBlock(UUID id, UUID friendId) {
        return friendService.isFriend(id, friendId);
    }
}
