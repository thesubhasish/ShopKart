package com.shopkart.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"order.placed.events", "stock.updated.events"})
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, JPA, RestClient, circuit breaker, and Kafka listener config wired correctly.
    }
}
