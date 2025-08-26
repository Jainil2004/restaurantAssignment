package com.slooze.SDE.controller;

import com.slooze.SDE.model.Restaurant;
import com.slooze.SDE.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<?> getAllRestaurants(HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        String country = request.getAttribute("country").toString();

        try {
            return ResponseEntity.ok(restaurantService.getAllRestaurants(role, country));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<?> getMenuForRestaurant(@PathVariable Long id, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        String country = request.getAttribute("country").toString();

        try {
            return ResponseEntity.ok(restaurantService.getMenuForRestaurant(id, role, country));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
