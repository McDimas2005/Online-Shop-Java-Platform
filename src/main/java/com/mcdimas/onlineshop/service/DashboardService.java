package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.entity.OrderStatus;
import com.mcdimas.onlineshop.repository.CustomerOrderRepository;
import com.mcdimas.onlineshop.repository.ProductRepository;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ProductRepository productRepository;
    private final CustomerOrderRepository orderRepository;

    public DashboardService(ProductRepository productRepository, CustomerOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Long> metrics() {
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.findAll().stream().filter(product -> product.isActive()).count();
        long lowStock = productRepository.findAll().stream()
                .filter(product -> product.getQuantityInStock() > 0 && product.getQuantityInStock() <= 10)
                .count();
        long totalOrders = orderRepository.count();
        long delivered = orderRepository.countByStatus(OrderStatus.DELIVERED);
        long pending = totalOrders - delivered;
        return Map.of(
                "totalProducts", totalProducts,
                "activeProducts", activeProducts,
                "lowStock", lowStock,
                "totalOrders", totalOrders,
                "deliveredOrders", delivered,
                "pendingOrders", pending
        );
    }
}
