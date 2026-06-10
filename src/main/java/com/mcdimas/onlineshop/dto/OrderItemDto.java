package com.mcdimas.onlineshop.dto;

import com.mcdimas.onlineshop.entity.ProductCategory;
import java.math.BigDecimal;

public record OrderItemDto(
        String productCode,
        String productName,
        ProductCategory category,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal
) {
}
