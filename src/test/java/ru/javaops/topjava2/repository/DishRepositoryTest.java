package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaops.topjava2.model.Dish;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static ru.javaops.topjava2.test_data.DishTestData.*;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;
import static ru.javaops.topjava2.test_data.UserTestData.NOT_FOUND;

public class DishRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private DishRepository dishRepository;

    @Test
    public void get() {
        Dish actual = dishRepository.get(DISH1_3_ID, RESTAURANT1_ID);
        DISH_MATCHER.assertMatch(actual, dish1_3);
    }

    @Test
    public void getNotFound() {
        Assertions.assertNull(dishRepository.get(NOT_FOUND, RESTAURANT1_ID));
        Assertions.assertNull(dishRepository.get(DISH1_3_ID, NOT_FOUND));
    }

    @Test
    public void getAll() {
        List<Dish> actual = dishRepository.getAllByRestaurantId(RESTAURANT1_ID);
        DISH_MATCHER.assertMatch(actual, dish1_1, dish1_2, dish1_3, dish1_4);
    }

    @Test
    public void getAllNotFound() {
        List<Dish> actual = dishRepository.getAllByRestaurantId(NOT_FOUND);
        DISH_MATCHER.assertMatch(actual, List.of());
    }

    @Test
    public void create() {
        Dish expected = getNewDish();
        Dish actual = dishRepository.save(getNewDish());
        expected.setId(actual.getId());
        DISH_MATCHER.assertMatch(actual, expected);
    }

    @Test
    public void update() {
        Dish expected = getUpdatedDish();
        Dish actual = dishRepository.save(getUpdatedDish());
        DISH_MATCHER.assertMatch(actual, expected);
    }

    @Test
    public void createDuplicateTitleAndRestaurant() {
        Dish duplicate = getNewDish();
        duplicate.setTitle(dish1_1.getTitle());
        duplicate.setRestaurantId(dish1_1.getRestaurantId());
        Assertions.assertThrows(DataAccessException.class, () -> dishRepository.save(duplicate));
    }

    @Test
    public void delete() {
        Assertions.assertNotNull(dishRepository.get(DISH1_3_ID, RESTAURANT1_ID));
        Assertions.assertTrue(dishRepository.delete(DISH1_3_ID, RESTAURANT1_ID) != 0);
        Assertions.assertNull(dishRepository.get(DISH1_3_ID, RESTAURANT1_ID));
    }

    @Test
    public void deleteNotFound() {
        Assertions.assertEquals(0, dishRepository.delete(NOT_FOUND, RESTAURANT1_ID));
        Assertions.assertEquals(0, dishRepository.delete(DISH1_3_ID, NOT_FOUND));
    }

    @Test
    public void deleteAllByRestaurant() {
        Assertions.assertFalse(dishRepository.getAllByRestaurantId(RESTAURANT1_ID).isEmpty());
        Assertions.assertNotEquals(0, dishRepository.deleteAllByRestaurantId(RESTAURANT1_ID));
        Assertions.assertTrue(dishRepository.getAllByRestaurantId(RESTAURANT1_ID).isEmpty());
    }

    @Test
    public void deleteAllNotFound() {
        Assertions.assertEquals(0, dishRepository.deleteAllByRestaurantId(NOT_FOUND));
    }

    @Test
    public void createWithConstraintException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами", 550, null)));

        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish(null, 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("      ", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Д", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами".repeat(5), 550, RESTAURANT1_ID)));

        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами", null, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами", -25, RESTAURANT1_ID)));
    }
}
