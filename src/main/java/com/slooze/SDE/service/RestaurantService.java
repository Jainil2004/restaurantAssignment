package com.slooze.SDE.service;

import com.slooze.SDE.DTO.MenuItemDto;
import com.slooze.SDE.DTO.RestaurantDto;
import com.slooze.SDE.model.Country;
import com.slooze.SDE.model.Restaurant;
import com.slooze.SDE.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantDto> getAllRestaurants(String role, String country) {
        List<Restaurant> restaurants;
        
        if ("ADMIN".equals(role)) {
            restaurants = restaurantRepository.findAll();
        } else {
            restaurants = restaurantRepository.findRestaurantByCountry(Country.valueOf(country.toUpperCase()));
        }

        // Convert entities to DTOs
        return restaurants.stream()
                .map(restaurant -> {
                    List<MenuItemDto> menuItemDtos = restaurant.getMenuItemList().stream()
                            .map(menuItem -> new MenuItemDto(
                                    menuItem.getId(),
                                    menuItem.getName(),
                                    menuItem.getPrice(),
                                    menuItem.getRestaurant().getId()
                            ))
                            .collect(Collectors.toList());

                    return new RestaurantDto(
                            restaurant.getId(),
                            restaurant.getName(),
                            restaurant.getCountry(),
                            menuItemDtos
                    );
                })
                .collect(Collectors.toList());
    }

    public List<MenuItemDto> getMenuForRestaurant(Long restaurantId, String role, String country) throws Exception {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        if (!"ADMIN".equals(role) && !restaurant.getCountry().name().equals(country.toUpperCase())) {
            throw new AccessDeniedException("You are not allowed to view this restaurant");
        }

        // Convert MenuItem entities to MenuItemDtos
        return restaurant.getMenuItemList().stream()
                .map(menuItem -> new MenuItemDto(
                        menuItem.getId(),
                        menuItem.getName(),
                        menuItem.getPrice(),
                        menuItem.getRestaurant().getId()
                ))
                .collect(Collectors.toList());
    }
}
