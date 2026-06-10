package com.mcdimas.onlineshop.controller;

import com.mcdimas.onlineshop.dto.CartDto;
import com.mcdimas.onlineshop.dto.OrderDto;
import com.mcdimas.onlineshop.dto.ProductDto;
import com.mcdimas.onlineshop.dto.ProductForm;
import com.mcdimas.onlineshop.dto.TrackingEventDto;
import com.mcdimas.onlineshop.entity.ProductCategory;
import com.mcdimas.onlineshop.mapper.ShopMapper;
import com.mcdimas.onlineshop.service.CartService;
import com.mcdimas.onlineshop.service.CheckoutService;
import com.mcdimas.onlineshop.service.OrderService;
import com.mcdimas.onlineshop.service.OrderTrackingService;
import com.mcdimas.onlineshop.service.ProductService;
import com.mcdimas.onlineshop.sse.SseHub;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ShopApiController {
    private final ProductService productService;
    private final CartService cartService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final OrderTrackingService trackingService;
    private final ShopMapper mapper;
    private final SseHub sseHub;

    public ShopApiController(ProductService productService, CartService cartService, CheckoutService checkoutService,
                             OrderService orderService, OrderTrackingService trackingService, ShopMapper mapper,
                             SseHub sseHub) {
        this.productService = productService;
        this.cartService = cartService;
        this.checkoutService = checkoutService;
        this.orderService = orderService;
        this.trackingService = trackingService;
        this.mapper = mapper;
        this.sseHub = sseHub;
    }

    @GetMapping("/api/products")
    public List<ProductDto> products(@RequestParam(required = false) ProductCategory category,
                                     @RequestParam(required = false) String q,
                                     @RequestParam(required = false) String brand,
                                     @RequestParam(required = false) String size,
                                     @RequestParam(required = false) Boolean availableOnly) {
        return productService.search(category, q, brand, size, availableOnly).stream().map(mapper::product).toList();
    }

    @GetMapping("/api/products/{id}")
    public ProductDto product(@PathVariable Long id) {
        return mapper.product(productService.get(id));
    }

    @GetMapping("/api/cart")
    public CartDto cart(Principal principal) {
        return mapper.cart(cartService.cartFor(principal.getName()));
    }

    @PostMapping("/api/cart/items")
    public CartDto addCartItem(Principal principal, @RequestBody Map<String, Integer> request) {
        return mapper.cart(cartService.addItem(principal.getName(), request.get("productId").longValue(), request.getOrDefault("quantity", 1)));
    }

    @PatchMapping("/api/cart/items/{id}")
    public CartDto updateCartItem(Principal principal, @PathVariable Long id, @RequestBody Map<String, Integer> request) {
        return mapper.cart(cartService.updateItem(principal.getName(), id, request.getOrDefault("quantity", 1)));
    }

    @DeleteMapping("/api/cart/items/{id}")
    public CartDto removeCartItem(Principal principal, @PathVariable Long id) {
        return mapper.cart(cartService.removeItem(principal.getName(), id));
    }

    @DeleteMapping("/api/cart")
    public CartDto clearCart(Principal principal) {
        return mapper.cart(cartService.clear(principal.getName()));
    }

    @PostMapping("/api/checkout")
    public OrderDto checkout(Principal principal) {
        return mapper.order(checkoutService.checkout(principal.getName()));
    }

    @GetMapping("/api/orders")
    public List<OrderDto> orders(Principal principal) {
        return orderService.customerOrders(principal.getName()).stream().map(mapper::order).toList();
    }

    @GetMapping("/api/orders/{id}")
    public OrderDto order(Principal principal, @PathVariable Long id) {
        return mapper.order(orderService.customerOrder(id, principal.getName()));
    }

    @GetMapping("/api/orders/{id}/tracking")
    public List<TrackingEventDto> tracking(@PathVariable Long id) {
        return trackingService.events(id);
    }

    @GetMapping("/api/admin/orders")
    public List<OrderDto> adminOrders() {
        return orderService.allOrders().stream().map(mapper::order).toList();
    }

    @PostMapping("/api/admin/products/clothing")
    public ProductDto addClothing(@Valid @RequestBody ProductForm form) {
        return mapper.product(productService.addClothing(form));
    }

    @PostMapping("/api/admin/products/electronics")
    public ProductDto addElectronics(@Valid @RequestBody ProductForm form) {
        return mapper.product(productService.addElectronics(form));
    }

    @PutMapping("/api/admin/products/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @Valid @RequestBody ProductForm form) {
        return mapper.product(productService.update(id, form));
    }

    @PatchMapping("/api/admin/products/{id}/restock")
    public ProductDto restock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        return mapper.product(productService.restock(id, request.getOrDefault("quantity", 1)));
    }

    @PatchMapping("/api/admin/products/{id}/deactivate")
    public ProductDto deactivate(@PathVariable Long id) {
        return mapper.product(productService.deactivate(id));
    }

    @GetMapping("/sse/orders/{orderId}")
    public SseEmitter orderStream(@PathVariable Long orderId) {
        return sseHub.subscribe("order-" + orderId);
    }
}
