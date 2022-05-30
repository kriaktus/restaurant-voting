package com.github.kriaktus.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import com.github.kriaktus.restaurantvoting.model.Dish;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.DishTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class DishUtil {

    public static Dish fromDishToAndRestaurant(DishTo dishTo, Restaurant restaurant) {
        return new Dish(dishTo.getId(), dishTo.getTitle(), dishTo.getCost(), restaurant.id());
    }

    public static DishTo toDishTo(Dish dish) {
        return new DishTo(dish.getId(), dish.getTitle(), dish.getCost());
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
