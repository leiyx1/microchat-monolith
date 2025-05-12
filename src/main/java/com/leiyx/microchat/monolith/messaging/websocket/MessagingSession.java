package com.leiyx.microchat.monolith.messaging.websocket;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MessagingSession {
    private static final Logger logger = LoggerFactory.getLogger(MessagingSession.class);
    private static final ConcurrentHashMap<String, ChannelHandlerContext> id_ctx_map = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ChannelHandlerContext, String> ctx_id_map = new ConcurrentHashMap<>();

    public static Optional<ChannelHandlerContext> getChannelHandlerContext(String id) {
        if (id_ctx_map.containsKey(id))
            return Optional.of(id_ctx_map.get(id));
        else
            return Optional.empty();
    }

    public static String getId(ChannelHandlerContext ctx) {
        return ctx_id_map.get(ctx);
    }

    public static void addSession(String id, ChannelHandlerContext ctx) {
        if (id_ctx_map.containsKey(id)) {
            ChannelHandlerContext formerCtx = id_ctx_map.get(id);
            logger.debug("Connection session of {}({}) are about to be removed due to new connection of this id", formerCtx.toString(), id);
            id_ctx_map.remove(id);
            ctx_id_map.remove(formerCtx);
            formerCtx.close();
        }

        id_ctx_map.put(id, ctx);
        ctx_id_map.put(ctx, id);
    }

    public static void removeSession(ChannelHandlerContext ctx) {
        if (ctx_id_map.containsKey(ctx)) {
            String id = ctx_id_map.get(ctx);
            id_ctx_map.remove(id);
            ctx_id_map.remove(ctx);
            logger.debug("Connection session of {}({}) removed", ctx, id);
        } else {
            logger.debug("Connection session of {} removed", ctx.toString());
        }
    }
}


