package com.mcdimas.onlineshop.controller;

import com.mcdimas.onlineshop.legacy.LegacyCommandResult;
import com.mcdimas.onlineshop.legacy.LegacyConsoleSessionService;
import com.mcdimas.onlineshop.legacy.LegacyInputRequest;
import com.mcdimas.onlineshop.sse.SseHub;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class LegacyConsoleController {
    private final LegacyConsoleSessionService legacyService;
    private final SseHub sseHub;

    public LegacyConsoleController(LegacyConsoleSessionService legacyService, SseHub sseHub) {
        this.legacyService = legacyService;
        this.sseHub = sseHub;
    }

    @PostMapping("/api/legacy-console/session")
    public LegacyCommandResult create() {
        return legacyService.create();
    }

    @PostMapping("/api/legacy-console/session/{sessionId}/input")
    public LegacyCommandResult input(@PathVariable String sessionId, @RequestBody LegacyInputRequest request) {
        return legacyService.input(sessionId, request.input());
    }

    @GetMapping("/api/legacy-console/session/{sessionId}")
    public LegacyCommandResult current(@PathVariable String sessionId) {
        return legacyService.current(sessionId);
    }

    @PostMapping("/api/legacy-console/session/{sessionId}/reset")
    public LegacyCommandResult reset(@PathVariable String sessionId) {
        return legacyService.reset(sessionId);
    }

    @GetMapping("/sse/legacy-console/{sessionId}")
    public SseEmitter stream(@PathVariable String sessionId) {
        return sseHub.subscribe("legacy-" + sessionId);
    }
}
