package com.leiyx.microchat.monolith.messaging.config;

import com.leiyx.microchat.monolith.messaging.util.IdGenerator;
import com.leiyx.microchat.monolith.messaging.util.RandomWorkerIdSnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new RandomWorkerIdSnowflakeIdGenerator();
    }
}
