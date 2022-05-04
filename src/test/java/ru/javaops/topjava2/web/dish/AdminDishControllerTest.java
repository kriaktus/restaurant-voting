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
import ru.javaops.topjava2.to.DishTo;
import ru.javaops.topjava2.util.JsonUtil;
import ru.javaops.topjava2.web.AbstractControllerTest;

import java.util.List;

import static ru.javaops.topjava2.test_data.DishTestData.*;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT1_ID;
import static ru.javaops.topjava2.test_data.UserTestData.*;
import static ru.javaops.topjava2.util.DishUtil.toDishTo;
import static ru.javaops.topjava2.util.validation.ValidationUtil.checkNotFoundWithId;

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
                .andExpect(DISH_TO_MATCHER.contentJson(toDishTo(dish1_1)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/{id}", RESTAURANT1_ID, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
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
        DishTo expected = toDishTo(getNewDish());
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewDish())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        DishTo actual = DISH_TO_MATCHER.readFromJson(action);
        int newId = actual.id();
        expected.setId(newId);
        DISH_TO_MATCHER.assertMatch(expected, actual);
        DISH_TO_MATCHER.assertMatch(
                toDishTo(checkNotFoundWithId(dishRepository.findByIdAndRestaurantId(newId, RESTAURANT1_ID), newId)),
                expected);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicate() throws Exception {
        DishTo duplicate = toDishTo(new Dish(dish1_1));
        duplicate.setId(null);
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        Dish expected = getUpdatedDish();
        int id = expected.id();
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(toDishTo(getUpdatedDish()))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DISH_MATCHER.assertMatch(
                checkNotFoundWithId(dishRepository.findByIdAndRestaurantId(id, RESTAURANT1_ID), id),
                getUpdatedDish());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateTitle() throws Exception {
        DishTo duplicateTitleDishTo = new DishTo(null, DISH1_1_TITLE, 590);
        perform(MockMvcRequestBuilders.put(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicateTitleDishTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/{id}", RESTAURANT1_ID, DISH1_3_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        DISH_MATCHER.assertMatch(
                dishRepository.findAllByRestaurantIdOrderByTitle(RESTAURANT1_ID),
                dish1_1, dish1_2, dish1_4);
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
        DISH_MATCHER.assertMatch(dishRepository.findAllByRestaurantIdOrderByTitle(RESTAURANT1_ID), List.of());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteAllByRestaurantIdNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL, NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
