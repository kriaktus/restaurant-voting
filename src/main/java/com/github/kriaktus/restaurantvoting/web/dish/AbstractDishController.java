package com.github.kriaktus.restaurantvoting.web.dish;

import com.github.kriaktus.restaurantvoting.repository.DishRepository;
import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

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
