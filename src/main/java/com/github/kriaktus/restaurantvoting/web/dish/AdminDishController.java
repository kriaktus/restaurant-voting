package com.github.kriaktus.restaurantvoting.web.dish;

import com.github.kriaktus.restaurantvoting.model.Dish;
import com.github.kriaktus.restaurantvoting.to.DishTo;
import com.github.kriaktus.restaurantvoting.util.DishUtil;
import com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.github.kriaktus.restaurantvoting.util.DishUtil.toDishTo;

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "AdminDishController")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
@CacheConfig(cacheNames = "dishes")
public class AdminDishController extends AbstractDishController {
    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";

    @Operation(summary = "#get", description = "Get dish by id from the restaurant (with id={restaurantId}) menu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = DishTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}")
    public DishTo get(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#get(id:{}, restaurantId:{})", id, restaurantId);
        return DishUtil.toDishTo(ValidationUtil.checkNotFoundWithMessage(
                dishRepository.findByIdAndRestaurantId(id, restaurantId),
                String.format("Dish with id=%d and restaurantId=%d not found", id, restaurantId)));
    }

    @Operation(summary = "#createWithLocation", description = "Create new dish from the restaurant (with id={restaurantId}) menu, return in header his url")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = DishTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @PostMapping
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<DishTo> createWithLocation(@Valid @RequestBody DishTo dishTo, @PathVariable int restaurantId) {
        log.info("AdminDishController#create(dishTo:{}, restaurantId:{})", dishTo, restaurantId);
        ValidationUtil.checkNew(dishTo);
        Dish created = dishRepository.save(DishUtil.fromDishToAndRestaurant(dishTo, ValidationUtil.checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId)));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(DishUtil.toDishTo(created));
    }

    @Operation(summary = "#update", description = "Update dish by id from the restaurant (with id={restaurantId}) menu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void update(@Valid @RequestBody DishTo dishTo, @PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#update(dishTo:{}, id:{}, restaurantId:{})", dishTo, id, restaurantId);
        ValidationUtil.assureIdConsistent(dishTo, id);
        dishRepository.save(DishUtil.fromDishToAndRestaurant(dishTo, ValidationUtil.checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId)));
    }

    @Operation(summary = "#delete", description = "Delete dish by id from the restaurant (with id={restaurantId}) menu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminDishController#delete(id:{}, restaurantId:{})", id, restaurantId);
        ValidationUtil.checkModification(dishRepository.delete(id, restaurantId), id);
    }

    @Operation(summary = "#deleteAllByRestaurantId", description = "Delete all dishes from the restaurant (with id={restaurantId}) menu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void deleteAllByRestaurantId(@PathVariable int restaurantId) {
        log.info("AdminDishController#deleteAllByRestaurantId(restaurantId:{})", restaurantId);
        ValidationUtil.checkModification(dishRepository.deleteAllByRestaurantId(restaurantId));
    }
}
