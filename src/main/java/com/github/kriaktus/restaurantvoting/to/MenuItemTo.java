package com.github.kriaktus.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuItemTo extends NamedTo {
    @NotNull
    @Positive
    Integer price;

    public MenuItemTo(Integer id, String name, Integer price) {
        super(id, name);
        this.price = price;
    }

    public MenuItemTo(MenuItemTo menuItemTo) {
        this(menuItemTo.id, menuItemTo.name, menuItemTo.price);
    }
}
