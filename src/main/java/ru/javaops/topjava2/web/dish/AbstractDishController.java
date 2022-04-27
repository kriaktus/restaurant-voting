package ru.javaops.topjava2.web.dish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.javaops.topjava2.repository.DishRepository;

public abstract class AbstractDishController {
    @Autowired
    protected DishRepository dishRepository;
    @Autowired
    private UniqueDishValidator dishValidator;

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(dishValidator);
    }
}
