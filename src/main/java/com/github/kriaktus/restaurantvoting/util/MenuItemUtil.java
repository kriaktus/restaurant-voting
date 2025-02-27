package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class MenuItemUtil {

    public static MenuItem fromMenuItemToAndRestaurantId(MenuItemTo menuItemTo, Integer restaurantId) {
        return new MenuItem(menuItemTo.getId(), menuItemTo.getName(), menuItemTo.getPrice(), restaurantId);
    }

    public static Set<MenuItem> fromMenuItemToAndRestaurantId(Collection<MenuItemTo> menuItemsTo, Integer restaurantId) {
        return menuItemsTo.stream()
                .map(menuItemTo -> MenuItemUtil.fromMenuItemToAndRestaurantId(menuItemTo, restaurantId))
                .collect(Collectors.toSet());
    }

    public static MenuItemTo toMenuItemTo(MenuItem menuItem) {
        return new MenuItemTo(menuItem.getId(), menuItem.getName(), menuItem.getPrice());
    }

    public static List<MenuItemTo> toMenuItemTo(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(MenuItemUtil::toMenuItemTo)
                .toList();
    }
}
