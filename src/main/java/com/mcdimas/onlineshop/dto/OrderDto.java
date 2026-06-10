package com.mcdimas.onlineshop.dto;

import com.mcdimas.onlineshop.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNumber,
        LocalDateTime createdAt,
        OrderStatus status,
        BigDecimal totalPrice,
        int distanceKm,
        int progressPercent,
        List<OrderItemDto> items
) {
}
