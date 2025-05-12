package com.leiyx.microchat.monolith.auth.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "T(java.util.UUID).fromString(subject)")
public @interface CurrentUserId {
}
