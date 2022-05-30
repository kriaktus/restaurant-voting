package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.test_data.UserTestData;
import com.github.kriaktus.restaurantvoting.test_data.VoteTestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.kriaktus.restaurantvoting.model.Vote;

import java.time.LocalDate;
import java.util.List;

public class VoteRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void getByUserIdAndDate() {
        Vote actual = voteRepository.getByUserIdAndDate(UserTestData.ADMIN_ID, LocalDate.now().minusDays(1));
        VoteTestData.VOTE_MATCHER.assertMatch(actual, VoteTestData.adminYesterdayVote);
    }

    @Test
    void getByUserIdAndDateNotFound() {
        Assertions.assertNull(voteRepository.getByUserIdAndDate(UserTestData.USER_ID, LocalDate.now().plusDays(1)));
        Assertions.assertNull(voteRepository.getByUserIdAndDate(UserTestData.NOT_FOUND, LocalDate.now()));
    }

    @Test
    void create() {
        Vote expected = VoteTestData.getNewVote();
        Vote actual = voteRepository.save(VoteTestData.getNewVote());
        expected.setId(actual.id());
        VoteTestData.VOTE_MATCHER.assertMatch(expected, actual);
    }

    @Test
    void deleteByUserIdAndDate() {
        Assertions.assertNotNull(voteRepository.getByUserIdAndDate(UserTestData.USER_ID, LocalDate.now()));
        Assertions.assertNotEquals(0, voteRepository.deleteByUserIdAndDate(UserTestData.USER_ID, LocalDate.now()));
        Assertions.assertNull(voteRepository.getByUserIdAndDate(UserTestData.USER_ID, LocalDate.now()));
    }

    @Test
    void deleteByUserIdAndDateNotFound() {
        Assertions.assertEquals(0, voteRepository.deleteByUserIdAndDate(UserTestData.USER_ID, LocalDate.now().plusDays(1)));
        Assertions.assertEquals(0, voteRepository.deleteByUserIdAndDate(UserTestData.NOT_FOUND, LocalDate.now()));
    }

    @Test
    void getAllByDate() {
        List<Vote> actual = voteRepository.getAllByDate(LocalDate.now());
        VoteTestData.VOTE_MATCHER.assertMatch(actual, List.of(VoteTestData.userTodayVote, VoteTestData.adminTodayVote, VoteTestData.user2TodayVote));
    }
}