package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
