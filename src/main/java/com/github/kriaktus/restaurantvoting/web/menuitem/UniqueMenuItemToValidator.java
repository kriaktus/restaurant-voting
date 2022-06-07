package com.github.kriaktus.restaurantvoting.web.menuitem;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static com.github.kriaktus.restaurantvoting.util.MenuItemUtil.fromMenuItemToAndRestaurantId;
import static com.github.kriaktus.restaurantvoting.web.GlobalExceptionHandler.EXCEPTION_MENU_ITEM_DUPLICATE_NAME;
import static com.github.kriaktus.restaurantvoting.web.GlobalExceptionHandler.EXCEPTION_MENU_ITEM_FROM_ANOTHER_RESTAURANT;

@Component
@AllArgsConstructor
public class UniqueMenuItemToValidator implements Validator {
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return MenuItemTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        MenuItemTo itemTo = (MenuItemTo) target;
        int restaurantId;
        try {
            restaurantId = Integer.parseInt(request.getRequestURI().split("/")[4]);
        } catch (NumberFormatException nfe) {
            return;
        }
        menuRepository.findByDateAndRestaurantId(LocalDate.now(), restaurantId).ifPresent(actualMenu -> {
            Set<MenuItem> actualMenuItems = actualMenu.getItems();
            String itemToName = itemTo.getName();
            switch (request.getMethod()) {
                case "POST" -> {
                    boolean duplicateNameWithItemFromActualMenu = actualMenuItems.stream()
                            .filter(item -> item.getName().equals(itemToName))
                            .anyMatch(obj -> true);
                    if (duplicateNameWithItemFromActualMenu)
                        errors.rejectValue("name", "", EXCEPTION_MENU_ITEM_DUPLICATE_NAME);
                }
                case "PUT" -> {
                    Set<MenuItem> allItemsByRestaurant = menuItemRepository.findAllByRestaurantId(restaurantId);
                    MenuItem menuItem = fromMenuItemToAndRestaurantId(itemTo, restaurantId);
                    if (!menuItem.isNew() && !allItemsByRestaurant.contains(menuItem))
                        errors.rejectValue("id", "", EXCEPTION_MENU_ITEM_FROM_ANOTHER_RESTAURANT);
                    boolean duplicateNameWithItemFromActualMenuNotItself = actualMenuItems.stream()
                            .filter(item -> item.getName().equals(itemToName) && !Objects.equals(item.getId(), itemTo.getId()))
                            .anyMatch(obj -> true);
                    if (duplicateNameWithItemFromActualMenuNotItself)
                        errors.rejectValue("name", "", EXCEPTION_MENU_ITEM_DUPLICATE_NAME);
                }
            }
        });
    }
}
