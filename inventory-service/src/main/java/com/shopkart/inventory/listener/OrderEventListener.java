package com.shopkart.inventory.listener;

import com.shopkart.inventory.event.OrderPlacedEvent;
import com.shopkart.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${shopkart.kafka.topics.order-placed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {}", event.orderId());
        // No try/catch here anymore - KafkaErrorHandlerConfig's DefaultErrorHandler now
        // handles exceptions at the container level: 3 retries, then dead-letter topic.
        // Swallowing exceptions here would hide them from that error handler entirely.
        inventoryService.processOrder(event);
    }
}
