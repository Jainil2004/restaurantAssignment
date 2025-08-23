package com.slooze.SDE.controller;

import com.slooze.SDE.model.Order;
import com.slooze.SDE.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam Long restaurantId, @RequestBody List<Long> menuItemIds, HttpRequest request) {
        
    }
}
