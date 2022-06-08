package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.to.RestaurantWithMenuTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantTo;
import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantWithMenuTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNotFoundWithMessage;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "RestaurantController")
@ApiResponses(@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content))
public class RestaurantController extends AbstractRestaurantController {

    static final String REST_URL = "/api/restaurants";

    @Operation(summary = "#getActive", description = "Get active restaurant by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RestaurantTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}")
    @Cacheable("restaurantTo")
    public RestaurantTo getActive(@PathVariable int id) {
        log.info("RestaurantController#getActive(id:{})", id);
        return toRestaurantTo(checkNotFoundWithMessage(restaurantRepository.findActiveById(id),
                String.format("Restaurant with id=%s not exist or hasn't actual menu", id)));
    }

    @Operation(summary = "#getAllActive", description = "Get all active restaurants")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantTo.class)))))
    @GetMapping
    @Cacheable("restaurantTo")
    public List<RestaurantTo> getAllActive() {
        log.info("RestaurantController#getAllActive()");
        return toRestaurantTo(restaurantRepository.findAllActive());
    }

    @Operation(summary = "#getWithActualMenu", description = "Get active restaurant by id with actual menu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RestaurantWithMenuTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}/with-actual-menu")
    @Cacheable("restaurantWithMenuTo")
    public RestaurantWithMenuTo getWithActualMenu(@PathVariable int id) {
        log.info("RestaurantController#getWithActualMenu(id:{})", id);
        return toRestaurantWithMenuTo(checkNotFoundWithMessage(restaurantRepository.findByIdWithActualMenu(id),
                String.format("Restaurant with id=%s not exist or hasn't actual menu", id)));
    }

    @Operation(summary = "#getAllWithActualMenu", description = "Get all active restaurants with actual menu")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RestaurantWithMenuTo.class))))
    @GetMapping("/with-actual-menu")
    @Cacheable("restaurantWithMenuTo")
    public List<RestaurantWithMenuTo> getAllWithActualMenu() {
        log.info("RestaurantController#getAllWithActualMenu()");
        return toRestaurantWithMenuTo(restaurantRepository.findAllWithActualMenu());
    }
}
