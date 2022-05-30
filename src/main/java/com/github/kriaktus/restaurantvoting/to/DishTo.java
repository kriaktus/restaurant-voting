package com.github.kriaktus.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DishTo extends NamedTo {

    @NotNull
    @Positive
    Integer price;

    public DishTo(Integer id, String name, Integer cost) {
        super(id, name);
        this.price = cost;
    }
}
