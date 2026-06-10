package com.mcdimas.onlineshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mcdimas.onlineshop.entity.ClothingProduct;
import com.mcdimas.onlineshop.entity.ElectronicsProduct;
import com.mcdimas.onlineshop.entity.ProductCategory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductDomainTest {
    @Test
    void clothingProductPreservesOriginalOopSpecialization() {
        ClothingProduct product = new ClothingProduct("P100", "Jacket", new BigDecimal("75.00"), 4, "L");

        assertThat(product.category()).isEqualTo(ProductCategory.CLOTHING);
        assertThat(product.isAvailable()).isTrue();
        assertThat(product.getSize()).isEqualTo("L");
    }

    @Test
    void electronicsProductPreservesBrandAndWarrantyBehavior() {
        ElectronicsProduct product = new ElectronicsProduct("P200", "Tablet", new BigDecimal("250.00"), 2, "TechCo", 2);

        assertThat(product.category()).isEqualTo(ProductCategory.ELECTRONICS);
        assertThat(product.getBrand()).isEqualTo("TechCo");
        assertThat(product.getWarrantyPeriodYears()).isEqualTo(2);
    }

    @Test
    void productAvailabilityRequiresActiveStock() {
        ClothingProduct product = new ClothingProduct("P101", "Cap", new BigDecimal("12.00"), 0, "M");

        assertThat(product.isAvailable()).isFalse();
        product.restock(3);
        assertThat(product.isAvailable()).isTrue();
        product.setActive(false);
        assertThat(product.isAvailable()).isFalse();
    }

    @Test
    void stockCannotBeDeductedBelowZero() {
        ClothingProduct product = new ClothingProduct("P102", "Shoes", new BigDecimal("59.00"), 1, "9");

        assertThatThrownBy(() -> product.deductStock(2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }
}
