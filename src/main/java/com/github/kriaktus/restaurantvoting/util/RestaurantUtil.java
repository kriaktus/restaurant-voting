package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.to.RestaurantWithMenuTo;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;

import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNotFoundWithMessage;

@UtilityClass
public class RestaurantUtil {

    public static Restaurant toRestaurant(RestaurantTo restaurantTo) {
        return new Restaurant(restaurantTo.getId(), restaurantTo.getName(), null);
    }

    public static RestaurantTo toRestaurantTo(Restaurant restaurant) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName());
    }

    public static RestaurantWithMenuTo toRestaurantWithMenuTo(Restaurant restaurant) {
        Menu actualMenu = checkNotFoundWithMessage(restaurant.getMenu().stream().findFirst(), "Actual menu not found");
        return new RestaurantWithMenuTo(restaurant.getId(), restaurant.getName(), MenuUtil.toMenuTo(actualMenu));
    }

    public static List<RestaurantTo> toRestaurantTo(Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantUtil::toRestaurantTo)
                .toList();
    }

    public static List<RestaurantWithMenuTo> toRestaurantWithMenuTo(Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantUtil::toRestaurantWithMenuTo)
                .toList();
    }

    public static Restaurant updateRestaurantFields(Restaurant restaurant, RestaurantTo restaurantTo) {
        restaurant.setName(restaurantTo.getName());
        return restaurant;
    }
}
