package com.github.kriaktus.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class RestaurantUtil {

    public static Restaurant toRestaurant(RestaurantTo restaurantTo) {
        return new Restaurant(restaurantTo.getId(), restaurantTo.getName(), null);
    }

    public static RestaurantTo toRestaurantTo(Restaurant restaurant) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName());
    }

    public static List<RestaurantTo> toRestaurantTo(Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantUtil::toRestaurantTo)
                .toList();
    }

    public static List<RestaurantTo> toRestaurantTo(Restaurant... restaurants) {
        return Arrays.stream(restaurants)
                .map(RestaurantUtil::toRestaurantTo)
                .toList();
    }

    public static Restaurant updateRestaurantFields(Restaurant restaurant, RestaurantTo restaurantTo) {
        restaurant.setName(restaurantTo.getName());
        return restaurant;
    }
}
