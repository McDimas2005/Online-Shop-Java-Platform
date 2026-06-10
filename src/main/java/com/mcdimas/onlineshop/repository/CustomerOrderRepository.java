package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.entity.OrderStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomerUserEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    Optional<CustomerOrder> findByIdAndCustomerUserEmailIgnoreCase(Long id, String email);

    long countByStatus(OrderStatus status);
}
