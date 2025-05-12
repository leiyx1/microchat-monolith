package com.leiyx.microchat.monolith.auth.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
class SecurityConfig {
    private final OAuth2ResourceServerProperties.Jwt properties;

    SecurityConfig(OAuth2ResourceServerProperties properties) {
        this.properties = properties.getJwt();
    }

    @Bean
    SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        http.oauth2ResourceServer((resourceServer) -> resourceServer.jwt(withDefaults()));
        http.oauth2Client(withDefaults());
        return http.build();
    }
}