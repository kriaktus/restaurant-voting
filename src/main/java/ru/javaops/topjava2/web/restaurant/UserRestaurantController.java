package ru.javaops.topjava2.web.restaurant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javaops.topjava2.to.RestaurantTo;

import java.util.List;

import static ru.javaops.topjava2.util.RestaurantUtil.toRestaurantTo;

@RestController
@RequestMapping(value = UserRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "restaurants")
public class UserRestaurantController extends AbstractRestaurantController {

    static final String REST_URL = "/api/restaurants";

    @GetMapping
    @Cacheable
    public List<RestaurantTo> getAll() {
        log.info("UserRestaurantController#getAll()");
        return toRestaurantTo(restaurantRepository.findAll(Sort.by("name")));
    }
}
