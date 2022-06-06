package com.github.kriaktus.restaurantvoting.web.restaurant;


import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.web.GlobalExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
    @Transactional
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        RestaurantTo restaurantTo = (RestaurantTo) target;
        if (request.getMethod().equals("PUT")) {
            try {
                int pathId = Integer.parseInt(request.getRequestURI().split("/")[4]);
                if (!restaurantRepository.existsById(pathId)) {
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
