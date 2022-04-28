package ru.javaops.topjava2.web.dish;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.javaops.topjava2.model.Dish;
import ru.javaops.topjava2.repository.DishRepository;
import ru.javaops.topjava2.util.JsonUtil;
import ru.javaops.topjava2.web.AbstractControllerTest;

import java.util.List;

import static ru.javaops.topjava2.test_data.DishTestData.*;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT2_ID;
import static ru.javaops.topjava2.test_data.UserTestData.*;

public class AdminDishControllerTest extends AbstractControllerTest {
    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    @Autowired
    private DishRepository dishRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(dish1_1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        Dish expected = getNewDish();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewDish())))
                .andDo(MockMvcResultHandlers.print());
        Dish actual = DISH_MATCHER.readFromJson(action);
        int newId = actual.id();
        expected.setId(newId);
        DISH_MATCHER.assertMatch(expected, actual);
        DISH_MATCHER.assertMatch(dishRepository.get(newId, RESTAURANT1_ID), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithoutRestaurantId() throws Exception {
        Dish expected = getNewDish();
        Dish updated = getNewDish();
        updated.setRestaurantId(null);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(MockMvcResultHandlers.print());
        Dish actual = DISH_MATCHER.readFromJson(action);
        int newId = actual.id();
        expected.setId(newId);
        DISH_MATCHER.assertMatch(expected, actual);
        DISH_MATCHER.assertMatch(dishRepository.get(newId, RESTAURANT1_ID), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicate() throws Exception {
        Dish duplicate = new Dish(dish1_1);
        duplicate.setId(null);
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithVariousRestaurantId() throws Exception {
        Dish dishForRestaurant1 = getNewDish();
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(dishForRestaurant1)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Dish expected = getUpdatedDish();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        DISH_MATCHER.assertMatch(dishRepository.get(expected.id(), RESTAURANT1_ID), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithoutRestaurantId() throws Exception {
        Dish expected = getUpdatedDish();
        Dish updated = getUpdatedDish();
        updated.setRestaurantId(null);
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        DISH_MATCHER.assertMatch(dishRepository.get(expected.id(), RESTAURANT1_ID), expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateTitle() throws Exception {
        Dish duplicateTitleDish = getUpdatedDish();
        duplicateTitleDish.setTitle(dish1_1.getTitle());
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateTitleDish)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateWithVariousRestaurantId() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT2_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedDish())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DISH_MATCHER.assertMatch(dishRepository.getAllByRestaurantId(RESTAURANT1_ID), dish1_1, dish1_2, dish1_4);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteAllByRestaurantId() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL, RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DISH_MATCHER.assertMatch(dishRepository.getAllByRestaurantId(RESTAURANT1_ID), List.of());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteAllByRestaurantIdNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
