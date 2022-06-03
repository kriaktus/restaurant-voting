package com.github.kriaktus.restaurantvoting.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class MenuItem extends NamedEntity {

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private Integer restaurantId;

    public MenuItem(Integer id, String name, Integer price, Integer restaurantId) {
        super(id, name);
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public MenuItem(String name, Integer price, Integer restaurantId) {
        super.setName(name);
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public MenuItem(MenuItem menuItem) {
        this(menuItem.id, menuItem.name, menuItem.price, menuItem.restaurantId);
    }
}
