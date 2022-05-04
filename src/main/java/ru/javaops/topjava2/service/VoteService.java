package ru.javaops.topjava2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.repository.RestaurantRepository;
import ru.javaops.topjava2.repository.VoteRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.javaops.topjava2.util.validation.ValidationUtil.*;

@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public Vote get(int userId, Clock clock) {
        return voteRepository.getByUserIdAndDate(userId, LocalDate.now(clock));
    }

    @Transactional
    public Vote createOrUpdate(int userId, Clock clock, int restaurantId) {
        compareCurrentTimeWith(LocalTime.of(11, 0), true, "Voting ends at 11:00. You can see the results", clock);
        Vote vote = new Vote(userId, LocalDate.now(clock), checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId));
        voteRepository.deleteByUserIdAndDate(userId, LocalDate.now(clock));
        return voteRepository.save(vote);
    }

    public void delete(int userId, Clock clock) {
        compareCurrentTimeWith(LocalTime.of(11, 0), true, "You can't delete you voice after voting end", clock);
        checkModification(voteRepository.deleteByUserIdAndDate(userId, LocalDate.now(clock)));
    }

    public Map<String, Integer> calculateResult(Clock clock) {
        return voteRepository.getAllByDate(LocalDate.now(clock))
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                vote -> String.format("(id:%s) %s", vote.getRestaurant().getId(),vote.getRestaurant().getName()),
                                vote -> 1, Integer::sum),
                        map -> map.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)))
                );
    }
}
