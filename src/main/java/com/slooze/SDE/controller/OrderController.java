package com.slooze.SDE.controller;

import com.slooze.SDE.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam Long restaurantId, @RequestBody List<Long> menuItemIds, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String role = request.getAttribute("role").toString();
        String country = request.getAttribute("country").toString();
        String username = request.getAttribute("username").toString();

        try {
            return ResponseEntity.ok(orderService.createOrder(restaurantId, menuItemIds, role, country, username));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<?> checkoutOrder(@PathVariable Long orderId, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        String country = request.getAttribute("country").toString();

        try {
            return ResponseEntity.ok(orderService.checkoutOrder(orderId, role, country));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        String country = request.getAttribute("country").toString();

        try {
            return ResponseEntity.ok(orderService.cancelOrder(orderId, role, country));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
