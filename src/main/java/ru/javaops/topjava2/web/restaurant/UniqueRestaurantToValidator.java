package ru.javaops.topjava2.web.restaurant;


import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.to.RestaurantTo;
import ru.javaops.topjava2.util.validation.ValidationUtil;
import ru.javaops.topjava2.web.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class UniqueRestaurantToValidator implements Validator {
    private final RestaurantRepository restaurantRepository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return RestaurantTo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        RestaurantTo restaurantTo = (RestaurantTo) target;
        String requestURI = request.getRequestURI();
        if (request.getMethod().equals("PUT")) {
            try {
                int pathId = Integer.parseInt(requestURI.split("/")[4]);
                ValidationUtil.assureIdConsistent(restaurantTo, pathId);
                if (restaurantRepository.get(pathId) == null) {
                    errors.rejectValue("id", "", GlobalExceptionHandler.EXCEPTION_ENTITY_NOT_EXIST);
                }
            } catch (NumberFormatException nfe) {
                return;
            }
        }

        restaurantRepository.getRestaurantByName(restaurantTo.getName()).ifPresent(dbRestaurant -> {
            if (request.getMethod().equals("PUT") && dbRestaurant.id() == restaurantTo.id()) return;
            errors.rejectValue("name", "", GlobalExceptionHandler.EXCEPTION_DUPLICATE_NAME);
        });
    }
}
