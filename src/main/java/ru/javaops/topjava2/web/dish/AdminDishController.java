package ru.javaops.topjava2.web.dish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.to.DishTo;

import javax.validation.Valid;
import java.net.URI;

import static ru.javaops.topjava2.util.DishUtil.fromDishToAndRestaurant;
import static ru.javaops.topjava2.util.DishUtil.toDishTo;
import static ru.javaops.topjava2.util.validation.ValidationUtil.*;

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "dishes")
public class AdminDishController extends AbstractDishController {
    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";

    @GetMapping("/{id}")
    public DishTo get(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#get(id:{}, restaurantId:{})", id, restaurantId);
        return toDishTo(checkNotFoundWithMessage(
                dishRepository.findByIdAndRestaurantId(id, restaurantId),
                String.format("Dish with id=%d and restaurantId=%d not found", id, restaurantId)));
    }

    @PostMapping
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<DishTo> create(@Valid @RequestBody DishTo dishTo, @PathVariable int restaurantId) {
        log.info("AdminDishController#create(dishTo:{}, restaurantId:{})", dishTo, restaurantId);
        checkNew(dishTo);
        Dish created = dishRepository.save(fromDishToAndRestaurant(dishTo, checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId)));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(toDishTo(created));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void update(@Valid @RequestBody DishTo dishTo, @PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#update(dishTo:{}, id:{}, restaurantId:{})", dishTo, id, restaurantId);
        assureIdConsistent(dishTo, id);
        dishRepository.save(fromDishToAndRestaurant(dishTo, checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#delete(id:{}, restaurantId:{})", id, restaurantId);
        checkModification(dishRepository.delete(id, restaurantId), id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void deleteAllByRestaurantId(@PathVariable int restaurantId) {
        log.info("AdminDishController#deleteAllByRestaurantId(restaurantId:{})", restaurantId);
        checkModification(dishRepository.deleteAllByRestaurantId(restaurantId));
    }
}
