package ru.javaops.topjava2.web.vote;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.javaops.topjava2.web.AbstractControllerTest;

import java.time.*;

import static org.mockito.Mockito.doReturn;
import static ru.javaops.topjava2.test_data.RestaurantTestData.RESTAURANT2_ID;
import static ru.javaops.topjava2.test_data.UserTestData.USER3_MAIL;
import static ru.javaops.topjava2.test_data.UserTestData.USER_MAIL;

public class VoteControllerAfter11Test extends AbstractControllerTest {

    public static final String REST_URL = "/api/votes";
    @InjectMocks
    @Autowired
    private VoteController voteController;
    @Mock
    private Clock clock;

    @BeforeEach()
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
        Clock fixedClock = Clock.fixed(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        doReturn(fixedClock.instant()).when(clock).instant();
        doReturn(fixedClock.getZone()).when(clock).getZone();
    }

    @Test
    @WithUserDetails(value = USER3_MAIL)
    void createOrUpdateAfter11() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL).queryParam("restaurantId", Integer.toString(RESTAURANT2_ID)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void deleteAfter11() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }
}
