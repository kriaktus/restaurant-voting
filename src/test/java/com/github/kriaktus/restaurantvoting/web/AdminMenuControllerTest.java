package com.github.kriaktus.restaurantvoting.web;

import com.github.kriaktus.restaurantvoting.repository.MenuRepository;
import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.to.MenuItemTo;
import com.github.kriaktus.restaurantvoting.to.MenuTo;
import com.github.kriaktus.restaurantvoting.util.JsonUtil;
import com.github.kriaktus.restaurantvoting.util.MenuUtil;
import com.github.kriaktus.restaurantvoting.web.menuitem.AdminMenuItemController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static com.github.kriaktus.restaurantvoting.test_data.MenuItemTestData.menuItemTo1_4;
import static com.github.kriaktus.restaurantvoting.test_data.MenuItemTestData.menuItemTo2_1;
import static com.github.kriaktus.restaurantvoting.test_data.MenuTestData.*;
import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.RESTAURANT1_ID;
import static com.github.kriaktus.restaurantvoting.test_data.RestaurantTestData.RESTAURANT4_ID;
import static com.github.kriaktus.restaurantvoting.test_data.UserTestData.NOT_FOUND;
import static com.github.kriaktus.restaurantvoting.web.menu.AdminMenuController.REST_URL;

public class AdminMenuControllerTest extends AbstractControllerTest {
    @Autowired
    private MenuRepository menuRepository;

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getActual() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/actual", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_TO_MATCHER.contentJson(menuTo1));
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getActualNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/actual", NOT_FOUND))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void getActualUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(AdminMenuItemController.REST_URL + "/actual", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = UserTestData.USER_MAIL)
    void getActualForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(AdminMenuItemController.REST_URL + "/actual", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date", RESTAURANT1_ID).queryParam("date", LocalDate.now().toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_TO_MATCHER.contentJson(menuTo1));
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void getByDateNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date", RESTAURANT1_ID).queryParam("date", LocalDate.MAX.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createActualWithLocation() throws Exception {
        MenuTo expected = getNewMenuTo();
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT4_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
        MenuTo actual = MENU_TO_MATCHER.readFromJson(resultActions);
        expected.setId(actual.getId());
        List<MenuItemTo> expectedItems = expected.getItems();
        List<MenuItemTo> actualItems = actual.getItems();
        Assertions.assertEquals(expectedItems.size(), actualItems.size());
        for (int i = 0; i < expectedItems.size(); i++) {
            expectedItems.get(i).setId(actualItems.get(i).getId());
        }
        MENU_TO_MATCHER.assertMatch(actual, expected);
        MENU_TO_MATCHER.assertMatch(MenuUtil.toMenuTo(menuRepository.findByDateAndRestaurantId(LocalDate.now(), RESTAURANT4_ID).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createActualAlreadyExist() throws Exception {
        MenuTo menuTo = getNewMenuTo();
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void createActualDuplicateItemName() throws Exception {
        MenuTo menuTo = getNewMenuTo();
        List<MenuItemTo> items = menuTo.getItems();
        items.get(0).setName(items.get(1).getName());
        perform(MockMvcRequestBuilders.post(REST_URL, RESTAURANT4_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateActual() throws Exception {
        MenuTo expected = getUpdatedMenuTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "/actual", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(expected)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        MENU_TO_MATCHER.assertMatch(MenuUtil.toMenuTo(menuRepository.findByDateAndRestaurantId(LocalDate.now(), RESTAURANT1_ID).orElseThrow()), expected);
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateActualMenuNotExist() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/actual", RESTAURANT4_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedMenuTo())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateActualItemBelongAnotherRestaurant() throws Exception {
        MenuTo menuTo = getUpdatedMenuTo();
        menuTo.getItems().add(menuItemTo2_1);
        perform(MockMvcRequestBuilders.put(REST_URL + "/actual", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void updateActualDuplicateItemId() throws Exception {
        MenuTo menuTo = getUpdatedMenuTo();
        menuTo.getItems().get(0).setId(menuItemTo1_4.getId());
        perform(MockMvcRequestBuilders.put(REST_URL + "/actual", RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(menuTo)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteActualMenu() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/actual", RESTAURANT1_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertNull(menuRepository.findByDateAndRestaurantId(LocalDate.now(), RESTAURANT1_ID).orElse(null));
    }

    @Test
    @WithUserDetails(value = UserTestData.ADMIN_MAIL)
    void deleteFromActualMenuNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + "/actual", RESTAURANT4_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
