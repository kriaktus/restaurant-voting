package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

public abstract class AbstractRestaurantController {
    @Autowired
    protected RestaurantRepository restaurantRepository;
    @Autowired
    private UniqueRestaurantToValidator restaurantToValidator;

    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(restaurantToValidator);
    }
}
