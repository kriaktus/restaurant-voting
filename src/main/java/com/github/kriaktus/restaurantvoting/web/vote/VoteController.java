package com.github.kriaktus.restaurantvoting.web.vote;

import com.github.kriaktus.restaurantvoting.model.Vote;
import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.repository.VoteRepository;
import com.github.kriaktus.restaurantvoting.to.VoteTo;
import com.github.kriaktus.restaurantvoting.web.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;

import static com.github.kriaktus.restaurantvoting.util.VoteUtil.toVoteTo;
import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.*;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "VoteController")
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
@AllArgsConstructor
public class VoteController {

    static final String REST_URL = "/api/votes";

    private VoteRepository voteRepository;
    private RestaurantRepository restaurantRepository;
    private Clock clock;

    @Operation(summary = "#getToday", description = "Get authorized user today's voice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/today")
    public VoteTo getToday(@AuthenticationPrincipal AuthUser authUser) {
        log.info("VoteController#getToday(authUser:{})", authUser);
        int userId = authUser.id();
        return toVoteTo(checkNotFoundWithMessage(voteRepository.getByUserIdAndDate(userId, LocalDate.now()),
                String.format("Voice for date=%s to user with id=%s not found", LocalDate.now(), userId))
        );
    }

    @Operation(summary = "#getByDate", description = "Get authorized user's voice by date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/by-date")
    public VoteTo getByDate(@AuthenticationPrincipal AuthUser authUser, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("date") LocalDate date) {
        log.info("VoteController#getByDate(authUser:{})", authUser);
        int userId = authUser.id();
        return toVoteTo(checkNotFoundWithMessage(voteRepository.getByUserIdAndDate(userId, date),
                String.format("Voice for date=%s to user with id=%s not found", date, userId))
        );
    }

    @Operation(summary = "#createWithLocation", description = "Vote. Only one voice by day")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @PostMapping
    @Transactional
    public ResponseEntity<VoteTo> createWithLocation(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("VoteController#createWithLocation(authUser:{}, restaurantId:{})", authUser, restaurantId);
        int userId = authUser.id();
        checkNotFoundWithMessage(voteRepository.getByUserIdAndDate(userId, LocalDate.now()).isEmpty(), "Today's voice already exist");
        Vote created = voteRepository.save(new Vote(userId, LocalDate.now(), checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId)));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/today").build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(toVoteTo(created));
    }

    @Operation(summary = "#update", description = "Change voice. Doesn't work after 11:00")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @PutMapping("/today")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("VoteController#update(authUser:{}, restaurantId:{})", authUser, restaurantId);
        compareCurrentTimeWithDeadline(clock);
        Vote vote = checkNotFoundWithMessage(voteRepository.getByUserIdAndDate(authUser.id(), LocalDate.now(clock)), "Today's voice doesn't exist");
        vote.setRestaurant(checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId));
    }
}
