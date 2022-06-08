package com.github.kriaktus.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuTo extends BaseTo {
    @NotNull
    LocalDate menuDate;
    @NotNull
    @Valid
    List<MenuItemTo> items;

    public MenuTo(Integer id, LocalDate menuDate, List<MenuItemTo> items) {
        super(id);
        this.menuDate = menuDate;
        this.items = items;
    }
}
