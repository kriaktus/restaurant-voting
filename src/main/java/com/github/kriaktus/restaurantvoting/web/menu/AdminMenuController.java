package com.github.kriaktus.restaurantvoting.web.menu;

import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.to.MenuTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import static com.github.kriaktus.restaurantvoting.util.MenuUtil.fromMenuToAndRestaurant;
import static com.github.kriaktus.restaurantvoting.util.MenuUtil.toMenuTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.*;

@RestController
@RequestMapping(value = AdminMenuController.REST_URL)
@Slf4j
@Tag(name = "AdminMenuController")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
@AllArgsConstructor
public class AdminMenuController {
    private MenuRepository menuRepository;
    private RestaurantRepository restaurantRepository;
    private UniqueMenuToValidator uniqueMenuToValidator;

    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu";

    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(uniqueMenuToValidator);
    }

    @Operation(summary = "#getActual", description = "Get actual menu of the restaurant (with id={restaurantId})")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MenuTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/actual")
    public MenuTo getActual(@PathVariable int restaurantId) {
        log.info("AdminMenuController#getActual(restaurantId:{})", restaurantId);
        return toMenuTo(checkNotFoundWithMessage(
                menuRepository.findByDateAndRestaurantId(LocalDate.now(), restaurantId),
                String.format("Actual menu to restaurant with id=%d not found", restaurantId)));
    }

    @Operation(summary = "#getByDate", description = "Get menu by date of the restaurant (with id={restaurantId})")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MenuTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/by-date")
    public MenuTo getByDate(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate date, @PathVariable int restaurantId) {
        log.info("AdminMenuController#getByDate(restaurantId:{})", restaurantId);
        return toMenuTo(checkNotFoundWithMessage(
                menuRepository.findByDateAndRestaurantId(date, restaurantId),
                String.format("Menu by date=%s to restaurant with id=%d not found", date, restaurantId)));
    }

    @Operation(summary = "#createActualWithLocation", description = "Create new actual menu of the restaurant (with id={restaurantId}), return in header his url")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = MenuTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @PostMapping
    @Transactional
    @CacheEvict(cacheNames = {"restaurantTo", "restaurantWithMenuTo"}, allEntries = true)
    public ResponseEntity<MenuTo> createActualWithLocation(@Valid @RequestBody MenuTo menuTo, @PathVariable int restaurantId) {
        log.info("AdminMenuController#createActualWithLocation(menuTo:{}, restaurantId:{})", menuTo, restaurantId);
        checkNew(menuTo);
        Optional<Menu> actualMenu = menuRepository.findByDateAndRestaurantIdWithoutItems(LocalDate.now(), restaurantId);
        checkNotFoundWithMessage(actualMenu.isEmpty(), String.format("Actual menu to restaurant with id=%d already exist", restaurantId));
        Menu created = menuRepository.save(fromMenuToAndRestaurant(
                menuTo, checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId).getId())
        );
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/actual")
                .buildAndExpand(restaurantId)
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(toMenuTo(created));
    }

    @Operation(summary = "#updateActual", description = "Update actual menu of the restaurant (with id={restaurantId})")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @PutMapping("/actual")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(cacheNames = {"restaurantWithMenuTo"}, allEntries = true)
    public void updateActual(@Valid @RequestBody MenuTo menuTo, @PathVariable int restaurantId) {
        log.info("AdminMenuController#updateActual(menuTo:{}, restaurantId:{})", menuTo, restaurantId);
        Optional<Menu> actualMenu = menuRepository.findByDateAndRestaurantIdWithoutItems(LocalDate.now(), restaurantId);
        checkNotFoundWithMessage(actualMenu.isPresent(), String.format("Actual menu to restaurant with id=%d not found", restaurantId));
        assureIdConsistent(menuTo, actualMenu.get().id());
        menuRepository.save(fromMenuToAndRestaurant(menuTo, checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId).getId()));
    }

    @Operation(summary = "#deleteActual", description = "Delete actual menu of the restaurant (with id={restaurantId})")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping("/actual")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"restaurantTo", "restaurantWithMenuTo"}, allEntries = true)
    public void deleteActual(@PathVariable int restaurantId) {
        log.info("AdminMenuController#deleteActual(restaurantId:{})", restaurantId);
        checkModification(menuRepository.deleteByDateAndRestaurantId(LocalDate.now(), restaurantId));
    }
}
