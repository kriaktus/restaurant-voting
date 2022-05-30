package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.model.Dish;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.DishTo;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class DishUtil {

    public static Dish fromDishToAndRestaurant(DishTo dishTo, Restaurant restaurant) {
        return new Dish(dishTo.getId(), dishTo.getName(), dishTo.getPrice(), restaurant.id());
    }

    public static DishTo toDishTo(Dish dish) {
        return new DishTo(dish.getId(), dish.getName(), dish.getPrice());
    }

    public static List<DishTo> toDishTo(Collection<Dish> dishes) {
        return dishes.stream()
                .map(DishUtil::toDishTo)
                .toList();
    }

    public static List<DishTo> toDishTo(Dish... dishes) {
        return Arrays.stream(dishes)
                .map(DishUtil::toDishTo)
                .toList();
    }
}
