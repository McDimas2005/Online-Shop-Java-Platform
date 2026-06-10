package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerUserEmailIgnoreCase(String email);
}
