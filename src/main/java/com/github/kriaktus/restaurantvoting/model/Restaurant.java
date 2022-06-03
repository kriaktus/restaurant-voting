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
    @OrderBy("name")
    @ToString.Exclude
    private List<MenuItem> menuItems;

    public Restaurant(String name, List<MenuItem> menuItems) {
        super(name);
        this.menuItems = menuItems;
    }

    public Restaurant(Integer id, String name, List<MenuItem> menuItems) {
        super(id, name);
        this.menuItems = menuItems;
    }

    public Restaurant(Restaurant restaurant) {
        this(restaurant.id, restaurant.name, restaurant.menuItems);
    }
}
