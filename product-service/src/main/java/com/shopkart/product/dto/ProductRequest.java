package com.shopkart.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        String imageUrl,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {}
