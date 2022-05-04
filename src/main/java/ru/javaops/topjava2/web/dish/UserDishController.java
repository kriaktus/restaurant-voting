package ru.javaops.topjava2.web.dish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.to.DishTo;

import java.util.List;

import static ru.javaops.topjava2.util.DishUtil.toDishTo;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNotFoundWithId;

@RestController
@RequestMapping(value = UserDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "dishes")
public class UserDishController extends AbstractDishController {

    public static final String REST_URL = "/api/restaurants/{restaurantId}/dishes";

    @GetMapping
    @Cacheable
    public List<DishTo> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("UserDishController#getAllByRestaurant(restaurantId:{})", restaurantId);
        checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId);
        return toDishTo(dishRepository.findAllByRestaurantIdOrderByTitle(restaurantId));
    }
}
