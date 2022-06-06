package com.github.kriaktus.restaurantvoting.web.restaurant;

import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;
import com.github.kriaktus.restaurantvoting.util.JsonUtil;
import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.*;
import static com.github.kriaktus.restaurantvoting.test_data.UserTestData.*;
import static com.github.kriaktus.restaurantvoting.util.RestaurantUtil.toRestaurantTo;

public class AdminRestaurantControllerTest extends AbstractControllerTest {

    public static final String REST_URL = "/api/admin/restaurants";
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_TO_MATCHER.contentJson(List.of(toRestaurantTo(restaurant1), toRestaurantTo(restaurant2),
                        toRestaurantTo(restaurant3), toRestaurantTo(restaurant4))));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAllForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        RestaurantTo expected = toRestaurantTo(getNewRestaurant());
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewRestaurant())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        RestaurantTo actual = RESTAURANT_TO_MATCHER.readFromJson(resultActions);
        int newId = actual.id();
        expected.setId(newId);
        RESTAURANT_TO_MATCHER.assertMatch(actual, expected);
        RESTAURANT_TO_MATCHER.assertMatch(toRestaurantTo(restaurantRepository.findById(newId).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateName() throws Exception {
        Restaurant duplicate = getNewRestaurant();
        duplicate.setName(restaurant2.getName());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        RestaurantTo expected = getUpdatedRestaurantTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedRestaurantTo())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        RESTAURANT_TO_MATCHER.assertMatch(toRestaurantTo(restaurantRepository.findById(RESTAURANT1_ID).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateName() throws Exception {
        RestaurantTo duplicate = getUpdatedRestaurantTo();
        duplicate.setName(restaurant2.getName());
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithVariousId() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedRestaurantTo())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        Assertions.assertTrue(restaurantRepository.existsById(RESTAURANT1_ID));
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertFalse(restaurantRepository.existsById(RESTAURANT1_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
