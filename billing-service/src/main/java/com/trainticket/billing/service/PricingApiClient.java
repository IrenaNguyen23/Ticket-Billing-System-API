package com.trainticket.billing.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PricingApiClient {

    private final WebClient webClient;

    @Value("${app.pricing.base-url:https://pricing-api.example.com}")
    private String baseUrl;

    @CircuitBreaker(name = "pricing-api", fallbackMethod = "fallback")
    public PricingResponse getFare(String fromStationId, String toStationId) {
        return webClient.get()
                .uri(baseUrl + "/v1/fare?from={from}&to={to}&type=standard", fromStationId, toStationId)
                .retrieve()
                .bodyToMono(PricingResponse.class)
                .block();
    }

    private PricingResponse fallback(String fromStationId, String toStationId, Throwable ex) {
        return new PricingResponse(0L, "VND", "UNKNOWN");
    }

    @Data
    @AllArgsConstructor
    public static class PricingResponse {
        private long fare;
        private String currency;
        private String zone;
    }
}
