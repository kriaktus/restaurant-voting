package ru.javaops.topjava2.web.vote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.service.VoteService;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.util.validation.ValidationUtil;
import ru.javaops.topjava2.web.AuthUser;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;

import static ru.javaops.topjava2.util.VoteUtil.toVoteTo;

@RestController
@RequestMapping(value = UserVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@CacheConfig(cacheNames = "votes")
public class UserVoteController {

    static final String REST_URL = "/api/votes";
    @Autowired
    private VoteService voteService;
    @Autowired
    private Clock clock;

    @GetMapping
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#get(authUser:{})", authUser);
        return toVoteTo(ValidationUtil.checkNotFoundWithMessage(
                voteService.get(authUser.id(), clock),
                String.format("No found vote for %s for user with id=%s", LocalDate.now(clock), authUser.id()))
        );
    }

    @PostMapping
    @CacheEvict(allEntries = true)
    public VoteTo vote(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("UserVoteController#vote(authUser:{}, restaurantId:{})", authUser, restaurantId);
        return toVoteTo(voteService.createOrUpdate(authUser.id(), clock, restaurantId));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#delete(authUser:{})", authUser);
        voteService.delete(authUser.id(), clock);
    }

    @GetMapping("/calculate-result")
    @Cacheable
    public Map<String, Integer> calculateResult() {
        log.info("UserVoteController#calculateResult()");
        return voteService.calculateResult(clock);
    }
}
