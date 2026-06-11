package com.mcdimas.onlineshop.config;

import com.mcdimas.onlineshop.security.ShopUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/health", "/login", "/register", "/error", "/403", "/404", "/500",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/api/auth/me", "/api/products/**", "/products", "/products/**",
                                "/legacy-console", "/legacy-console/**", "/api/legacy-console/**", "/sse/legacy-console/**").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/customer/**", "/cart/**", "/checkout/**", "/orders/**", "/profile/**",
                                "/api/cart/**", "/api/checkout", "/api/orders/**", "/sse/orders/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(exceptions -> exceptions.accessDeniedPage("/403"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/sse/**"));
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(ShopUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new RoleBasedAuthenticationSuccessHandler();
    }

    private static class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            boolean admin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(admin ? "/admin/dashboard" : "/customer/dashboard");
        }
    }
}
