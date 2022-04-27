package ru.javaops.topjava2.web.dish;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.util.validation.ValidationUtil;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AdminDishController extends AbstractDishController {
    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";

    @GetMapping("/{id}")
    public Dish get(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#get(id:{}, restaurantId:{})", id, restaurantId);
        return dishRepository.get(id, restaurantId);
    }

    @PostMapping
    public ResponseEntity<Dish> create(@Valid @RequestBody Dish dish, @PathVariable int restaurantId, BindingResult bindingResult) {
        log.info("AdminDishController#create(dish:{}, restaurantId:{}, bindingResult.hasErrors():{})", dish, restaurantId, bindingResult.hasErrors());
        ValidationUtil.checkNew(dish);
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    public void update(@Valid @RequestBody Dish dish, @PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#update(dish:{}, id:{}, restaurantId:{})", dish, id, restaurantId);
        ValidationUtil.assureIdConsistent(dish, id);
        dishRepository.save(dish);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#delete(id:{}, restaurantId:{})", id, restaurantId);
        ValidationUtil.checkModification(dishRepository.delete(id, restaurantId), id);
    }

    @DeleteMapping
    public void deleteAllByRestaurantId(@PathVariable int restaurantId) {
        log.info("AdminDishController#deleteAllByRestaurantId(restaurantId:{})", restaurantId);
        ValidationUtil.checkModification(dishRepository.deleteAllByRestaurantId(restaurantId));
    }
}
