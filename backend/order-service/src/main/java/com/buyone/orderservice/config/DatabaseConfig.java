package com.buyone.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB auto-configuration. Spring Boot handles MongoTemplate.
 * DB name from application.yml: spring.data.mongodb.database=orders
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.buyone.orderservice.repository")
public class DatabaseConfig extends AbstractMongoClientConfiguration {
    
    @Override
    protected String getDatabaseName() {
        return "orders";  // Your DB name
    }
}
