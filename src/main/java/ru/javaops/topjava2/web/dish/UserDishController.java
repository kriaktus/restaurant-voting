package ru.javaops.topjava2.web.dish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.model.Dish;

import java.util.List;

@RestController
@RequestMapping(value = UserDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserDishController extends AbstractDishController {

    public static final String REST_URL = "/api/restaurants/{restaurantId}/dishes";

    @GetMapping
    public ResponseEntity<List<Dish>> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("UserDishController#getAllByRestaurant(restaurantId:{})", restaurantId);
        List<Dish> dishes = dishRepository.getAllByRestaurantId(restaurantId);
        return dishes.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(dishes);
    }
}
