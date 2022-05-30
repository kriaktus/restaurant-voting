package com.github.kriaktus.restaurantvoting.web.restaurant;

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
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;

import javax.validation.Valid;
import java.net.URI;

import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.*;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNew;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNotFoundWithId;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "AdminRestaurantController")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
@CacheConfig(cacheNames = "restaurants")
public class AdminRestaurantController extends AbstractRestaurantController {

    static final String REST_URL = "/api/admin/restaurants";

    @Operation(summary = "#get", description = "Get restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RestaurantTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}")
    public RestaurantTo get(@PathVariable int id) {
        log.info("AdminRestaurantController#get(id:{})", id);
        return toRestaurantTo(checkNotFoundWithId(restaurantRepository.findById(id), id));
    }

    @Operation(summary = "#createWithLocation", description = "Create new restaurant, return in header his url")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = RestaurantTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
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

    @Operation(summary = "#update", description = "Update restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Transactional
    public void update(@Valid @RequestBody RestaurantTo restaurantTo, @PathVariable int id) {
        log.info("AdminRestaurantController#update(restaurantTo:{}, id:{})", restaurantTo, id);
        restaurantRepository.save(updateRestaurantFields(checkNotFoundWithId(restaurantRepository.findById(id), id), restaurantTo));
    }

    @Operation(summary = "#delete", description = "Delete restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("AdminRestaurantController#delete(id:{})", id);
        restaurantRepository.deleteExisted(id);
    }
}