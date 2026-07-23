package com.shopkart.inventory.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${shopkart.kafka.topics.stock-updated}")
    private String stockUpdatedTopic;

    public void publish(StockUpdatedEvent event) {
        String key = String.valueOf(event.orderId());

        kafkaTemplate.send(stockUpdatedTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish StockUpdatedEvent for order {}", event.orderId(), ex);
                    } else {
                        log.info("Published StockUpdatedEvent (success={}) for order {}",
                                event.success(), event.orderId());
                    }
                });
    }
}
