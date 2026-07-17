package com.shopkart.order.service;

import com.shopkart.order.client.ProductServiceClient;
import com.shopkart.order.dto.CreateOrderRequest;
import com.shopkart.order.dto.OrderItemRequest;
import com.shopkart.order.dto.OrderResponse;
import com.shopkart.order.dto.ProductInfo;
import com.shopkart.order.entity.Order;
import com.shopkart.order.entity.OrderItem;
import com.shopkart.order.entity.OrderStatus;
import com.shopkart.order.event.OrderEventPublisher;
import com.shopkart.order.event.OrderPlacedEvent;
import com.shopkart.order.exception.OrderNotFoundException;
import com.shopkart.order.exception.ProductUnavailableException;
import com.shopkart.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            // Fetch live product data - price and availability are always checked
            // fresh at order time, never trusted from the client request.
            ProductInfo product = productServiceClient.getProduct(itemRequest.productId());

            if (!product.active()) {
                throw new ProductUnavailableException(product.id(), product.name());
            }

            BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem item = OrderItem.builder()
                    .productId(product.id())
                    .productName(product.name())
                    .unitPrice(product.price())
                    .quantity(itemRequest.quantity())
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        // Registered to fire only AFTER the transaction commits - if anything rolls back
        // the order save, this callback never runs, so we never publish an event for an
        // order that doesn't actually exist in the DB.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderEventPublisher.publishOrderPlaced(toEvent(saved));
            }
        });

        return OrderResponse.from(saved);
    }

    private OrderPlacedEvent toEvent(Order order) {
        List<OrderPlacedEvent.OrderedItem> items = order.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderedItem(item.getProductId(), item.getQuantity()))
                .toList();

        return new OrderPlacedEvent(
                order.getId(),
                order.getUserId(),
                items,
                order.getTotalAmount(),
                Instant.now()
        );
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }
}
