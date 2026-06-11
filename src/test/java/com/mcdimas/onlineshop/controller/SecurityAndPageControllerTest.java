package com.mcdimas.onlineshop.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mcdimas.onlineshop.entity.Role;
import com.mcdimas.onlineshop.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAndPageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void publicPagesAreAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/legacy-console"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    void demoUsersExistWithEncodedPasswordsAndCorrectRoles() {
        var admin = userAccountRepository.findByEmailIgnoreCase("admin@onlineshop.local").orElseThrow();
        var customer = userAccountRepository.findByEmailIgnoreCase("customer@onlineshop.local").orElseThrow();

        assertThat(admin.getUserCode()).isEqualTo("AD000");
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(passwordEncoder.matches("admin123", admin.getPasswordHash())).isTrue();
        assertThat(admin.getPasswordHash()).doesNotContain("admin123");
        assertThat(customer.getUserCode()).isEqualTo("CU001");
        assertThat(customer.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(passwordEncoder.matches("customer123", customer.getPasswordHash())).isTrue();
    }

    @Test
    void dashboardRedirectsByAuthenticationStateAndRole() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/dashboard").with(user("admin@onlineshop.local").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(get("/dashboard").with(user("customer@onlineshop.local").roles("CUSTOMER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard"));
    }

    @Test
    void formLoginRedirectsToRoleDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").userParameter("email").user("admin@onlineshop.local").password("admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(formLogin("/login").userParameter("email").user("customer@onlineshop.local").password("customer123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/dashboard"));
    }

    @Test
    void protectedPagesRespectRoles() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/admin/dashboard").with(user("customer@onlineshop.local").roles("CUSTOMER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/dashboard").with(user("admin@onlineshop.local").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customer/dashboard").with(user("customer@onlineshop.local").roles("CUSTOMER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customer/dashboard").with(user("admin@onlineshop.local").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }
}
