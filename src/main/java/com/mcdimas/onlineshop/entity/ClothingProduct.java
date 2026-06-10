package com.mcdimas.onlineshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class ClothingProduct extends Product {
    @Column(nullable = false, length = 40)
    private String size;

    protected ClothingProduct() {
    }

    public ClothingProduct(String productCode, String productName, BigDecimal price, int quantityInStock, String size) {
        super(productCode, productName, price, quantityInStock);
        this.size = size;
    }

    @Override
    public ProductCategory category() {
        return ProductCategory.CLOTHING;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
