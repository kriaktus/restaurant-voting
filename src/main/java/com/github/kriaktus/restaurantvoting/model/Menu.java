package com.github.kriaktus.restaurantvoting.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true, exclude = "items")
public class Menu extends BaseEntity {

    @NotNull
    @Column(name = "menu_date", nullable = false, updatable = false)
    private LocalDate menuDate;

    @NotNull
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "menu_menu_item",
            joinColumns = {@JoinColumn(name = "menu_id")},
            inverseJoinColumns = {@JoinColumn(name = "menu_item_id")}
    )
    private List<MenuItem> items;

    public Menu(Integer id, LocalDate menuDate, Integer restaurantId, List<MenuItem> items) {
        super(id);
        this.menuDate = menuDate;
        this.restaurantId = restaurantId;
        this.items = items;
    }
}
