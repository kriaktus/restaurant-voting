package com.github.kriaktus.restaurantvoting.web.vote;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.github.kriaktus.restaurantvoting.testdata.RestaurantTestData.RESTAURANT2_ID;
import static com.github.kriaktus.restaurantvoting.testdata.UserTestData.USER3_MAIL;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.DEADLINE_CHANGE_VOICE;
import static com.github.kriaktus.restaurantvoting.web.vote.VoteController.REST_URL;

public class VoteControllerAfterDeadlineTest extends AbstractVoteControllerTest {

    @PostConstruct
    private void setClock() {
        Clock fixedClock = Clock.fixed(LocalDateTime.of(LocalDate.now(), DEADLINE_CHANGE_VOICE.plusHours(1)).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        ReflectionTestUtils.setField(unwrapVoteController(), "clock", fixedClock);
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void createAfterDeadlineIsOk() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void updateAfterDeadlineIsLocked() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/today").queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isLocked());
    }
}
