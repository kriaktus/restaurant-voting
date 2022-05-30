package com.github.kriaktus.restaurantvoting.model;

import com.github.kriaktus.restaurantvoting.HasId;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Entity
@Table(name = "dish")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Dish extends BaseEntity implements HasId {

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Positive
    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    public Dish(Integer id, String title, Integer cost, Integer restaurantId) {
        super(id);
        this.title = title;
        this.cost = cost;
        this.restaurantId = restaurantId;
    }

    public Dish(Dish dish) {
        this(dish.id, dish.title, dish.cost, dish.restaurantId);
    }
}
