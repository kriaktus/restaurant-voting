package com.github.kriaktus.restaurantvoting.web.vote;

import com.github.kriaktus.restaurantvoting.repository.VoteRepository;
import com.github.kriaktus.restaurantvoting.to.VoteTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.github.kriaktus.restaurantvoting.testdata.RestaurantTestData.RESTAURANT1_ID;
import static com.github.kriaktus.restaurantvoting.testdata.RestaurantTestData.RESTAURANT2_ID;
import static com.github.kriaktus.restaurantvoting.testdata.UserTestData.*;
import static com.github.kriaktus.restaurantvoting.testdata.VoteTestData.*;
import static com.github.kriaktus.restaurantvoting.util.VoteUtil.toVoteTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.DEADLINE_CHANGE_VOICE;
import static com.github.kriaktus.restaurantvoting.web.vote.VoteController.REST_URL;

public class VoteControllerTest extends AbstractVoteControllerTest {

    @Autowired
    private VoteRepository voteRepository;

    @PostConstruct
    private void setClock() {
        Clock fixedClock = Clock.fixed(LocalDateTime.of(LocalDate.now(), DEADLINE_CHANGE_VOICE.minusHours(1)).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        ReflectionTestUtils.setField(unwrapVoteController(), "clock", fixedClock);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getToday() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(userTodayVoteTo));
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void getTodayNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/today"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(userYesterdayVoteTo));
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void create() throws Exception {
        VoteTo expected = getNewVoteTo();
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        VoteTo actual = VOTE_TO_MATCHER.readFromJson(resultActions);
        expected.setId(actual.id());
        VOTE_TO_MATCHER.assertMatch(actual, expected);
        VOTE_TO_MATCHER.assertMatch(toVoteTo(voteRepository.getByUserIdAndDate(USER3_ID, LocalDate.now()).get()), expected);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createVoteAlreadyExist() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void createNotFound() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(NOT_FOUND)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        VoteTo expected = getUpdatedVoteTo();
        perform(MockMvcRequestBuilders.put(REST_URL + "/today").queryParam("restaurantId", Integer.toString(RESTAURANT1_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        VOTE_TO_MATCHER.assertMatch(toVoteTo(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now()).get()), expected);
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void updateVoteNotExist() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/today").queryParam("restaurantId", Integer.toString(RESTAURANT1_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/today").queryParam("restaurantId", Integer.toString(NOT_FOUND)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
