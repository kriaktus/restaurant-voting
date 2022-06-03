package com.github.kriaktus.restaurantvoting.test_data;

import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.to.MenuTo;
import com.github.kriaktus.restaurantvoting.web.MatcherFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.github.kriaktus.restaurantvoting.test_data.MenuItemTestData.*;

public class MenuTestData {
    public static final MatcherFactory.Matcher<MenuTo> MENU_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(MenuTo.class);
    public static final int MENU_TO_1_ID = 1;
    public static final MenuTo menuTo1 = new MenuTo(MENU_TO_1_ID, LocalDate.now(), new ArrayList<>(List.of(menuItemTo1_1, menuItemTo1_2, menuItemTo1_3, menuItemTo1_4)));

    public static MenuTo getNewMenuTo() {
        MenuTo newMenuTo = new MenuTo(null, LocalDate.now(), new ArrayList<>());
        List<MenuItemTo> items = newMenuTo.getItems();
        items.add(new MenuItemTo(null, "Блюдо1", 400));
        items.add(new MenuItemTo(null, "Блюдо2", 500));
        items.add(new MenuItemTo(null, "Блюдо3", 600));
        return newMenuTo;
    }

    public static MenuTo getUpdatedMenuTo() {
        MenuTo updatedMenuTo = new MenuTo(menuTo1.getId(), menuTo1.getMenuDate(), new ArrayList<>());
        List<MenuItemTo> items = updatedMenuTo.getItems();
        items.add(new MenuItemTo(MENU_ITEM_TO_1_3_ID, "Блюдо1", 590));
        items.add(new MenuItemTo(MENU_ITEM_TO_1_2_ID, "Блюдо2", 590));
        items.add(new MenuItemTo(MENU_ITEM_TO_1_1_ID, "Блюдо3", 590));
        items.add(new MenuItemTo(MENU_ITEM_TO_1_4_ID, "Тартар из говядины со шпинатом и трюфельным понзу", 590));
        return updatedMenuTo;
    }
}
