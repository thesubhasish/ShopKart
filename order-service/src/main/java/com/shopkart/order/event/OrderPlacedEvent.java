package com.shopkart.order.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Published to the "order.placed.events" Kafka topic after an order is persisted.
 *
 * This is a public contract: inventory-service (and later notification-service) deserialize
 * this exact shape. Changing a field here without coordinating consumers breaks them silently
 * at runtime, not at compile time - this is the main risk of event-driven architecture that
 * doesn't exist with direct REST calls.
 */
public record OrderPlacedEvent(
        Long orderId,
        Long userId,
        List<OrderedItem> items,
        BigDecimal totalAmount,
        Instant occurredAt
) {
    public record OrderedItem(
            Long productId,
            Integer quantity
    ) {}
}
