package com.leiyx.microchat.monolith.messaging.util;

import java.util.Optional;

public interface IdGenerator {
    Optional<Long> get();
}
