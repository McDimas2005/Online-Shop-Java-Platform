package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);
}
