package com.github.kriaktus.restaurantvoting.web.menuitem;

import com.github.kriaktus.restaurantvoting.error.IllegalRequestDataException;
import com.github.kriaktus.restaurantvoting.model.Menu;
import com.github.kriaktus.restaurantvoting.model.MenuItem;
import com.github.kriaktus.restaurantvoting.repository.MenuItemRepository;
import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.util.MenuItemUtil;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNotFoundWithMessage;

@Component
@AllArgsConstructor
public class MenuItemToValidator implements Validator {
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
        Menu actualMenu = checkNotFoundWithMessage(menuRepository.findByDateAndRestaurantId(LocalDate.now(), restaurantId),
                String.format("Actual menu to restaurant with id=%d not found", restaurantId));
        List<MenuItem> actualMenuItems = actualMenu.getItems();
        String itemToName = itemTo.getName();
        switch (request.getMethod()) {
            case "POST" -> {
                boolean hasSameNameAsContentActualMenu = actualMenuItems.stream()
                        .filter(item -> item.getName().equals(itemToName))
                        .anyMatch(obj -> true);
                if (hasSameNameAsContentActualMenu)
                    throw new IllegalRequestDataException("Menu item has same name as the content of actual menu");
            }
            case "PUT" -> {
                Set<MenuItem> allItemsByRestaurant = menuItemRepository.findAllByRestaurantId(restaurantId);
                MenuItem menuItem = MenuItemUtil.fromMenuItemToAndRestaurantId(itemTo, restaurantId);
                if (!menuItem.isNew() && !allItemsByRestaurant.contains(menuItem))
                    throw new IllegalRequestDataException("Menu item belong to another restaurant or have a non-existent id");
                boolean hasSameNameAsContentActualMenuNotItself = actualMenuItems.stream()
                        .filter(item -> item.getName().equals(itemToName) && !Objects.equals(item.getId(), itemTo.getId()))
                        .anyMatch(obj -> true);
                if (hasSameNameAsContentActualMenuNotItself)
                    throw new IllegalRequestDataException("Menu item has same name as item of actual menu");
            }
        }
    }
}
