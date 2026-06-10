package com.mcdimas.onlineshop.repository;

import com.mcdimas.onlineshop.entity.OrderTrackingEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTrackingEventRepository extends JpaRepository<OrderTrackingEvent, Long> {
    List<OrderTrackingEvent> findByOrderIdOrderByOccurredAtAsc(Long orderId);
}
