package com.mcdimas.onlineshop.legacy;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LegacySessionRegistry {
    private final Map<String, LegacyConsoleSession> sessions = new ConcurrentHashMap<>();

    public LegacyConsoleSession create() {
        cleanup();
        String id = UUID.randomUUID().toString();
        LegacyConsoleSession session = new LegacyConsoleSession(id);
        sessions.put(id, session);
        return session;
    }

    public LegacyConsoleSession get(String id) {
        cleanup();
        return sessions.computeIfAbsent(id, LegacyConsoleSession::new);
    }

    private void cleanup() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(45));
        sessions.entrySet().removeIf(entry -> entry.getValue().lastTouched().isBefore(cutoff));
    }
}
