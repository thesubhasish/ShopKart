package com.shopkart.order.client;

import com.shopkart.order.dto.ProductInfo;
import com.shopkart.order.exception.DownstreamServiceException;
import com.shopkart.order.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * All calls to Product Service go through here. If Product Service is down or slow,
 * this is the one place that needs to change (e.g. adding Resilience4j circuit breaker
 * on Day 7) rather than scattered RestClient calls across the codebase.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient productServiceRestClient;

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
}
