package ru.javaops.topjava2.web.restaurant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.to.RestaurantTo;

import javax.validation.Valid;
import java.net.URI;

import static ru.javaops.topjava2.util.RestaurantUtil.*;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNew;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNotFoundWithId;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "restaurants")
public class AdminRestaurantController extends AbstractRestaurantController {

    static final String REST_URL = "/api/admin/restaurants";

    @GetMapping("/{id}")
    public RestaurantTo get(@PathVariable int id) {
        log.info("AdminRestaurantController#get(id:{})", id);
        return toRestaurantTo(checkNotFoundWithId(restaurantRepository.findById(id), id));
    }

    @PostMapping
    @CacheEvict(allEntries = true)
    public ResponseEntity<RestaurantTo> createWithLocation(@Valid @RequestBody RestaurantTo restaurantTo) {
        log.info("AdminRestaurantController#create(restaurantTo:{})", restaurantTo);
        checkNew(restaurantTo);
        Restaurant created = restaurantRepository.save(toRestaurant(restaurantTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").build(created.id());
        return ResponseEntity.created(uriOfNewResource).body(toRestaurantTo(created));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Transactional
    public void update(@Valid @RequestBody RestaurantTo restaurantTo, @PathVariable int id) {
        log.info("AdminRestaurantController#update(restaurantTo:{}, id:{})", restaurantTo, id);
        restaurantRepository.save(updateRestaurantFields(checkNotFoundWithId(restaurantRepository.findById(id), id), restaurantTo));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("AdminRestaurantController#delete(id:{})", id);
        restaurantRepository.deleteExisted(id);
    }
}