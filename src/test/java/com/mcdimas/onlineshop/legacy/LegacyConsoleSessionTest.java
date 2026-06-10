package com.mcdimas.onlineshop.legacy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LegacyConsoleSessionTest {
    @Test
    void sessionSupportsOriginalLoginAndMainMenuFlow() {
        LegacyConsoleSession session = new LegacyConsoleSession("test");

        assertThat(session.welcome()).contains("Online Shopping Platform", "Your Choice");
        assertThat(session.submit("1").output()).contains("LOGIN");
        LegacyCommandResult result = session.submit("CU001");

        assertThat(result.output()).contains("Login Success", "View Shopping Cart", "Customer Info");
        assertThat(result.state()).isEqualTo(LegacyConsoleState.MAIN_MENU);
    }

    @Test
    void invalidInputKeepsTheSimulationControlled() {
        LegacyConsoleSession session = new LegacyConsoleSession("test");

        LegacyCommandResult result = session.submit("not-a-shell-command");

        assertThat(result.output()).contains("Type a valid menu number");
        assertThat(result.output()).doesNotContain("/home", "PATH", "Exception");
    }

    @Test
    void resetRestoresSeededPrototypeState() {
        LegacyConsoleSession session = new LegacyConsoleSession("test");
        session.submit("1");
        session.submit("CU001");

        LegacyCommandResult result = session.reset();

        assertThat(result.state()).isEqualTo(LegacyConsoleState.LOGIN_MENU);
        assertThat(result.output()).contains("Log In", "Sign Up");
    }
}
