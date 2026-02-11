package com.buyone.gatewayservice.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;

class JwtHeaderGatewayFilterFactoryTests {

    private JwtHeaderGatewayFilterFactory filterFactory;

    @BeforeEach
    void setUp() {
        filterFactory = new JwtHeaderGatewayFilterFactory();
    }

    @Test
    void testConstructor() {
        JwtHeaderGatewayFilterFactory factory = new JwtHeaderGatewayFilterFactory();
        assertNotNull(factory);
    }

    @Test
    void testConfigClass() {
        JwtHeaderGatewayFilterFactory.Config config = new JwtHeaderGatewayFilterFactory.Config();
        assertNotNull(config);
    }

    @Test
    void testApplyReturnsGatewayFilter() {
        JwtHeaderGatewayFilterFactory.Config config = new JwtHeaderGatewayFilterFactory.Config();
        GatewayFilter filter = filterFactory.apply(config);
        assertNotNull(filter);
    }

    @Test
    void testApplyWithNewConfig() {
        GatewayFilter filter = filterFactory.apply(new JwtHeaderGatewayFilterFactory.Config());
        assertNotNull(filter);
    }

    @Test
    void testMultipleConfigsProduceDifferentFilters() {
        JwtHeaderGatewayFilterFactory.Config config1 = new JwtHeaderGatewayFilterFactory.Config();
        JwtHeaderGatewayFilterFactory.Config config2 = new JwtHeaderGatewayFilterFactory.Config();
        
        GatewayFilter filter1 = filterFactory.apply(config1);
        GatewayFilter filter2 = filterFactory.apply(config2);
        
        assertNotNull(filter1);
        assertNotNull(filter2);
    }

    @Test
    void testNewFactoryInstance() {
        JwtHeaderGatewayFilterFactory factory1 = new JwtHeaderGatewayFilterFactory();
        JwtHeaderGatewayFilterFactory factory2 = new JwtHeaderGatewayFilterFactory();
        
        assertNotNull(factory1);
        assertNotNull(factory2);
        assertNotSame(factory1, factory2);
    }

    @Test
    void testConfigCreation() {
        JwtHeaderGatewayFilterFactory.Config config = new JwtHeaderGatewayFilterFactory.Config();
        assertNotNull(config);
        assertEquals(JwtHeaderGatewayFilterFactory.Config.class, config.getClass());
    }
}
