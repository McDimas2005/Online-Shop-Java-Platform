package com.mcdimas.onlineshop.dto;

import com.mcdimas.onlineshop.entity.ProductCategory;
import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String productCode,
        String productName,
        BigDecimal price,
        int quantityInStock,
        boolean active,
        boolean available,
        ProductCategory category,
        String size,
        String brand,
        Integer warrantyPeriodYears
) {
}
