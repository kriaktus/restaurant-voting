package com.github.kriaktus.restaurantvoting.test_data;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.util.RestaurantUtil;
import com.github.kriaktus.restaurantvoting.web.MatcherFactory;

import java.util.List;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "dishes");
    public static final MatcherFactory.Matcher<RestaurantTo> RESTAURANT_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(RestaurantTo.class);

    public static final int RESTAURANT1_ID = 1;
    public static final int RESTAURANT2_ID = 2;
    public static final int RESTAURANT3_ID = 3;
    public static final int RESTAURANT4_ID = 4;

    public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Duo Gastrobar", List.of());
    public static final Restaurant restaurant2 = new Restaurant(RESTAURANT2_ID, "tartarbar", List.of());
    public static final Restaurant restaurant3 = new Restaurant(RESTAURANT3_ID, "Duo Asia", List.of());
    public static final Restaurant restaurant4 = new Restaurant(RESTAURANT4_ID, "Мастер Кебаб", List.of());

    public static Restaurant getNewRestaurant(MenuItem... menuItems) {
        return new Restaurant(null, "MODI", List.of(menuItems));
    }

    public static RestaurantTo getUpdatedRestaurantTo() {
        RestaurantTo updated = RestaurantUtil.toRestaurantTo(restaurant1);
        updated.setName("updatedName");
        return updated;
    }
}
