package com.mcdimas.onlineshop.legacy;

import com.mcdimas.onlineshop.sse.SseHub;
import org.springframework.stereotype.Service;

@Service
public class LegacyConsoleSessionService {
    private final LegacySessionRegistry registry;
    private final SseHub sseHub;

    public LegacyConsoleSessionService(LegacySessionRegistry registry, SseHub sseHub) {
        this.registry = registry;
        this.sseHub = sseHub;
    }

    public LegacyCommandResult create() {
        LegacyConsoleSession session = registry.create();
        LegacyCommandResult result = new LegacyCommandResult(session.id(), session.welcome(), session.state());
        publish(result);
        return result;
    }

    public LegacyCommandResult input(String sessionId, String input) {
        LegacyCommandResult result = registry.get(sessionId).submit(input);
        publish(result);
        return result;
    }

    public LegacyCommandResult current(String sessionId) {
        LegacyConsoleSession session = registry.get(sessionId);
        return new LegacyCommandResult(session.id(), "", session.state());
    }

    public LegacyCommandResult reset(String sessionId) {
        LegacyCommandResult result = registry.get(sessionId).reset();
        publish(result);
        return result;
    }

    private void publish(LegacyCommandResult result) {
        sseHub.publish("legacy-" + result.sessionId(), result);
    }
}
