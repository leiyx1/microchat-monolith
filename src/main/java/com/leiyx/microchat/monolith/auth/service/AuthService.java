package com.leiyx.microchat.monolith.auth.service;

import com.leiyx.microchat.monolith.friend.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@FeignClient(name="auth-server", url = "${microchat.auth-url}")
public interface AuthService {
    @GetMapping("admin/realms/microchat/users")
    List<User> getUsersByUsername(@RequestParam Map<String, String> params);

    @GetMapping("admin/realms/microchat/users/{id}")
    Optional<User> getUserById(@PathVariable UUID id);
}
