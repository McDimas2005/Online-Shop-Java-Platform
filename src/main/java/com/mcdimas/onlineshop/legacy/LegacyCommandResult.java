package com.mcdimas.onlineshop.legacy;

public record LegacyCommandResult(String sessionId, String output, LegacyConsoleState state, boolean clearScreen) {
    public LegacyCommandResult(String sessionId, String output, LegacyConsoleState state) {
        this(sessionId, output, state, false);
    }
}
