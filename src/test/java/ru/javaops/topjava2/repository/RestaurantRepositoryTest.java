package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaops.topjava2.model.Restaurant;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static ru.javaops.topjava2.test_data.RestaurantTestData.*;
import static ru.javaops.topjava2.test_data.UserTestData.NOT_FOUND;

public class RestaurantRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void get() {
        Restaurant actual = restaurantRepository.get(RESTAURANT1_ID);
        RESTAURANT_MATCHER.assertMatch(actual, restaurant1);
    }

    @Test
    public void getNotFound() {
        Assertions.assertNull(restaurantRepository.get(NOT_FOUND));
    }

    @Test
    public void getAll() {
        List<Restaurant> actual = restaurantRepository.getAll();
        RESTAURANT_MATCHER_EXCLUDE_DISHES.assertMatch(actual, restaurant3, restaurant1, restaurant2, restaurant4);
    }

    @Test
    public void create() {
        Restaurant actual = restaurantRepository.save(getNewRestaurant());
        Restaurant expected = getNewRestaurant();
        expected.setId(actual.getId());
        RESTAURANT_MATCHER.assertMatch(actual, expected);
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.get(actual.id()), expected);
    }

    @Test
    public void update() {
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.save(getUpdatedRestaurant()), getUpdatedRestaurant());
    }

    @Test
    public void createDuplicateName() {
        Restaurant duplicateNameRestaurant = getNewRestaurant();
        duplicateNameRestaurant.setName(restaurant1.getName());
        Assertions.assertThrows(DataAccessException.class, () -> restaurantRepository.save(duplicateNameRestaurant));
    }

    @Test
    public void createWithConstraintException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> restaurantRepository.save(new Restaurant(null, List.of())));
        Assertions.assertThrows(ConstraintViolationException.class, () -> restaurantRepository.save(new Restaurant("", List.of())));
        Assertions.assertThrows(ConstraintViolationException.class, () -> restaurantRepository.save(new Restaurant("   ", List.of())));
        Assertions.assertThrows(ConstraintViolationException.class, () -> restaurantRepository.save(new Restaurant("n", List.of())));
        Assertions.assertThrows(ConstraintViolationException.class, () -> restaurantRepository.save(new Restaurant("long".repeat(33), List.of())));
    }

    @Test
    public void delete() {
        Assertions.assertNotNull(restaurantRepository.get(RESTAURANT1_ID));
        Assertions.assertTrue(restaurantRepository.delete(RESTAURANT1_ID) != 0);
        Assertions.assertNull(restaurantRepository.get(RESTAURANT1_ID));
        RESTAURANT_MATCHER_EXCLUDE_DISHES.assertMatch(restaurantRepository.getAll(), restaurant3, restaurant2, restaurant4);
    }

    @Test
    public void deleteNotFound() {
        Assertions.assertNull(restaurantRepository.get(NOT_FOUND));
        Assertions.assertEquals(0, restaurantRepository.delete(NOT_FOUND));
        RESTAURANT_MATCHER_EXCLUDE_DISHES.assertMatch(restaurantRepository.getAll(), restaurant3, restaurant1, restaurant2, restaurant4);
    }
}
