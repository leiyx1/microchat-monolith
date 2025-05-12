package com.leiyx.microchat.monolith.messaging.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class HTTPRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    public HTTPRequestHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        ctx.fireChannelRead(request.retain());
    }
}
