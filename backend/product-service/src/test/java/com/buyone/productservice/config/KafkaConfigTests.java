package com.buyone.productservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTests {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");
    }

    @Test
    void testProducerFactory() {
        ProducerFactory<String, Object> factory = kafkaConfig.producerFactory();
        assertNotNull(factory);
    }

    @Test
    void testKafkaTemplate() {
        KafkaTemplate<String, Object> template = kafkaConfig.kafkaTemplate();
        assertNotNull(template);
    }

    @Test
    void testProducerFactoryNotNull() {
        ProducerFactory<String, Object> factory = kafkaConfig.producerFactory();
        assertNotNull(factory);
        assertTrue(factory.getConfigurationProperties().containsKey("bootstrap.servers"));
    }
}
