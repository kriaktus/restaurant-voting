package com.github.kriaktus.restaurantvoting.web.menuitem;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.fromMenuItemToAndRestaurantId;
import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.toMenuItemTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.*;

@RestController
@RequestMapping(value = AdminMenuItemController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "AdminMenuItemController")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
@AllArgsConstructor
public class AdminMenuItemController {
    private MenuItemRepository menuItemRepository;
    private MenuRepository menuRepository;
    private RestaurantRepository restaurantRepository;
    private UniqueMenuItemToValidator uniqueMenuItemToValidator;

    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu/actual/menu-item";

    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(uniqueMenuItemToValidator);
    }

    @Operation(summary = "#getFromActualMenu", description = "Get menu item by id from the restaurant (with id={restaurantId}) actual menu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MenuItemTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}")
    public MenuItemTo getFromActualMenu(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminMenuItemController#getFromActualMenu(id:{}, restaurantId:{})", id, restaurantId);
        return toMenuItemTo(checkNotFoundWithMessage(menuItemRepository.findFromActiveMenuByIdAndRestaurantId(id, restaurantId),
                String.format("Menu item with id=%d and restaurantId=%d not found in actual menu", id, restaurantId)));
    }

    @Operation(summary = "#createWithLocationToActualMenu", description = "Create new menu item in restaurant (with id={restaurantId}) actual menu, return in header his url")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = MenuItemTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @PostMapping
    @Transactional
    public ResponseEntity<MenuItemTo> createWithLocationToActualMenu(@Valid @RequestBody MenuItemTo menuItemTo, @PathVariable int restaurantId) {
        log.info("AdminMenuItemController#createWithLocationToActualMenu(menuItemTo:{}, restaurantId:{})", menuItemTo, restaurantId);
        checkNew(menuItemTo);
        MenuItem created = saveMenuTo(menuItemTo, restaurantId);
        getActualMenuItems(restaurantId).add(created);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(toMenuItemTo(created));
    }

    @Operation(summary = "#updateInActualMenu", description = "Update menu item by id from the restaurant (with id={restaurantId}) actual menu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void updateInActualMenu(@Valid @RequestBody MenuItemTo menuItemTo, @PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminMenuItemController#updateInActualMenu(menuItemTo:{}, id:{}, restaurantId:{})", menuItemTo, id, restaurantId);
        assureIdConsistent(menuItemTo, id);
        getActualMenuItems(restaurantId).add(saveMenuTo(menuItemTo, restaurantId));
    }

    @Operation(summary = "#deleteFromActualMenu", description = "Delete menu item by id from the restaurant (with id={restaurantId}) actual menu")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteFromActualMenu(@PathVariable int id, @PathVariable int restaurantId) {
        log.info("AdminMenuItemController#delete(id:{}, restaurantId:{})", id, restaurantId);
        MenuItem itemToRemove = checkNotFoundWithId(menuItemRepository.findFromActiveMenuByIdAndRestaurantId(id, restaurantId), id);
        getActualMenuItems(restaurantId).remove(itemToRemove);
    }

    private Set<MenuItem> getActualMenuItems(int restaurantId) {
        return checkNotFoundWithMessage(menuRepository.findByDateAndRestaurantId(LocalDate.now(), restaurantId),
                String.format("Actual menu to restaurant with id=%d not found", restaurantId)).getItems();
    }

    private MenuItem saveMenuTo(MenuItemTo menuItemTo, int restaurantId) {
        return menuItemRepository.save(fromMenuItemToAndRestaurantId(menuItemTo, checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId).getId()));
    }
}
