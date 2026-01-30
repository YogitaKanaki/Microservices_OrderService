package com.microservices.order_service.service;



import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    private final WebClient webClient;

    public OrderService(WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventory")
    @Retry(name = "inventoryService")
    @TimeLimiter(name = "inventoryService")
    public CompletableFuture<String> placeOrder(String product) {
        return webClient.get()
                .uri("/inventory/{product}", product)
                .retrieve()
                .bodyToMono(Boolean.class)
                .map(inStock -> inStock
                        ? "Order placed for " + product
                        : "Product out of stock")
                .toFuture();
    }

    // fallback must match signature: (same args + Throwable)
    public CompletableFuture<String> fallbackInventory(String product, Throwable ex) {
        return CompletableFuture.completedFuture(
                "Inventory service unavailable. Please try later."
        );
    }
}

