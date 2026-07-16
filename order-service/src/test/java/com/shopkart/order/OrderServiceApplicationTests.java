package com.shopkart.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
        // If this passes, JPA, RestClient config, and repositories wired correctly.
    }
}
