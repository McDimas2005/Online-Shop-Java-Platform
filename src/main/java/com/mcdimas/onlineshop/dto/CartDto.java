package com.mcdimas.onlineshop.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartItemDto> items, BigDecimal total) {
}
