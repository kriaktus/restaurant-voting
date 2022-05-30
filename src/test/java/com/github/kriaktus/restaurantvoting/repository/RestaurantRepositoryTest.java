package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import com.github.kriaktus.restaurantvoting.error.IllegalRequestDataException;
import com.github.kriaktus.restaurantvoting.model.Restaurant;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.*;

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
        Assertions.assertFalse(restaurantRepository.existsById(UserTestData.NOT_FOUND));
        Assertions.assertThrows(IllegalRequestDataException.class, () -> restaurantRepository.deleteExisted(UserTestData.NOT_FOUND));
        RESTAURANT_MATCHER.assertMatch(restaurantRepository.findAll(Sort.by("name")), restaurant3, restaurant1, restaurant2, restaurant4);
    }
}
