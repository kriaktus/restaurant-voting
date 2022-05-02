package ru.javaops.topjava2.web.vote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.service.VoteService;
import ru.javaops.topjava2.web.AuthUser;

import java.time.Clock;
import java.util.Map;

@RestController
@RequestMapping(value = UserVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserVoteController {

    static final String REST_URL = "/api/votes";
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteService voteService;

    @Autowired
    private Clock clock;

    @GetMapping
    public ResponseEntity<Vote> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#get(authUser:{})", authUser);
        Vote actualVote = voteService.get(authUser.id(), clock);
        return actualVote == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualVote);
    }

    @PostMapping
    public ResponseEntity<Vote> createOrUpdate(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("UserVoteController#createOrUpdate(authUser:{}, restaurantId:{})", authUser, restaurantId);
        return ResponseEntity.ok(voteService.createOrUpdate(authUser.id(), clock, restaurantId));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#delete(authUser:{})", authUser);
        voteService.delete(authUser.id(), clock);
    }

    @GetMapping("/calculate-result")
    public ResponseEntity<Map<String, Integer>> calculateResult() {
        log.info("UserVoteController#calculateResult()");
        return ResponseEntity.ok(voteService.calculateResult(clock));
    }
}
