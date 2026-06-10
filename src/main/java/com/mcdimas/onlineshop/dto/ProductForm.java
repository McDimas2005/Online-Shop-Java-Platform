package com.mcdimas.onlineshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class ProductForm {
    @NotBlank
    private String productCode;
    @NotBlank
    private String productName;
    @DecimalMin("0.01")
    private BigDecimal price;
    @Min(0)
    private int quantityInStock;
    private String size;
    private String brand;
    @Min(0)
    private int warrantyPeriodYears;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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
