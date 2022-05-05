package ru.javaops.topjava2.web.dish;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping(value = DishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "DishController")
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
@CacheConfig(cacheNames = "dishes")
public class DishController extends AbstractDishController {

    public static final String REST_URL = "/api/restaurants/{restaurantId}/dishes";

    @Operation(summary = "#getAllByRestaurant", description = "Get dishes from the restaurant (with id={restaurantId}) menu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = DishTo.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping
    @Cacheable
    public List<DishTo> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("UserDishController#getAllByRestaurant(restaurantId:{})", restaurantId);
        checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId);
        return toDishTo(dishRepository.findAllByRestaurantIdOrderByTitle(restaurantId));
    }
}
