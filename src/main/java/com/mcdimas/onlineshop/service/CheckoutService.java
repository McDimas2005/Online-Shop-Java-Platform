package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.entity.Cart;
import com.mcdimas.onlineshop.entity.CartItem;
import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.entity.OrderItem;
import com.mcdimas.onlineshop.exception.AppException;
import com.mcdimas.onlineshop.repository.CustomerOrderRepository;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class CheckoutService {
    private final CartService cartService;
    private final CustomerOrderRepository orderRepository;
    private final OrderTrackingService orderTrackingService;

    public CheckoutService(CartService cartService, CustomerOrderRepository orderRepository,
                           OrderTrackingService orderTrackingService) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.orderTrackingService = orderTrackingService;
    }

    @Transactional
    public CustomerOrder checkout(String email) {
        Cart cart = cartService.cartFor(email);
        if (cart.getItems().isEmpty()) {
            throw new AppException("Checkout cannot happen with an empty cart.");
        }
        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().isAvailable() || item.getQuantity() > item.getProduct().getQuantityInStock()) {
                throw new AppException("Insufficient stock for " + item.getProduct().getProductName() + ".");
            }
        }
        String orderNumber = "OD" + cart.getCustomer().getUser().getUserCode() + "-"
                + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        CustomerOrder order = new CustomerOrder(orderNumber, cart.getCustomer(), cart.total(), cart.getCustomer().getDeliveryDistanceKm());
        for (CartItem item : cart.getItems()) {
            item.getProduct().deductStock(item.getQuantity());
            order.getItems().add(new OrderItem(order, item.getProduct(), item.getQuantity()));
        }
        cart.clear();
        CustomerOrder saved = orderRepository.save(order);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderTrackingService.start(saved.getId());
            }
        });
        return saved;
    }
}
