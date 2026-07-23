package com.shopkart.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    // Optimistic locking: if two orders try to deduct stock for the same product at the
    // same time, the second write to commit will get an OptimisticLockException instead
    // of silently overwriting the first update and overselling stock.
    @Version
    private Long version;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean hasEnoughStock(int requestedQuantity) {
        return availableQuantity >= requestedQuantity;
    }

    public void deduct(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalStateException(
                    "Cannot deduct " + quantity + " units - only " + availableQuantity + " available");
        }
        availableQuantity -= quantity;
    }
}
