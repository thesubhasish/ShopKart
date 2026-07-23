package com.shopkart.order.event;

import java.time.Instant;

/**
 * Mirrors inventory-service's StockUpdatedEvent. Same duplication pattern as
 * OrderPlacedEvent's copy in inventory-service - each side owns its own view of the contract.
 */
public record StockUpdatedEvent(
        Long orderId,
        boolean success,
        String reason,
        Instant occurredAt
) {}
