package com.buyone.productservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ContextConfiguration(classes = SecurityConfig.class)
class SecurityConfigTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void securityFilterChainBeanExists() {
        assertTrue(context.containsBean("filterChain"));
    }

    @Test
    void securityFilterChainNotNull() {
        SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);
        assertNotNull(filterChain);
    }
}
