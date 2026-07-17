package com.shopkart.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${shopkart.kafka.topics.order-placed}")
    private String orderPlacedTopic;

    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name(orderPlacedTopic)
                .partitions(3)     // allows 3 consumer instances to process in parallel later
                .replicas(1)       // single broker in local dev; bump to 3 in a real cluster
                .build();
    }
}
