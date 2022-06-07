package com.github.kriaktus.restaurantvoting.testdata;

import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.web.MatcherFactory;

public class MenuItemTestData {
    public static final MatcherFactory.Matcher<MenuItemTo> MENU_ITEM_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(MenuItemTo.class);

    public static final int MENU_ITEM_TO_1_1_ID = 1;
    public static final int MENU_ITEM_TO_1_2_ID = 2;
    public static final int MENU_ITEM_TO_1_3_ID = 3;
    public static final int MENU_ITEM_TO_1_4_ID = 4;
    public static final int MENU_ITEM_TO_2_1_ID = 5;

    public static final String MENU_ITEM_TO_1_3_TITLE = "Говядина Топ-блейд с трюфельным пюре";

    public static final MenuItemTo menuItemTo1_1 = new MenuItemTo(MENU_ITEM_TO_1_1_ID, "Тартар из говядины с хумусом и пармезаном", 490);
    public static final MenuItemTo menuItemTo1_2 = new MenuItemTo(MENU_ITEM_TO_1_2_ID, "Паста орзо с шеей бычка", 490);
    public static final MenuItemTo menuItemTo1_3 = new MenuItemTo(MENU_ITEM_TO_1_3_ID, MENU_ITEM_TO_1_3_TITLE, 590);
    public static final MenuItemTo menuItemTo1_4 = new MenuItemTo(MENU_ITEM_TO_1_4_ID, "Чернослив с соленой карамелью и орехом пекан", 390);
    public static final MenuItemTo menuItemTo2_1 = new MenuItemTo(MENU_ITEM_TO_2_1_ID, "Тартар из говядины со шпинатом и трюфельным понзу", 490);

    public static MenuItemTo getNewMenuItemTo() {
        return new MenuItemTo(null, "Дим самы с телячими хвостами", 550);
    }

    public static MenuItemTo getUpdatedMenuItemTo() {
        MenuItemTo updated = new MenuItemTo(menuItemTo1_1);
        updated.setName("Биск из креветок с лймом");
        return updated;
    }

    public static MenuItemTo getDuplicateNameMenuItemTo() {
        return new MenuItemTo(null, MenuItemTestData.MENU_ITEM_TO_1_3_TITLE, 590);
    }
}
