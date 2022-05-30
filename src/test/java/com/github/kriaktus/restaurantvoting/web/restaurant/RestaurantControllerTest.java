package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData;
import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.util.RestaurantUtil;
import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantTo;

public class RestaurantControllerTest extends AbstractControllerTest {

    public static final String REST_URL = "/api/restaurants";

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RestaurantTestData.RESTAURANT_TO_MATCHER.contentJson(RestaurantUtil.toRestaurantTo(RestaurantTestData.restaurant3, RestaurantTestData.restaurant1, RestaurantTestData.restaurant2, RestaurantTestData.restaurant4)));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
