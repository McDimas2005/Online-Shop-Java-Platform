package com.mcdimas.onlineshop.seed;

import static org.assertj.core.api.Assertions.assertThat;

import com.mcdimas.onlineshop.entity.Role;
import com.mcdimas.onlineshop.repository.ProductRepository;
import com.mcdimas.onlineshop.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoDataSeederTest {
    @Autowired
    DemoDataSeeder seeder;
    @Autowired
    UserAccountRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void seedingIsIdempotentAndRepairsDemoIdentities() throws Exception {
        seeder.run();
        long users = userRepository.count();
        long products = productRepository.count();

        seeder.run();

        assertThat(userRepository.count()).isEqualTo(users);
        assertThat(productRepository.count()).isEqualTo(products);
        var admin = userRepository.findByEmailIgnoreCase("admin@onlineshop.local").orElseThrow();
        var customer = userRepository.findByEmailIgnoreCase("customer@onlineshop.local").orElseThrow();
        assertThat(admin.getUserCode()).isEqualTo("AD000");
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
        assertThat(passwordEncoder.matches("admin123", admin.getPasswordHash())).isTrue();
        assertThat(customer.getUserCode()).isEqualTo("CU001");
        assertThat(customer.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(passwordEncoder.matches("customer123", customer.getPasswordHash())).isTrue();
    }
}
