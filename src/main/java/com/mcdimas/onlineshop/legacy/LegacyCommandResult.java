package com.mcdimas.onlineshop.legacy;

public record LegacyCommandResult(String sessionId, String output, LegacyConsoleState state) {
}
