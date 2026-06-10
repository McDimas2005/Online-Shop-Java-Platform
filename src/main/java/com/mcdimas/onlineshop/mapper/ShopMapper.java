package com.mcdimas.onlineshop.mapper;

import com.mcdimas.onlineshop.dto.CartDto;
import com.mcdimas.onlineshop.dto.CartItemDto;
import com.mcdimas.onlineshop.dto.OrderDto;
import com.mcdimas.onlineshop.dto.OrderItemDto;
import com.mcdimas.onlineshop.dto.ProductDto;
import com.mcdimas.onlineshop.dto.TrackingEventDto;
import com.mcdimas.onlineshop.entity.Cart;
import com.mcdimas.onlineshop.entity.CartItem;
import com.mcdimas.onlineshop.entity.ClothingProduct;
import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.entity.ElectronicsProduct;
import com.mcdimas.onlineshop.entity.OrderItem;
import com.mcdimas.onlineshop.entity.OrderTrackingEvent;
import com.mcdimas.onlineshop.entity.Product;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ShopMapper {
    public ProductDto product(Product product) {
        String size = product instanceof ClothingProduct clothing ? clothing.getSize() : null;
        String brand = product instanceof ElectronicsProduct electronics ? electronics.getBrand() : null;
        Integer warranty = product instanceof ElectronicsProduct electronics ? electronics.getWarrantyPeriodYears() : null;
        return new ProductDto(product.getId(), product.getProductCode(), product.getProductName(), product.getPrice(),
                product.getQuantityInStock(), product.isActive(), product.isAvailable(), product.category(), size, brand, warranty);
    }

    public CartDto cart(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream().map(this::cartItem).toList();
        return new CartDto(items, cart.total());
    }

    public CartItemDto cartItem(CartItem item) {
        Product product = item.getProduct();
        return new CartItemDto(item.getId(), product.getId(), product.getProductCode(), product.getProductName(),
                item.getQuantity(), product.getPrice(), item.subtotal());
    }

    public OrderDto order(CustomerOrder order) {
        List<OrderItemDto> items = order.getItems().stream().map(this::orderItem).toList();
        return new OrderDto(order.getId(), order.getOrderNumber(), order.getCreatedAt(), order.getStatus(),
                order.getTotalPrice(), order.getDistanceKm(), order.getProgressPercent(), items);
    }

    public OrderItemDto orderItem(OrderItem item) {
        return new OrderItemDto(item.getProductCode(), item.getProductName(), item.getCategory(), item.getUnitPrice(),
                item.getQuantity(), item.getSubtotal());
    }

    public TrackingEventDto trackingEvent(OrderTrackingEvent event) {
        return new TrackingEventDto(event.getStatus(), event.getProgressPercent(), event.getMessage(), event.getOccurredAt());
    }
}
