package ru.javaops.topjava2.web.vote;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.web.AbstractControllerTest;

import java.time.*;

import static org.mockito.Mockito.doReturn;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT2_ID;
import static ru.javaops.topjava2.test_data.UserTestData.*;
import static ru.javaops.topjava2.test_data.VoteTestData.*;
import static ru.javaops.topjava2.util.VoteUtil.toVoteTo;

public class VoteControllerTest extends AbstractControllerTest {

    public static final String REST_URL = "/api/votes";
    @InjectMocks
    @Autowired
    private VoteController voteController;
    @Mock
    private Clock clock;
    @Autowired
    private VoteRepository voteRepository;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        Clock fixedClock = Clock.fixed(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(toVoteTo(userTodayVote)));
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void createOrUpdate() throws Exception {
        VoteTo expect = toVoteTo(getNewVote());
        ResultActions resultActions = perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        VoteTo actual = VOTE_TO_MATCHER.readFromJson(resultActions);
        expect.setId(actual.id());
        VOTE_TO_MATCHER.assertMatch(expect, actual);
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void createOrUpdateNotFound() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(NOT_FOUND)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void delete() throws Exception {
        Assertions.assertNotNull(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now(clock)));
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertNull(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now(clock)));
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void deleteNotFound() throws Exception {
        Assertions.assertNull(voteRepository.getByUserIdAndDate(USER3_ID, LocalDate.now(clock)));
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void calculateResult() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/calculate-result"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
