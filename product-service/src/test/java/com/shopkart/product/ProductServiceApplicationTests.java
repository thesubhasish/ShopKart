package com.shopkart.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, JPA, Flyway-equivalent schema, and repositories wired correctly.
    }
}
