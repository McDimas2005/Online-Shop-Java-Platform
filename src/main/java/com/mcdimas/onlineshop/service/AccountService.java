package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.dto.RegisterRequest;
import com.mcdimas.onlineshop.entity.Cart;
import com.mcdimas.onlineshop.entity.CustomerProfile;
import com.mcdimas.onlineshop.entity.Role;
import com.mcdimas.onlineshop.entity.UserAccount;
import com.mcdimas.onlineshop.exception.AppException;
import com.mcdimas.onlineshop.exception.NotFoundException;
import com.mcdimas.onlineshop.repository.CustomerProfileRepository;
import com.mcdimas.onlineshop.repository.UserAccountRepository;
import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final UserAccountRepository userAccountRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public AccountService(UserAccountRepository userAccountRepository,
                          CustomerProfileRepository customerProfileRepository,
                          PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerCustomer(RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new AppException("Email is already registered.");
        }
        String code = nextCustomerCode();
        UserAccount user = new UserAccount(code, request.name(), request.email(),
                passwordEncoder.encode(request.password()), Role.CUSTOMER);
        CustomerProfile profile = new CustomerProfile(user, request.address(), 3 + random.nextInt(7));
        Cart cart = new Cart(profile);
        profile.setCart(cart);
        user.setCustomerProfile(profile);
        return userAccountRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserAccount byEmail(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    @Transactional(readOnly = true)
    public CustomerProfile customerProfile(String email) {
        return customerProfileRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Customer profile not found."));
    }

    @Transactional
    public CustomerProfile updateProfile(String email, String newEmail, String address) {
        CustomerProfile profile = customerProfile(email);
        UserAccount user = profile.getUser();
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail())) {
            if (userAccountRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new AppException("Email is already registered.");
            }
            user.setEmail(newEmail);
        }
        if (address != null && !address.isBlank()) {
            profile.setAddress(address);
        }
        return profile;
    }

    private String nextCustomerCode() {
        long count = userAccountRepository.count() + 1;
        String code;
        do {
            code = "CU" + String.format("%03d", count++);
        } while (userAccountRepository.existsByUserCode(code));
        return code;
    }
}
