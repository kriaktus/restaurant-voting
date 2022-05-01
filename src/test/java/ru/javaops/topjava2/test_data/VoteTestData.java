package ru.javaops.topjava2.test_data;

import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.web.MatcherFactory;

import java.time.LocalDate;

import static ru.javaops.topjava2.test_data.RestaurantTestData.*;
import static ru.javaops.topjava2.test_data.UserTestData.*;

public class VoteTestData {
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Vote.class);
    public static final MatcherFactory.Matcher<Vote> VOTE_MATCHER_EXCLUDE_RESTAURANT_DISHES = MatcherFactory.usingIgnoringFieldsComparator(Vote.class, "restaurant.dishes");

    public static final int YESTERDAY_USER_VOTE_ID = 5;
    public static final int YESTERDAY_ADMIN_VOTE_ID = 6;
    public static final int TODAY_USER_VOTE_ID = 7;
    public static final int TODAY_ADMIN_VOTE_ID = 8;
    public static final int TODAY_USER2_VOTE_ID = 9;

    public static final Vote userYesterdayVote = new Vote(YESTERDAY_USER_VOTE_ID, USER_ID, LocalDate.now().minusDays(1), restaurant4);
    public static final Vote adminYesterdayVote = new Vote(YESTERDAY_ADMIN_VOTE_ID, ADMIN_ID, LocalDate.now().minusDays(1), restaurant1);
    public static final Vote userTodayVote = new Vote(TODAY_USER_VOTE_ID, USER_ID, LocalDate.now(), restaurant2);
    public static final Vote adminTodayVote = new Vote(TODAY_ADMIN_VOTE_ID, ADMIN_ID, LocalDate.now(), restaurant2);
    public static final Vote user2TodayVote = new Vote(TODAY_USER2_VOTE_ID, USER2_ID, LocalDate.now(), restaurant3);

    public static Vote getNewVote() {
        return new Vote(null, USER3_ID, LocalDate.now(), restaurant2);
    }
}
