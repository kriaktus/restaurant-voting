package com.github.kriaktus.restaurantvoting.testdata;

import com.github.kriaktus.restaurantvoting.to.VoteTo;
import com.github.kriaktus.restaurantvoting.web.MatcherFactory;

import java.time.LocalDate;

import static com.github.kriaktus.restaurantvoting.testdata.RestaurantTestData.*;

public class VoteTestData {
    public static final MatcherFactory.Matcher<VoteTo> VOTE_TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(VoteTo.class);

    public static final int YESTERDAY_USER_VOTE_TO_ID = 5;
    public static final int TODAY_USER_VOTE_TO_ID = 7;

    public static final VoteTo userYesterdayVoteTo = new VoteTo(YESTERDAY_USER_VOTE_TO_ID, LocalDate.now().minusDays(1), RESTAURANT4_ID);
    public static final VoteTo userTodayVoteTo = new VoteTo(TODAY_USER_VOTE_TO_ID, LocalDate.now(), RESTAURANT2_ID);

    public static VoteTo getNewVoteTo() {
        return new VoteTo(null, LocalDate.now(), RESTAURANT2_ID);
    }

    public static VoteTo getUpdatedVoteTo() {
        return new VoteTo(TODAY_USER_VOTE_TO_ID, LocalDate.now(), RESTAURANT1_ID);
    }
}
