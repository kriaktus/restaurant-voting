package com.github.kriaktus.restaurantvoting.testdata;

import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.to.RestaurantWithMenuTo;
import com.github.kriaktus.restaurantvoting.web.MatcherFactory;

import java.util.Set;

import static com.github.kriaktus.restaurantvoting.testdata.MenuTestData.menuTo1;
import static com.github.kriaktus.restaurantvoting.util.MenuUtil.fromMenuToAndRestaurant;

public class RestaurantTestData {
    public static final MatcherFactory.Matcher<RestaurantTo> RESTAURANT_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(RestaurantTo.class);
    public static final MatcherFactory.Matcher<RestaurantWithMenuTo> RESTAURANT_WITH_MENU_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(RestaurantWithMenuTo.class);

    public static final int RESTAURANT1_ID = 1;
    public static final int RESTAURANT2_ID = 2;
    public static final int RESTAURANT3_ID = 3;
    public static final int RESTAURANT4_ID = 4;

    public static final RestaurantTo restaurantTo1 = new RestaurantTo(RESTAURANT1_ID, "Duo Gastrobar");
    public static final RestaurantTo restaurantTo2 = new RestaurantTo(RESTAURANT2_ID, "tartarbar");
    public static final RestaurantTo restaurantTo3 = new RestaurantTo(RESTAURANT3_ID, "Duo Asia");
    public static final RestaurantTo restaurantTo4 = new RestaurantTo(RESTAURANT4_ID, "Мастер Кебаб");

    public static final Restaurant restaurant1 = new Restaurant(RESTAURANT1_ID, "Duo Gastrobar", null);

    static {
        restaurant1.setMenu(Set.of(fromMenuToAndRestaurant(menuTo1, RESTAURANT1_ID)));
    }

    public static RestaurantTo getNewRestaurantTo() {
        return new RestaurantTo(null, "MODI");
    }

    public static RestaurantTo getUpdatedRestaurantTo() {
        return new RestaurantTo(RESTAURANT1_ID, "updatedName");
    }
}
