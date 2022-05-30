package com.github.kriaktus.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo extends BaseTo {
    @NotNull
    LocalDate votingDate;
    @NotNull
    RestaurantTo restaurant;

    public VoteTo(Integer id, LocalDate votingDate, RestaurantTo restaurant) {
        super(id);
        this.votingDate = votingDate;
        this.restaurant = restaurant;
    }
}
