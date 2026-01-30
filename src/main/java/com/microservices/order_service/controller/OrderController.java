package com.microservices.order_service.controller;



import com.microservices.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{product}")
    public CompletableFuture<String> createOrder(@PathVariable String product) {
        return orderService.placeOrder(product);
    }
}

