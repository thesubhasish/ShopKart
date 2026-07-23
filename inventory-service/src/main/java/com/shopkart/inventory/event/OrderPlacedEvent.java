package com.shopkart.inventory.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Mirrors order-service's OrderPlacedEvent. Deliberately duplicated rather than shared -
 * see the same note on ProductInfo in order-service's client package. If order-service
 * changes this shape without telling anyone, deserialization here breaks silently at
 * runtime, which is exactly the coordination risk event-driven architecture introduces.
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
