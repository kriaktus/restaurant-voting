package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import ru.javaops.topjava2.error.IllegalRequestDataException;
import ru.javaops.topjava2.model.Restaurant;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static ru.javaops.topjava2.test_data.RestaurantTestData.*;
import static ru.javaops.topjava2.test_data.UserTestData.NOT_FOUND;

public class RestaurantRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

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
        Assertions.assertTrue(restaurantRepository.existsById(RESTAURANT1_ID));
        restaurantRepository.deleteExisted(RESTAURANT1_ID);
        Assertions.assertFalse(restaurantRepository.existsById(RESTAURANT1_ID));
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.findAll(Sort.by("name")), restaurant3, restaurant2, restaurant4);
    }

    @Test
    public void deleteNotFound() {
        Assertions.assertFalse(restaurantRepository.existsById(NOT_FOUND));
        Assertions.assertThrows(IllegalRequestDataException.class, () -> restaurantRepository.deleteExisted(NOT_FOUND));
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.findAll(Sort.by("name")), restaurant3, restaurant1, restaurant2, restaurant4);
    }
}
