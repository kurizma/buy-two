package com.buyone.gatewayservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;

@Configuration
public class GatewayDebugConfig implements ApplicationListener<ContextRefreshedEvent> {
    
    private final ListableBeanFactory beanFactory;
    
    public GatewayDebugConfig(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("=== GatewayFilterFactory beans ===");
        beanFactory.getBeansOfType(GatewayFilterFactory.class)
                .forEach((name, bean) ->
                        System.out.println("Factory bean: " + name + " -> " + bean.getClass().getName()));
        System.out.println("=== END ===");
    }
}