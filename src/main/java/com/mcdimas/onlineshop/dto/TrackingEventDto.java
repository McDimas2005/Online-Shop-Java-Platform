package com.mcdimas.onlineshop.dto;

import com.mcdimas.onlineshop.entity.OrderStatus;
import java.time.LocalDateTime;

public record TrackingEventDto(
        OrderStatus status,
        int progressPercent,
        String message,
        LocalDateTime occurredAt
) {
}
