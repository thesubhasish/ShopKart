package com.shopkart.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${shopkart.kafka.topics.order-placed}")
    private String orderPlacedTopic;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        // Key by orderId (as String) so all events for the same order land on the same
        // partition, preserving per-order ordering if we ever publish more than one event per order.
        String key = String.valueOf(event.orderId());

        kafkaTemplate.send(orderPlacedTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // Order is already committed to the DB at this point - a publish failure
                        // here means inventory-service never finds out. Logging loudly for now;
                        // a production system would use the Outbox pattern to make this atomic.
                        log.error("Failed to publish OrderPlacedEvent for order {}", event.orderId(), ex);
                    } else {
                        log.info("Published OrderPlacedEvent for order {} to partition {}",
                                event.orderId(), result.getRecordMetadata().partition());
                    }
                });
    }
}
