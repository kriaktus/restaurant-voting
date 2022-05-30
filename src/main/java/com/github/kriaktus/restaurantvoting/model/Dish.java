package com.github.kriaktus.restaurantvoting.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "dish")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    public Dish(Integer id, String name, Integer price, Integer restaurantId) {
        super(id, name);
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public Dish(String name, Integer price, Integer restaurantId) {
        super.setName(name);
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public Dish(Dish dish) {
        this(dish.id, dish.name, dish.price, dish.restaurantId);
    }
}
