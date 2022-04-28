package ru.javaops.topjava2.test_data;

import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.web.MatcherFactory;

import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;

public class DishTestData {
    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Dish.class);

    public static final int DISH1_1_ID = 3;
    public static final int DISH1_2_ID = 2;
    public static final int DISH1_3_ID = 1;
    public static final int DISH1_4_ID = 4;

    public static final String DISH1_1_TITLE = "Говядина Топ-блейд с трюфельным пюре";

    public static final Dish dish1_1 = new Dish(DISH1_1_ID, DISH1_1_TITLE, 590, RESTAURANT1_ID);
    public static final Dish dish1_2 = new Dish(DISH1_2_ID, "Паста орзо с шеей бычка", 490, RESTAURANT1_ID);
    public static final Dish dish1_3 = new Dish(DISH1_3_ID, "Тартар из говядины с хумусом и пармезаном", 490, RESTAURANT1_ID);
    public static final Dish dish1_4 = new Dish(DISH1_4_ID, "Чернослив с соленой карамелью и орехом пекан", 390, RESTAURANT1_ID);

    public static Dish getNewDish() {
        return new Dish("Дим самы с телячими хвостами", 550, RESTAURANT1_ID);
    }

    public static Dish getUpdatedDish() {
        Dish updated = new Dish(dish1_3);
        updated.setTitle("Биск из креветок с лймом");
        updated.setCost(590);
        return updated;
    }
}
