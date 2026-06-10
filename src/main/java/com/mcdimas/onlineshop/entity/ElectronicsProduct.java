package com.mcdimas.onlineshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class ElectronicsProduct extends Product {
    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private int warrantyPeriodYears;

    protected ElectronicsProduct() {
    }

    public ElectronicsProduct(String productCode, String productName, BigDecimal price, int quantityInStock, String brand, int warrantyPeriodYears) {
        super(productCode, productName, price, quantityInStock);
        this.brand = brand;
        this.warrantyPeriodYears = warrantyPeriodYears;
    }

    @Override
    public ProductCategory category() {
        return ProductCategory.ELECTRONICS;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriodYears() {
        return warrantyPeriodYears;
    }

    public void setWarrantyPeriodYears(int warrantyPeriodYears) {
        this.warrantyPeriodYears = warrantyPeriodYears;
    }
}
