package com.shopkart.product.dto;

import com.shopkart.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String categoryName,
        String categorySlug,
        boolean active,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                product.isActive(),
                product.getCreatedAt()
        );
    }
}
