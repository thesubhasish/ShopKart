package com.shopkart.order.dto;

import com.shopkart.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt()
        );
    }
}
