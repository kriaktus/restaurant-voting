package com.github.kriaktus.restaurantvoting.repository;

import org.springframework.transaction.annotation.Transactional;
import com.github.kriaktus.restaurantvoting.model.Restaurant;

import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    Optional<Restaurant> getRestaurantByName(String name);
}
