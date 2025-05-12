package com.leiyx.microchat.monolith.messaging.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Scanner;

@Component
public class MessagingServer {

    private final EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    private final TextWebSocketFrameHandler textWebSocketFrameHandler;

    @Value("${websocket.port}")
    private Integer port;

    @Autowired
    public MessagingServer(TextWebSocketFrameHandler textWebSocketFrameHandler) {
        this.textWebSocketFrameHandler = textWebSocketFrameHandler;
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HTTPRequestHandler());
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", true));
                        pipeline.addLast(textWebSocketFrameHandler);
                    }
                });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(port));
        future.syncUninterruptibly();
        System.out.println("Started netty server on port " + port);
        channel = future.channel();
        if (channel != null) {
            channel.closeFuture().syncUninterruptibly();
        }
        group.shutdownGracefully();
    }
}
