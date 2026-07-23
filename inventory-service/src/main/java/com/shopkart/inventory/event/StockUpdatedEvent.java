package com.shopkart.inventory.event;

import java.time.Instant;

/**
 * Published to "stock.updated.events" after inventory-service finishes processing an
 * OrderPlacedEvent. order-service will consume this on Day 7 to move the order out of
 * PENDING into CONFIRMED or CANCELLED - this is the second half of the Saga.
 */
public record StockUpdatedEvent(
        Long orderId,
        boolean success,
        String reason,   // null when success=true, human-readable cause when false
        Instant occurredAt
) {
    public static StockUpdatedEvent success(Long orderId) {
        return new StockUpdatedEvent(orderId, true, null, Instant.now());
    }

    public static StockUpdatedEvent failure(Long orderId, String reason) {
        return new StockUpdatedEvent(orderId, false, reason, Instant.now());
    }
}
