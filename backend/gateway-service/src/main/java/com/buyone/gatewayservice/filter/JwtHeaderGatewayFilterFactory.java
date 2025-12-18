package com.buyone.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtHeaderGatewayFilterFactory.Config> {

    public JwtHeaderGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .cast(JwtAuthenticationToken.class)
                        .map(auth -> {
                            Jwt jwt = auth.getToken();
                            String userId = jwt.getClaimAsString("id");
                            String role = jwt.getClaimAsString("role");
                            ServerHttpRequest request = exchange.getRequest()
                                    .mutate()
                                    .header("X-USER-ID", userId)
                                    .header("X-USER-ROLE", role)
                                    .build();
                            return exchange.mutate().request(request).build();
                        })
                        .defaultIfEmpty(exchange)
                        .flatMap(chain::filter);
    }
    
    public static class Config {} // no custom config
}
