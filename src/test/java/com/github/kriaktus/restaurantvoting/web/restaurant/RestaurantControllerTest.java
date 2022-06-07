package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.testdata.UserTestData;
import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.github.kriaktus.restaurantvoting.testdata.RestaurantTestData.*;
import static com.github.kriaktus.restaurantvoting.testdata.UserTestData.NOT_FOUND;
import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantWithMenuTo;
import static com.github.kriaktus.restaurantvoting.web.restaurant.RestaurantController.REST_URL;

public class RestaurantControllerTest extends AbstractControllerTest {

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getActive() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_TO_MATCHER.contentJson(restaurantTo1));
    }

    @Test
    void getActiveUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getNotActive() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT4_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getActiveNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getAllActive() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_TO_MATCHER.contentJson(List.of(restaurantTo1, restaurantTo2, restaurantTo3)));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getWithActualMenu() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}/with-actual-menu", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_WITH_MENU_TO_MATCHER.contentJson(toRestaurantWithMenuTo(restaurant1)));
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getWithActualMenuNotActive() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}/with-actual-menu", RESTAURANT4_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getWithActualMenuNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}/with-actual-menu", NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
