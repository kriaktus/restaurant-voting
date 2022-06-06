package com.github.kriaktus.restaurantvoting.to;

public class RestaurantWithMenuTo extends RestaurantTo {
    public MenuTo menuTo;

    public RestaurantWithMenuTo(Integer id, String name, MenuTo menuTo) {
        super(id, name);
        this.menuTo = menuTo;
    }
}
