package com.shopkart.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"order.placed.events", "stock.updated.events"})
class InventoryServiceApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, JPA, Kafka listener/producer config, and repositories wired correctly.
    }
}
