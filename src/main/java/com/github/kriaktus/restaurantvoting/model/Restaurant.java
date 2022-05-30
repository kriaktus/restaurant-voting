package com.github.kriaktus.restaurantvoting.model;

import com.github.kriaktus.restaurantvoting.HasId;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Restaurant extends NamedEntity implements HasId {

    @JoinColumn(name = "restaurant_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("title")
    @ToString.Exclude
    private List<Dish> dishes;

    public Restaurant(String name, List<Dish> dishes) {
        super(name);
        this.dishes = dishes;
    }

    public Restaurant(Integer id, String name, List<Dish> dishes) {
        super(id, name);
        this.dishes = dishes;
    }

    public Restaurant(Restaurant restaurant) {
        this(restaurant.id, restaurant.name, restaurant.dishes);
    }
}
