package com.mcdimas.onlineshop.service;

import com.mcdimas.onlineshop.dto.TrackingEventDto;
import com.mcdimas.onlineshop.entity.CustomerOrder;
import com.mcdimas.onlineshop.entity.OrderStatus;
import com.mcdimas.onlineshop.entity.OrderTrackingEvent;
import com.mcdimas.onlineshop.exception.NotFoundException;
import com.mcdimas.onlineshop.mapper.ShopMapper;
import com.mcdimas.onlineshop.repository.CustomerOrderRepository;
import com.mcdimas.onlineshop.repository.OrderTrackingEventRepository;
import com.mcdimas.onlineshop.sse.SseHub;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class OrderTrackingService {
    private final CustomerOrderRepository orderRepository;
    private final OrderTrackingEventRepository eventRepository;
    private final SseHub sseHub;
    private final ShopMapper mapper;
    private final long delayMs;
    private final TransactionTemplate transactionTemplate;

    public OrderTrackingService(CustomerOrderRepository orderRepository,
                                OrderTrackingEventRepository eventRepository,
                                SseHub sseHub,
                                ShopMapper mapper,
                                PlatformTransactionManager transactionManager,
                                @Value("${app.tracking.delay-ms:1200}") long delayMs) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.sseHub = sseHub;
        this.mapper = mapper;
        this.delayMs = delayMs;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Async("orderTaskExecutor")
    public void start(Long orderId) {
        record(orderId, OrderStatus.ACCEPTED, 5, "Order accepted and queued for packing.");
        sleep();
        record(orderId, OrderStatus.PACKING, 25, "Warehouse team is packing the products.");
        sleep();
        record(orderId, OrderStatus.PACKING, 45, "All items have been checked and packed.");
        sleep();
        record(orderId, OrderStatus.SHIPPING, 65, "Order has left the warehouse.");
        sleep();
        record(orderId, OrderStatus.SHIPPING, 85, "Courier is moving across the simulated delivery distance.");
        sleep();
        record(orderId, OrderStatus.DELIVERED, 100, "Order delivered successfully.");
    }

    public TrackingEventDto record(Long orderId, OrderStatus status, int progress, String message) {
        TrackingEventDto dto = transactionTemplate.execute(transactionStatus -> {
            CustomerOrder order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found."));
            order.setStatus(status);
            order.setProgressPercent(progress);
            OrderTrackingEvent event = eventRepository.save(new OrderTrackingEvent(order, status, progress, message));
            return mapper.trackingEvent(event);
        });
        sseHub.publish("order-" + orderId, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TrackingEventDto> events(Long orderId) {
        return eventRepository.findByOrderIdOrderByOccurredAtAsc(orderId).stream()
                .map(mapper::trackingEvent)
                .toList();
    }

    private void sleep() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
