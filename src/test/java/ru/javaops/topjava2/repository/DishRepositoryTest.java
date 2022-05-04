package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaops.topjava2.model.Dish;

import javax.validation.ConstraintViolationException;

import static ru.javaops.topjava2.test_data.DishTestData.*;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;
import static ru.javaops.topjava2.test_data.UserTestData.NOT_FOUND;

public class DishRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private DishRepository dishRepository;

    @Test
    public void createDuplicateTitleAndRestaurant() {
        Dish duplicate = getNewDish();
        duplicate.setTitle(dish1_1.getTitle());
        duplicate.setRestaurantId(dish1_1.getRestaurantId());
        Assertions.assertThrows(DataAccessException.class, () -> dishRepository.save(duplicate));
    }

    @Test
    public void delete() {
        Assertions.assertTrue(dishRepository.findByIdAndRestaurantId(DISH1_3_ID, RESTAURANT1_ID).isPresent());
        Assertions.assertTrue(dishRepository.delete(DISH1_3_ID, RESTAURANT1_ID) != 0);
        Assertions.assertTrue(dishRepository.findByIdAndRestaurantId(DISH1_3_ID, RESTAURANT1_ID).isEmpty());
    }

    @Test
    public void deleteNotFound() {
        Assertions.assertEquals(0, dishRepository.delete(NOT_FOUND, RESTAURANT1_ID));
        Assertions.assertEquals(0, dishRepository.delete(DISH1_3_ID, NOT_FOUND));
    }

    @Test
    public void deleteAllByRestaurant() {
        Assertions.assertFalse(dishRepository.findAllByRestaurantIdOrderByTitle(RESTAURANT1_ID).isEmpty());
        Assertions.assertNotEquals(0, dishRepository.deleteAllByRestaurantId(RESTAURANT1_ID));
        Assertions.assertTrue(dishRepository.findAllByRestaurantIdOrderByTitle(RESTAURANT1_ID).isEmpty());
    }

    @Test
    public void deleteAllNotFound() {
        Assertions.assertEquals(0, dishRepository.deleteAllByRestaurantId(NOT_FOUND));
    }

    @Test
    public void createWithConstraintException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish(null, 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("      ", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Д", 550, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами".repeat(5), 550, RESTAURANT1_ID)));

        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами", null, RESTAURANT1_ID)));
        Assertions.assertThrows(ConstraintViolationException.class, () -> dishRepository.save(new Dish("Дим самы с телячими хвостами", -25, RESTAURANT1_ID)));
    }
}
