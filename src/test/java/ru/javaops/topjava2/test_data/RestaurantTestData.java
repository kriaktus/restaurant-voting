package ru.javaops.topjava2.test_data;

import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.web.MatcherFactory;

import java.util.List;

import static ru.javaops.topjava2.test_data.DishTestData.*;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class);
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER_EXCLUDE_DISHES = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "dishes");

    public static final int RESTAURANT1_ID = 1;
    public static final int RESTAURANT2_ID = 2;
    public static final int RESTAURANT3_ID = 3;
    public static final int RESTAURANT4_ID = 4;

    public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Duo Gastrobar", List.of(dish1_1, dish1_2, dish1_3, dish1_4));
    public static final Restaurant restaurant2 = new Restaurant(RESTAURANT2_ID, "tartarbar", List.of());
    public static final Restaurant restaurant3 = new Restaurant(RESTAURANT3_ID, "Duo Asia", List.of());
    public static final Restaurant restaurant4 = new Restaurant(RESTAURANT4_ID, "Мастер Кебаб", List.of());

    public static Restaurant getNewRestaurant(Dish... dishes) {
        return new Restaurant(null, "MODI", List.of(dishes));
    }

    public static Restaurant getUpdatedRestaurant() {
        Restaurant updated = new Restaurant(restaurant1);
        updated.setName("updatedName");
        return updated;
    }
}
