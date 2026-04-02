package com.trainticket.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public reactor.core.publisher.Mono<Void> filter(org.springframework.cloud.gateway.filter.ServerWebExchange exchange,
                                                    org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethodValue();
        log.info("Gateway request: {} {}", method, path);
        return chain.filter(exchange);
    }
}
