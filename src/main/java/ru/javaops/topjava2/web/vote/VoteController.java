package ru.javaops.topjava2.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "VoteController")
@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
public class VoteController {

    static final String REST_URL = "/api/votes";
    @Autowired
    private VoteService voteService;
    @Autowired
    private Clock clock;

    @Operation(summary = "#get", description = "Get authorized user today's vote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#get(authUser:{})", authUser);
        return toVoteTo(ValidationUtil.checkNotFoundWithMessage(
                voteService.get(authUser.id(), clock),
                String.format("No found vote for %s for user with id=%s", LocalDate.now(clock), authUser.id()))
        );
    }

    @Operation(summary = "#vote", description = "Vote for today or change your voice (cast earlier today). After 11:00 the decision cannot be changed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "406", description = "Not Acceptable", content = @Content)})
    @PostMapping
    public VoteTo vote(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("UserVoteController#vote(authUser:{}, restaurantId:{})", authUser, restaurantId);
        return toVoteTo(voteService.createOrUpdate(authUser.id(), clock, restaurantId));
    }

    @Operation(summary = "#delete", description = "Delete today voice. After 11:00 voice cannot be deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "406", description = "Not Acceptable"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        log.info("UserVoteController#delete(authUser:{})", authUser);
        voteService.delete(authUser.id(), clock);
    }

    @Operation(summary = "#calculateResult", description = "Get voting results (preliminary or final after 11:00) for today")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(example = "{\n    \"(id:2) tartarbar\": 2,\n    \"(id:3) Duo Asia\": 1\n}")))
    @GetMapping("/calculate-result")
    public Map<String, Integer> calculateResult() {
        log.info("UserVoteController#calculateResult()");
        return voteService.calculateResult(clock);
    }
}
