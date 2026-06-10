package com.mcdimas.onlineshop.controller;

import com.mcdimas.onlineshop.service.AccountService;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiController {
    private final AccountService accountService;

    public AuthApiController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/api/auth/me")
    public Map<String, Object> me(Principal principal) {
        if (principal == null) {
            return Map.of("authenticated", false);
        }
        var user = accountService.byEmail(principal.getName());
        return Map.of(
                "authenticated", true,
                "userCode", user.getUserCode(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        );
    }
}
