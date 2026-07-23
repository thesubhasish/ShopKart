package com.shopkart.inventory.service;

import com.shopkart.inventory.entity.InventoryItem;
import com.shopkart.inventory.event.OrderPlacedEvent;
import com.shopkart.inventory.event.StockEventPublisher;
import com.shopkart.inventory.event.StockUpdatedEvent;
import com.shopkart.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockEventPublisher stockEventPublisher;

    /**
     * Processes one OrderPlacedEvent: checks every line item has enough stock, and only
     * if ALL items are available does it deduct any of them. Either the whole order
     * succeeds or none of it does - a half-fulfilled order would be worse than rejecting it.
     */
    @Transactional
    public void processOrder(OrderPlacedEvent event) {
        log.info("Processing stock reservation for order {}", event.orderId());

        for (OrderPlacedEvent.OrderedItem orderedItem : event.items()) {
            InventoryItem item = inventoryRepository.findByProductId(orderedItem.productId())
                    .orElse(null);

            if (item == null) {
                log.warn("No inventory record for product {} - failing order {}",
                        orderedItem.productId(), event.orderId());
                stockEventPublisher.publish(StockUpdatedEvent.failure(
                        event.orderId(),
                        "No inventory record for product " + orderedItem.productId()));
                return;
            }

            if (!item.hasEnoughStock(orderedItem.quantity())) {
                log.warn("Insufficient stock for product {} (have {}, need {}) - failing order {}",
                        orderedItem.productId(), item.getAvailableQuantity(),
                        orderedItem.quantity(), event.orderId());
                stockEventPublisher.publish(StockUpdatedEvent.failure(
                        event.orderId(),
                        "Insufficient stock for product " + orderedItem.productId()));
                return;
            }
        }

        // Second pass: every item confirmed available above, now actually deduct.
        // Splitting check-then-deduct into two passes avoids partially deducting stock
        // for items 1-2 and then discovering item 3 is unavailable.
        try {
            for (OrderPlacedEvent.OrderedItem orderedItem : event.items()) {
                InventoryItem item = inventoryRepository.findByProductId(orderedItem.productId())
                        .orElseThrow(); // already verified to exist above
                item.deduct(orderedItem.quantity());
                inventoryRepository.save(item);
            }
        } catch (ObjectOptimisticLockingFailureException ex) {
            // Another order modified the same product's stock between our check and our
            // write. Safer to fail this order and let the customer retry than to guess.
            log.warn("Concurrent stock update conflict while processing order {}", event.orderId(), ex);
            stockEventPublisher.publish(StockUpdatedEvent.failure(
                    event.orderId(), "Stock changed concurrently, please retry"));
            return;
        }

        log.info("Stock reserved successfully for order {}", event.orderId());
        stockEventPublisher.publish(StockUpdatedEvent.success(event.orderId()));
    }
}
