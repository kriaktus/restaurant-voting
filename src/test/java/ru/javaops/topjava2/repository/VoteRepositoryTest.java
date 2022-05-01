package ru.javaops.topjava2.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.topjava2.model.Vote;

import java.time.LocalDate;
import java.util.List;

import static ru.javaops.topjava2.test_data.UserTestData.*;
import static ru.javaops.topjava2.test_data.VoteTestData.*;

public class VoteRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void getByUserIdAndDate() {
        Vote actual = voteRepository.getByUserIdAndDate(ADMIN_ID, LocalDate.now().minusDays(1));
        VOTE_MATCHER_EXCLUDE_RESTAURANT_DISHES.assertMatch(actual, adminYesterdayVote);
    }

    @Test
    void getByUserIdAndDateNotFound() {
        Assertions.assertNull(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now().plusDays(1)));
        Assertions.assertNull(voteRepository.getByUserIdAndDate(NOT_FOUND, LocalDate.now()));
    }

    @Test
    void create() {
        Vote expected = getNewVote();
        Vote actual = voteRepository.save(getNewVote());
        expected.setId(actual.id());
        VOTE_MATCHER.assertMatch(expected, actual);
    }

    @Test
    void deleteByUserIdAndDate() {
        Assertions.assertNotNull(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now()));
        Assertions.assertNotEquals(0, voteRepository.deleteByUserIdAndDate(USER_ID, LocalDate.now()));
        Assertions.assertNull(voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now()));
    }

    @Test
    void deleteByUserIdAndDateNotFound() {
        Assertions.assertEquals(0, voteRepository.deleteByUserIdAndDate(USER_ID, LocalDate.now().plusDays(1)));
        Assertions.assertEquals(0, voteRepository.deleteByUserIdAndDate(NOT_FOUND, LocalDate.now()));
    }

    @Test
    void getAllByDate() {
        List<Vote> actual = voteRepository.getAllByDate(LocalDate.now());
        VOTE_MATCHER_EXCLUDE_RESTAURANT_DISHES.assertMatch(actual, List.of(userTodayVote, adminTodayVote, user2TodayVote));
    }
}