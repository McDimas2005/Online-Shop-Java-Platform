package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.dto.ProductForm;
import com.mcdimas.onlineshop.entity.ClothingProduct;
import com.mcdimas.onlineshop.entity.ElectronicsProduct;
import com.mcdimas.onlineshop.entity.Product;
import com.mcdimas.onlineshop.entity.ProductCategory;
import com.mcdimas.onlineshop.exception.AppException;
import com.mcdimas.onlineshop.exception.NotFoundException;
import com.mcdimas.onlineshop.repository.ProductRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> search(ProductCategory category, String query, String brand, String size, Boolean availableOnly) {
        String q = normalize(query);
        String b = normalize(brand);
        String s = normalize(size);
        boolean onlyAvailable = Boolean.TRUE.equals(availableOnly);
        return productRepository.findAll().stream()
                .filter(product -> category == null || product.category() == category)
                .filter(product -> q == null || product.getProductName().toLowerCase(Locale.ROOT).contains(q)
                        || product.getProductCode().toLowerCase(Locale.ROOT).contains(q))
                .filter(product -> !onlyAvailable || product.isAvailable())
                .filter(product -> b == null || product instanceof ElectronicsProduct electronics
                        && electronics.getBrand().toLowerCase(Locale.ROOT).contains(b))
                .filter(product -> s == null || product instanceof ClothingProduct clothing
                        && clothing.getSize().toLowerCase(Locale.ROOT).contains(s))
                .sorted(Comparator.comparing(Product::getProductCode))
                .toList();
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found."));
    }

    @Transactional
    public ClothingProduct addClothing(ProductForm form) {
        ensureProductCodeAvailable(form.getProductCode());
        return productRepository.save(new ClothingProduct(form.getProductCode(), form.getProductName(),
                form.getPrice(), form.getQuantityInStock(), form.getSize()));
    }

    @Transactional
    public ElectronicsProduct addElectronics(ProductForm form) {
        ensureProductCodeAvailable(form.getProductCode());
        return productRepository.save(new ElectronicsProduct(form.getProductCode(), form.getProductName(),
                form.getPrice(), form.getQuantityInStock(), form.getBrand(), form.getWarrantyPeriodYears()));
    }

    @Transactional
    public Product update(Long id, ProductForm form) {
        Product product = get(id);
        product.setProductName(form.getProductName());
        product.setPrice(form.getPrice());
        product.setQuantityInStock(form.getQuantityInStock());
        if (product instanceof ClothingProduct clothing && form.getSize() != null) {
            clothing.setSize(form.getSize());
        }
        if (product instanceof ElectronicsProduct electronics) {
            if (form.getBrand() != null) {
                electronics.setBrand(form.getBrand());
            }
            electronics.setWarrantyPeriodYears(form.getWarrantyPeriodYears());
        }
        return product;
    }

    @Transactional
    public Product restock(Long id, int quantity) {
        Product product = get(id);
        product.restock(quantity);
        return product;
    }

    @Transactional
    public Product deactivate(Long id) {
        Product product = get(id);
        product.setActive(false);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> lowStock(int threshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantityInStock() > 0 && product.getQuantityInStock() <= threshold)
                .sorted(Comparator.comparing(Product::getQuantityInStock))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Product> outOfStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantityInStock() == 0 || !product.isActive())
                .sorted(Comparator.comparing(Product::getProductCode))
                .toList();
    }

    private void ensureProductCodeAvailable(String productCode) {
        if (productRepository.existsByProductCode(productCode)) {
            throw new AppException("Product code already exists.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.toLowerCase(Locale.ROOT).trim();
    }
}
