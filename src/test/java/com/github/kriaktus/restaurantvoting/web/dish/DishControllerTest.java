package com.github.kriaktus.restaurantvoting.web.dish;

import com.github.kriaktus.restaurantvoting.test_data.DishTestData;
import com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData;
import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.util.DishUtil;
import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.kriaktus.restaurantvoting.util.DishUtil.toDishTo;

public class DishControllerTest extends AbstractControllerTest {
    public static final String REST_URL = "/api/restaurants/{restaurantId}/dishes";

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL, RestaurantTestData.RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.DISH_TO_MATCHER.contentJson(DishUtil.toDishTo(DishTestData.dish1_1, DishTestData.dish1_2, DishTestData.dish1_3, DishTestData.dish1_4)));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getAllByRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL, UserTestData.NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void getAllByRestaurantUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL, RestaurantTestData.RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
