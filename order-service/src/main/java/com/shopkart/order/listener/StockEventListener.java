package com.shopkart.order.listener;

import com.shopkart.order.event.StockUpdatedEvent;
import com.shopkart.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventListener {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${shopkart.kafka.topics.stock-updated}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onStockUpdated(StockUpdatedEvent event) {
        log.info("Received StockUpdatedEvent for order {} (success={})", event.orderId(), event.success());
        // No try/catch - KafkaErrorHandlerConfig's DefaultErrorHandler handles retries
        // and dead-lettering at the container level. See inventory-service for the same pattern.
        orderService.applyStockResult(event.orderId(), event.success(), event.reason());
    }
}
