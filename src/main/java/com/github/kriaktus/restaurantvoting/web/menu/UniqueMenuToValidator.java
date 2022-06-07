package com.github.kriaktus.restaurantvoting.web.menu;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.to.MenuTo;
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

import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.fromMenuItemToAndRestaurantId;
import static com.github.kriaktus.restaurantvoting.web.GlobalExceptionHandler.*;

@Component
@AllArgsConstructor
public class UniqueMenuToValidator implements Validator {
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
            errors.rejectValue("menuDate", "", EXCEPTION_MENU_ACTUAL_DATE);
        int restaurantId;
        try {
            restaurantId = Integer.parseInt(request.getRequestURI().split("/")[4]);
        } catch (NumberFormatException nfe) {
            return;
        }
        List<MenuItemTo> menuItemTo = menuTo.getItems();
        switch (request.getMethod()) {
            case "POST" -> {
                boolean hasId = menuItemTo.stream()
                        .filter(item -> item.getId() != null)
                        .anyMatch(obj -> true);
                if (hasId) errors.rejectValue("items", "", EXCEPTION_MENU_MENU_ITEM_HAS_ID);
            }
            case "PUT" -> {
                Set<MenuItem> menuItems = fromMenuItemToAndRestaurantId(menuItemTo, restaurantId);
                Set<MenuItem> allItemsByRestaurant = menuItemRepository.findAllByRestaurantId(restaurantId);
                boolean belongToAnotherRestaurant = menuItems.stream()
                        .filter(item -> !item.isNew() && !allItemsByRestaurant.contains(item))
                        .anyMatch(obj -> true);
                if (belongToAnotherRestaurant)
                    errors.rejectValue("items", "", EXCEPTION_MENU_ITEM_FROM_ANOTHER_RESTAURANT);
                boolean hasSameId = menuItemTo.stream()
                        .filter(item -> !item.isNew())
                        .collect(Collectors.groupingBy(MenuItemTo::getId))
                        .values().stream()
                        .filter(n -> n.size() > 1)
                        .anyMatch(obj -> true);
                if (hasSameId) errors.rejectValue("items", "", EXCEPTION_MENU_MENU_ITEM_HAS_SAME_ID);
            }
        }
        boolean hasSameName = menuItemTo.stream()
                .collect(Collectors.groupingBy(MenuItemTo::getName))
                .values().stream()
                .filter(n -> n.size() > 1)
                .anyMatch(obj -> true);
        if (hasSameName) errors.rejectValue("items", "", EXCEPTION_MENU_MENU_ITEM_HAS_SAME_NAME);
    }
}
