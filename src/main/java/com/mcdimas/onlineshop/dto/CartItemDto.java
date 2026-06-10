package com.mcdimas.onlineshop.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long id,
        Long productId,
        String productCode,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
