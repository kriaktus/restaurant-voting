package com.github.kriaktus.restaurantvoting.web.dish;

import com.github.kriaktus.restaurantvoting.test_data.DishTestData;
import com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData;
import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.to.DishTo;
import com.github.kriaktus.restaurantvoting.util.DishUtil;
import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.github.kriaktus.restaurantvoting.model.Dish;
import com.github.kriaktus.restaurantvoting.repository.DishRepository;
import com.github.kriaktus.restaurantvoting.util.JsonUtil;

import java.util.List;

import static com.github.kriaktus.restaurantvoting.util.DishUtil.toDishTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.checkNotFoundWithId;

public class AdminDishControllerTest extends AbstractControllerTest {

    public static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    @Autowired
    private DishRepository dishRepository;

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.DISH_TO_MATCHER.contentJson(DishUtil.toDishTo(DishTestData.dish1_1)));
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, UserTestData.NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void create() throws Exception {
        DishTo expected = DishUtil.toDishTo(DishTestData.getNewDish());
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, RestaurantTestData.RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(DishTestData.getNewDish())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        DishTo actual = DishTestData.DISH_TO_MATCHER.readFromJson(action);
        int newId = actual.id();
        expected.setId(newId);
        DishTestData.DISH_TO_MATCHER.assertMatch(expected, actual);
        DishTestData.DISH_TO_MATCHER.assertMatch(
                toDishTo(checkNotFoundWithId(dishRepository.findByIdAndRestaurantId(newId, RestaurantTestData.RESTAURANT1_ID), newId)),
                expected);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createDuplicate() throws Exception {
        DishTo duplicate = DishUtil.toDishTo(new Dish(DishTestData.dish1_1));
        duplicate.setId(null);
        perform(MockMvcRequestBuilders.post(REST_URL, RestaurantTestData.RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void update() throws Exception {
        Dish expected = DishTestData.getUpdatedDish();
        int id = expected.id();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(DishUtil.toDishTo(DishTestData.getUpdatedDish()))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DishTestData.DISH_MATCHER.assertMatch(
                checkNotFoundWithId(dishRepository.findByIdAndRestaurantId(id, RestaurantTestData.RESTAURANT1_ID), id),
                DishTestData.getUpdatedDish());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateDuplicateTitle() throws Exception {
        DishTo duplicateTitleDishTo = new DishTo(null, DishTestData.DISH1_1_TITLE, 590);
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateTitleDishTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, DishTestData.DISH1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DishTestData.DISH_MATCHER.assertMatch(
                dishRepository.findAllByRestaurantIdOrderByTitle(RestaurantTestData.RESTAURANT1_ID),
                DishTestData.dish1_1, DishTestData.dish1_2, DishTestData.dish1_4);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RestaurantTestData.RESTAURANT1_ID, UserTestData.NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteAllByRestaurantId() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL, RestaurantTestData.RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DishTestData.DISH_MATCHER.assertMatch(dishRepository.findAllByRestaurantIdOrderByTitle(RestaurantTestData.RESTAURANT1_ID), List.of());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteAllByRestaurantIdNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL, UserTestData.NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
