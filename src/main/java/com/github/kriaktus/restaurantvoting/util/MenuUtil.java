package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.to.MenuTo;

import java.util.ArrayList;

import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.fromMenuItemToAndRestaurantId;
import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.toMenuItemTo;

public class MenuUtil {

    public static MenuTo toMenuTo(Menu menu) {
        return new MenuTo(menu.getId(), menu.getMenuDate(), toMenuItemTo(new ArrayList<>(menu.getItems())));
    }

    public static Menu fromMenuToAndRestaurant(MenuTo menuTo, Integer restaurantId) {
        return new Menu(menuTo.getId(), menuTo.getMenuDate(), restaurantId, fromMenuItemToAndRestaurantId(menuTo.getItems(), restaurantId));
    }
}
