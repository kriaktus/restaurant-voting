package ru.javaops.topjava2.web.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.repository.RestaurantRepository;

public abstract class AbstractDishController {
    @Autowired
    protected DishRepository dishRepository;
    @Autowired
    protected RestaurantRepository restaurantRepository;
    @Autowired
    private UniqueDishToValidator dishToValidator;

    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(dishToValidator);
    }
}
