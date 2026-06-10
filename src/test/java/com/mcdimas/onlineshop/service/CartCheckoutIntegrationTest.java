package com.mcdimas.onlineshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mcdimas.onlineshop.dto.ProductForm;
import com.mcdimas.onlineshop.dto.RegisterRequest;
import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.entity.OrderStatus;
import com.mcdimas.onlineshop.entity.Product;
import com.mcdimas.onlineshop.exception.AppException;
import com.mcdimas.onlineshop.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CartCheckoutIntegrationTest {
    @Autowired
    AccountService accountService;
    @Autowired
    ProductService productService;
    @Autowired
    CartService cartService;
    @Autowired
    CheckoutService checkoutService;
    @Autowired
    OrderTrackingService trackingService;
    @Autowired
    ProductRepository productRepository;

    @Test
    void cartAddUpdateRemoveAndTotalsWorkWithoutDeductingStock() {
        String email = uniqueEmail();
        accountService.registerCustomer(new RegisterRequest("Cart User", email, "password123", "Test Street"));
        Product product = productService.addClothing(productForm("PT" + shortId(), "Test Shirt", "19.99", 5, "M"));

        var cart = cartService.addItem(email, product.getId(), 2);
        assertThat(cart.total()).isEqualByComparingTo("39.98");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getQuantityInStock()).isEqualTo(5);

        cart = cartService.updateItem(email, cart.getItems().getFirst().getId(), 3);
        assertThat(cart.total()).isEqualByComparingTo("59.97");

        cart = cartService.removeItem(email, cart.getItems().getFirst().getId());
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void checkoutCreatesOrderSnapshotsDeductsStockAndClearsCart() {
        String email = uniqueEmail();
        accountService.registerCustomer(new RegisterRequest("Checkout User", email, "password123", "Test Street"));
        Product product = productService.addClothing(productForm("PT" + shortId(), "Snapshot Shirt", "20.00", 3, "L"));
        cartService.addItem(email, product.getId(), 2);

        CustomerOrder order = checkoutService.checkout(email);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().getFirst().getProductName()).isEqualTo("Snapshot Shirt");
        assertThat(order.getItems().getFirst().getUnitPrice()).isEqualByComparingTo("20.00");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getQuantityInStock()).isEqualTo(1);
        assertThat(cartService.cartFor(email).getItems()).isEmpty();
    }

    @Test
    void checkoutRejectsEmptyCartAndInsufficientStock() {
        String email = uniqueEmail();
        accountService.registerCustomer(new RegisterRequest("Failure User", email, "password123", "Test Street"));
        Product product = productService.addClothing(productForm("PT" + shortId(), "Limited Shirt", "20.00", 1, "S"));

        assertThatThrownBy(() -> checkoutService.checkout(email))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("empty cart");

        assertThatThrownBy(() -> cartService.addItem(email, product.getId(), 2))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Not enough stock");
    }

    @Test
    void trackingEventsCanProgressAnOrder() {
        String email = uniqueEmail();
        accountService.registerCustomer(new RegisterRequest("Tracking User", email, "password123", "Test Street"));
        Product product = productService.addClothing(productForm("PT" + shortId(), "Tracking Shirt", "20.00", 2, "M"));
        cartService.addItem(email, product.getId(), 1);
        CustomerOrder order = checkoutService.checkout(email);

        trackingService.record(order.getId(), OrderStatus.PACKING, 30, "Packing test order.");
        trackingService.record(order.getId(), OrderStatus.DELIVERED, 100, "Delivered test order.");

        assertThat(trackingService.events(order.getId())).extracting("status")
                .contains(OrderStatus.PACKING, OrderStatus.DELIVERED);
    }

    private ProductForm productForm(String code, String name, String price, int stock, String size) {
        ProductForm form = new ProductForm();
        form.setProductCode(code);
        form.setProductName(name);
        form.setPrice(new BigDecimal(price));
        form.setQuantityInStock(stock);
        form.setSize(size);
        return form;
    }

    private String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }

    private String shortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
