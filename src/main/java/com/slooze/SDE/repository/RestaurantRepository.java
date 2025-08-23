package com.slooze.SDE.repository;

import com.slooze.SDE.model.Country;
import com.slooze.SDE.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findRestaurantByCountry(Country country);
}
