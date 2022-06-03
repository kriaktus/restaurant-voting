package com.github.kriaktus.restaurantvoting.web.menu;

import com.github.kriaktus.restaurantvoting.error.IllegalRequestDataException;
import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.to.MenuTo;
import com.github.kriaktus.restaurantvoting.util.MenuItemUtil;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.*;

@Component
@AllArgsConstructor
public class MenuToValidator implements Validator {
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return MenuTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        MenuTo menuTo = (MenuTo) target;
        if (!menuTo.getMenuDate().isEqual(LocalDate.now()))
            throw new IllegalRequestDataException("Saved actual menu must contain actual date");
        int restaurantId;
        try {
            restaurantId = Integer.parseInt(request.getRequestURI().split("/")[4]);
        } catch (NumberFormatException nfe) {
            return;
        }
        Menu actualMenu = menuRepository.findByDateAndRestaurantIdWithoutItems(LocalDate.now(), restaurantId).orElse(null);
        List<MenuItemTo> menuItemTo = menuTo.getItems();
        switch (request.getMethod()) {
            case "POST" -> {
                checkNew(menuTo);
                checkNotFoundWithMessage(actualMenu == null, String.format("Actual menu to restaurant with id=%d already exist", restaurantId));
                boolean hasId = menuItemTo.stream()
                        .filter(item -> item.getId() != null)
                        .anyMatch(obj -> true);
                if (hasId) throw new IllegalRequestDataException("Created menu must contains menu items without id");
            }
            case "PUT" -> {
                checkNotFoundWithMessage(actualMenu, String.format("Actual menu to restaurant with id=%d not found", restaurantId));
                assureIdConsistent(menuTo, actualMenu.getId());
                List<MenuItem> menuItems = MenuItemUtil.fromMenuItemToAndRestaurantId(menuItemTo, restaurantId);
                Set<MenuItem> allItemsByRestaurant = menuItemRepository.findAllByRestaurantId(restaurantId);
                boolean belongToAnotherRestaurant = menuItems.stream()
                        .filter(item -> !item.isNew() && !allItemsByRestaurant.contains(item))
                        .anyMatch(obj -> true);
                if (belongToAnotherRestaurant)
                    throw new IllegalRequestDataException("Some menu items belong to another restaurant or have a non-existent id");
                boolean hasSameId = menuItemTo.stream()
                        .filter(item -> !item.isNew())
                        .collect(Collectors.groupingBy(MenuItemTo::getId))
                        .values().stream()
                        .filter(n -> n.size() > 1)
                        .anyMatch(obj -> true);
                if (hasSameId) throw new IllegalRequestDataException("Some menu items has same id");
            }
        }
        boolean hasSameName = menuItemTo.stream()
                .collect(Collectors.groupingBy(MenuItemTo::getName))
                .values().stream()
                .filter(n -> n.size() > 1)
                .anyMatch(obj -> true);
        if (hasSameName) throw new IllegalRequestDataException("Some menu items has same names");
    }
}
