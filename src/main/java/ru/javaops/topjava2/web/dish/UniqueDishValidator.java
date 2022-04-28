package ru.javaops.topjava2.web.dish;

import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.util.validation.ValidationUtil;
import ru.javaops.topjava2.web.GlobalExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class UniqueDishValidator implements Validator {
    private final DishRepository repository;
    private final HttpServletRequest request;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Dish.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Dish dish = (Dish) target;
        String requestURI = request.getRequestURI();
        try {
            ValidationUtil.assureRestaurantIdConsistent(dish, Integer.parseInt(requestURI.split("/")[4]));
        } catch (NumberFormatException nfe) {
            return;
        }

        repository.findByTitleAndRestaurantId(dish.getTitle(), dish.getRestaurantId()).ifPresent(dbDish -> {
            Integer id = dish.getId();
            if (request.getMethod().equals("PUT")) {
                // update itself - it's ok
                if (id != null && dbDish.id() == id) return;
                // if dish from request body hasn't id, but dish id in path equals dish.id from db - it's ok
                if (id == null && requestURI.endsWith("/" + dbDish.getId())) return;
            }
            errors.rejectValue("title", "", GlobalExceptionHandler.EXCEPTION_DUPLICATE_TITLE);
        });
    }
}
