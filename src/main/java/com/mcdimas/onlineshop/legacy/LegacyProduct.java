package com.mcdimas.onlineshop.legacy;

import java.math.BigDecimal;

class LegacyProduct {
    String id;
    String name;
    BigDecimal price;
    int stock;
    String category;
    String size;
    String brand;
    int warranty;

    LegacyProduct(String id, String name, BigDecimal price, int stock, String category, String size, String brand, int warranty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.size = size;
        this.brand = brand;
        this.warranty = warranty;
    }

    boolean available() {
        return stock > 0;
    }
}
