package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.to.MenuTo;

public class MenuUtil {

    public static MenuTo toMenuTo(Menu menu) {
        return new MenuTo(menu.getId(), menu.getMenuDate(), MenuItemUtil.toMenuItemTo(menu.getItems()));
    }

    public static Menu fromMenuToAndRestaurant(MenuTo menuTo, Integer restaurantId) {
        return new Menu(menuTo.getId(), menuTo.getMenuDate(), restaurantId, MenuItemUtil.fromMenuItemToAndRestaurantId(menuTo.getItems(), restaurantId));
    }
}
