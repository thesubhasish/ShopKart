package com.shopkart.order.exception;

public class DownstreamServiceException extends RuntimeException {
    public DownstreamServiceException(String serviceName, Throwable cause) {
        super(serviceName + " is currently unreachable, please try again shortly", cause);
    }
}
