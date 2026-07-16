package com.shopkart.order.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long productId, String productName) {
        super("Product '" + productName + "' (id: " + productId + ") is not currently available");
    }
}
