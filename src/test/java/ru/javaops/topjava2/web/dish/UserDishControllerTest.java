package ru.javaops.topjava2.web.dish;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.javaops.topjava2.web.AbstractControllerTest;

import static ru.javaops.topjava2.test_data.DishTestData.*;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;
import static ru.javaops.topjava2.test_data.UserTestData.NOT_FOUND;
import static ru.javaops.topjava2.test_data.UserTestData.USER_MAIL;

public class UserDishControllerTest extends AbstractControllerTest {
    public static final String REST_URL = "/api/restaurants/{restaurantId}/dishes";

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL, RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(dish1_1, dish1_2, dish1_3, dish1_4));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllByRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllByRestaurantUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL, RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
