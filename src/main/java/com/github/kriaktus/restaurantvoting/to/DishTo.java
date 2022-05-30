package com.github.kriaktus.restaurantvoting.to;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import com.github.kriaktus.restaurantvoting.util.validation.NoHtml;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DishTo extends BaseTo {
    @NotBlank
    @Size(min = 2, max = 100)
    @NoHtml
    String title;

    @NotNull
    @Positive
    Integer cost;

    public DishTo(Integer id, String title, Integer cost) {
        super(id);
        this.title = title;
        this.cost = cost;
    }
}
