package com.shopkart.order.entity;

public enum OrderStatus {
    PENDING,      // just created, not yet confirmed
    CONFIRMED,    // stock reserved, payment successful (Day 5-7)
    CANCELLED,    // stock unavailable or payment failed
    DELIVERED
}
