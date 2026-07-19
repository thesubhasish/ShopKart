package com.shopkart.order.client;

import com.shopkart.order.dto.ProductInfo;
import com.shopkart.order.exception.DownstreamServiceException;
import com.shopkart.order.exception.ProductNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * All calls to Product Service go through here - the one place inter-service HTTP
 * concerns live, including the circuit breaker below.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient productServiceRestClient;

    /**
     * "productService" here refers to the resilience4j.circuitbreaker.instances.productService
     * config block in application.yml, not to the product-service module itself.
     * ProductNotFoundException is deliberately NOT caught by the breaker - a 404 for one
     * bad product ID is a normal business outcome, not a sign product-service is unhealthy.
     * It's excluded via resilience4j.circuitbreaker.instances.productService.ignore-exceptions.
     */
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Retry(name = "productService")
    public ProductInfo getProduct(Long productId) {
        try {
            return productServiceRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductInfo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        } catch (ResourceAccessException ex) {
            // Connection refused, timeout, DNS failure, etc. - product-service itself is unreachable
            log.error("Product Service unreachable while fetching product {}", productId, ex);
            throw new DownstreamServiceException("Product Service", ex);
        }
    }

    /**
     * Invoked when the circuit is OPEN (failing fast without even calling product-service)
     * or when @Retry exhausts its attempts. Never invoked for ProductNotFoundException,
     * since that's excluded from the breaker's failure counting entirely.
     */
    private ProductInfo getProductFallback(Long productId, Throwable throwable) {
        log.error("Circuit breaker fallback triggered for product {}: {}",
                productId, throwable.getMessage());
        throw new DownstreamServiceException("Product Service", throwable);
    }
}
