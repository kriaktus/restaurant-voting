package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.util.RestaurantUtil;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;

import java.util.List;

import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantTo;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "RestaurantController")
@CacheConfig(cacheNames = "restaurants")
public class RestaurantController extends AbstractRestaurantController {

    static final String REST_URL = "/api/restaurants";

    @Operation(summary = "#getAll", description = "Get all restaurants")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RestaurantTo.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)})
    @GetMapping
    @Cacheable
    public List<RestaurantTo> getAll() {
        log.info("UserRestaurantController#getAll()");
        return RestaurantUtil.toRestaurantTo(restaurantRepository.findAll(Sort.by("name")));
    }
}
