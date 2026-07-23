package com.shopkart.gateway.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Replaces WebFlux's default error attribute map (which includes a full stack trace field
 * and internal exception class names) with a clean, consistent shape - the same
 * ErrorResponse contract every other ShopKart service already uses.
 */
@Configuration
public class GatewayErrorConfig {

    @Bean
    public DefaultErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
                Map<String, Object> original = super.getErrorAttributes(request, options);

                Map<String, Object> clean = new LinkedHashMap<>();
                clean.put("timestamp", LocalDateTime.now().toString());
                clean.put("status", original.getOrDefault("status", 500));
                clean.put("error", original.getOrDefault("error", "Internal Server Error"));
                clean.put("message", "The requested service is currently unavailable. Please try again shortly.");
                clean.put("path", original.get("path"));

                return clean;
            }
        };
    }
}
