package com.github.kriaktus.restaurantvoting.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Restaurant extends NamedEntity {

    @JoinColumn(name = "restaurant_id")
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private Set<Menu> menu;

    public Restaurant(String name, Set<Menu> menu) {
        super(name);
        this.menu = menu;
    }

    public Restaurant(Integer id, String name, Set<Menu> menu) {
        super(id, name);
        this.menu = menu;
    }

    public Restaurant(Restaurant restaurant) {
        this(restaurant.id, restaurant.name, restaurant.menu);
    }
}
