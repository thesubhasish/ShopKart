package com.shopkart.order.dto;

import java.math.BigDecimal;

/**
 * Mirrors the relevant fields of ProductResponse from product-service.
 * Deliberately NOT a shared library between services - each microservice owns its
 * own view of what it needs from another service's API. If product-service adds
 * fields we don't care about, this record simply ignores them (Jackson default).
 */
public record ProductInfo(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        boolean active
) {}
