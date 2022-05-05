package ru.javaops.topjava2.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.repository.VoteRepository;
import ru.javaops.topjava2.service.VoteService;
import ru.javaops.topjava2.to.VoteTo;
import ru.javaops.topjava2.util.validation.ValidationUtil;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static ru.javaops.topjava2.util.VoteUtil.toVoteTo;

@RestController
@RequestMapping(value = AdminVoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "AdminVoteController")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
@CacheConfig(cacheNames = "votes")
@AllArgsConstructor
public class AdminVoteController {

    static final String REST_URL = "/api/admin/votes";

    private VoteService voteService;
    private VoteRepository voteRepository;
    private RestaurantRepository restaurantRepository;

    @Operation(summary = "#get", description = "Get vote by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteTo.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content)})
    @GetMapping("/{id}")
    public VoteTo get(@PathVariable int id) {
        log.info("AdminVoteController#get(id:{})", id);
        return toVoteTo(ValidationUtil.checkNotFoundWithId(voteRepository.findById(id), id));
    }

    @Operation(summary = "#getAllByDate", description = "Get all votes by day")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VoteTo.class))))
    @GetMapping("/by-date")
    public List<VoteTo> getAllByDate(@Parameter(description = "dd.MM.yyyy") @RequestParam LocalDate date) {
        log.info("AdminVoteController#getAllByDate(localDate:{})", date);
        return toVoteTo(voteRepository.getAllByDate(date));
    }

    @Operation(summary = "#getAll", description = "Get all votes")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = VoteTo.class))))
    @GetMapping
    public List<VoteTo> getAll() {
        log.info("AdminVoteController#getAll()");
        return toVoteTo(voteRepository.findAll());
    }

    @Operation(summary = "#update", description = "Change vote selection by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void update(@PathVariable int id, @RequestParam int restaurantId) {
        log.info("AdminVoteController#update(id:{}, restaurantId:{})", id, restaurantId);
        Vote vote = ValidationUtil.checkNotFoundWithId(voteRepository.findById(id), id);
        Restaurant restaurant = ValidationUtil.checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId);
        vote.setRestaurant(restaurant);
    }

    @Operation(summary = "#delete", description = "Delete vote by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int id) {
        log.info("AdminVoteController#delete(id:{})", id);
        voteRepository.deleteExisted(id);
    }

    @Operation(summary = "#calculateResultByDate", description = "Get voting results for a selected day")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(example = "{\n    \"(id:2) tartarbar\": 2,\n    \"(id:3) Duo Asia\": 1\n}")))
    @GetMapping("/calculate-result")
    public Map<String, Integer> calculateResultByDate(@Parameter(description = "dd.MM.yyyy") @RequestParam LocalDate date) {
        log.info("AdminVoteController#calculateResultByDate(localDate:{})", date);
        return voteService.calculateResult(Clock.fixed(date.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
    }
}
