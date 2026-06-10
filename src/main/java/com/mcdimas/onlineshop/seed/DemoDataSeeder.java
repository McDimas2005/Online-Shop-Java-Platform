package com.mcdimas.onlineshop.seed;

import com.mcdimas.onlineshop.entity.Cart;
import com.mcdimas.onlineshop.entity.ClothingProduct;
import com.mcdimas.onlineshop.entity.CustomerProfile;
import com.mcdimas.onlineshop.entity.ElectronicsProduct;
import com.mcdimas.onlineshop.entity.Role;
import com.mcdimas.onlineshop.entity.UserAccount;
import com.mcdimas.onlineshop.repository.ProductRepository;
import com.mcdimas.onlineshop.repository.UserAccountRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoDataSeeder implements CommandLineRunner {
    private final UserAccountRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String customerPassword;

    public DemoDataSeeder(UserAccountRepository userRepository,
                          ProductRepository productRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.demo.admin-password}") String adminPassword,
                          @Value("${app.demo.customer-password}") String customerPassword) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.customerPassword = customerPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (!userRepository.existsByUserCode("AD000")) {
            UserAccount admin = new UserAccount("AD000", "Tsukishima Alan", "GreatGenshin@mihoyo.com",
                    passwordEncoder.encode(adminPassword), Role.ADMIN);
            userRepository.save(admin);
        }
        if (!userRepository.existsByUserCode("CU001")) {
            UserAccount customer = new UserAccount("CU001", "Jeanne Fortes", "loveVanitas@carte.com",
                    passwordEncoder.encode(customerPassword), Role.CUSTOMER);
            CustomerProfile profile = new CustomerProfile(customer, "Somewhere Paris", 6);
            Cart cart = new Cart(profile);
            profile.setCart(cart);
            customer.setCustomerProfile(profile);
            userRepository.save(customer);
        }
    }

    private void seedProducts() {
        addIfMissing(new ClothingProduct("P001", "T-Shirt - Blue", new BigDecimal("19.99"), 50, "M"));
        addIfMissing(new ClothingProduct("P002", "Jeans - Slim Fit", new BigDecimal("39.99"), 20, "32/34"));
        addIfMissing(new ClothingProduct("P005", "Sneakers - Sports", new BigDecimal("59.99"), 70, "9"));
        addIfMissing(new ElectronicsProduct("P003", "Smartphone - Model X", new BigDecimal("499.99"), 80, "TechCo", 3));
        addIfMissing(new ElectronicsProduct("P004", "Laptop - Ultrabook", new BigDecimal("899.99"), 20, "MegaElect", 5));
        addIfMissing(new ElectronicsProduct("P006", "Smartwatch - Fitness", new BigDecimal("129.99"), 50, "TechCo", 2));
    }

    private void addIfMissing(com.mcdimas.onlineshop.entity.Product product) {
        if (!productRepository.existsByProductCode(product.getProductCode())) {
            productRepository.save(product);
        }
    }
}
