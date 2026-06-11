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
import java.util.Optional;
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
    private final boolean seedDemoData;

    public DemoDataSeeder(UserAccountRepository userRepository,
                          ProductRepository productRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.demo.admin-password}") String adminPassword,
                          @Value("${app.demo.customer-password}") String customerPassword,
                          @Value("${app.seed-demo-data:true}") boolean seedDemoData) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.customerPassword = customerPassword;
        this.seedDemoData = seedDemoData;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDemoData) {
            return;
        }
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        ensureDemoUser("AD000", "Tsukishima Alan", "admin@onlineshop.local", adminPassword,
                Role.ADMIN, null, 4);
        ensureDemoUser("CU001", "Jeanne Fortes", "customer@onlineshop.local", customerPassword,
                Role.CUSTOMER, "Somewhere Paris", 6);
    }

    private void ensureDemoUser(String userCode, String name, String email, String password,
                                Role role, String address, int distanceKm) {
        Optional<UserAccount> byCode = userRepository.findByUserCode(userCode);
        Optional<UserAccount> byEmail = userRepository.findByEmailIgnoreCase(email);
        UserAccount user = byCode.or(() -> byEmail)
                .orElseGet(() -> new UserAccount(userCode, name, email, "", role));

        user.setUserCode(userCode);
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setPasswordHash(passwordEncoder.encode(password));

        if (role == Role.CUSTOMER && user.getCustomerProfile() == null) {
            CustomerProfile profile = new CustomerProfile(user, address, distanceKm);
            Cart cart = new Cart(profile);
            profile.setCart(cart);
            user.setCustomerProfile(profile);
        } else if (role == Role.CUSTOMER) {
            user.getCustomerProfile().setAddress(address);
            user.getCustomerProfile().setDeliveryDistanceKm(distanceKm);
        }

        userRepository.save(user);
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
