package com.leiyx.microchat.monolith.auth.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.leiyx.microchat.monolith.friend.entity.User;

@Service
public class CachedAuthService {
    @Autowired
    private AuthService authService;

    @Cacheable(value = "userByUsername", key = "#username")
    public Optional<User> getUserByUsername(String username) {
        List<User> users = authService.getUsersByUsername(Map.of("username", username, "exact", "true"));
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Cacheable(value = "userById", key = "#id")
    public Optional<User> getUserById(UUID id) {
        return authService.getUserById(id);
    }
} 