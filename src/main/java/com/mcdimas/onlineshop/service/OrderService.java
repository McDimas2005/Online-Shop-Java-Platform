package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.exception.NotFoundException;
import com.mcdimas.onlineshop.repository.CustomerOrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final CustomerOrderRepository orderRepository;

    public OrderService(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> customerOrders(String email) {
        return orderRepository.findByCustomerUserEmailIgnoreCaseOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public CustomerOrder customerOrder(Long id, String email) {
        return orderRepository.findByIdAndCustomerUserEmailIgnoreCase(id, email)
                .orElseThrow(() -> new NotFoundException("Order not found."));
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> allOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CustomerOrder get(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found."));
    }
}
