package com.slooze.SDE.service;

import com.slooze.SDE.model.Country;
import com.slooze.SDE.model.MenuItem;
import com.slooze.SDE.model.Restaurant;
import com.slooze.SDE.model.Role;
import com.slooze.SDE.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> getAllRestaurants(String role, String country) {
        if ("ADMIN".equals(role)) {
            return restaurantRepository.findAll();
        }

        return restaurantRepository.findRestaurantByCountry(Country.valueOf(country));
    }

    public List<MenuItem> getMenuForRestaurant(Long restaurantId, String role, String country) throws Exception {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (!"ADMIN".equals(role) && !restaurant.getCountry().name().equals(country)) {
            throw new AccessDeniedException("You are not allowed to view this restaurant");
        }

        return restaurant.getMenuItemList();
    }
}
