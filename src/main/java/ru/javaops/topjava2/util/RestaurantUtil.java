package ru.javaops.topjava2.util;

import lombok.experimental.UtilityClass;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.to.RestaurantTo;

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
