package ru.javaops.topjava2.web.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.javaops.topjava2.repository.RestaurantRepository;

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
