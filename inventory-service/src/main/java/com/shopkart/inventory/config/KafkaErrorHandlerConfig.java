package com.shopkart.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Wires a retry-then-dead-letter policy into the auto-configured Kafka listener container.
 * Spring Boot detects this single CommonErrorHandler bean and applies it to every
 * @KafkaListener automatically - no need to touch OrderEventListener itself.
 *
 * Without this, an exception thrown while processing a message (e.g. a transient DB
 * connection blip) would either crash the listener thread or silently drop the message,
 * depending on how it's caught. This gives failing messages 3 retries with a 1s pause,
 * and only after all 3 fail does it publish to "<topic>.DLT" for manual inspection -
 * the main topic keeps flowing for every other message in the meantime.
 */
@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaOperations<Object, Object> kafkaOperations) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaOperations);

        // 3 retries, 1 second apart, before giving up and routing to the DLT
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
