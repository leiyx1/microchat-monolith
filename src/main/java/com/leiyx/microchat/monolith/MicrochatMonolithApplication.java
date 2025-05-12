package com.leiyx.microchat.monolith;

import com.leiyx.microchat.monolith.messaging.websocket.MessagingServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableFeignClients
public class MicrochatMonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrochatMonolithApplication.class, args);
    }

}

@Component
class NettyServerRunner implements CommandLineRunner {

    private final MessagingServer messagingServer;

    @Autowired
    public NettyServerRunner(MessagingServer messagingServer) {
        this.messagingServer = messagingServer;
    }

    @Override
    public void run(String... args) throws Exception {
        messagingServer.start();
    }
}

