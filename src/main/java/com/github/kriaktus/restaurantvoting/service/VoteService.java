package com.github.kriaktus.restaurantvoting.service;

import com.github.kriaktus.restaurantvoting.repository.RestaurantRepository;
import com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.kriaktus.restaurantvoting.model.Vote;
import com.github.kriaktus.restaurantvoting.repository.VoteRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil.*;

@Service
@CacheConfig(cacheNames = "votes")
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public Vote get(int userId, Clock clock) {
        return voteRepository.getByUserIdAndDate(userId, LocalDate.now(clock));
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Vote createOrUpdate(int userId, Clock clock, int restaurantId) {
        ValidationUtil.compareCurrentTimeWith(LocalTime.of(11, 0), true, "Voting ends at 11:00. You can see the results", clock);
        Vote vote = new Vote(userId, LocalDate.now(clock), checkNotFoundWithId(restaurantRepository.findById(restaurantId), restaurantId));
        voteRepository.deleteByUserIdAndDate(userId, LocalDate.now(clock));
        return voteRepository.save(vote);
    }

    @CacheEvict(allEntries = true)
    public void delete(int userId, Clock clock) {
        ValidationUtil.compareCurrentTimeWith(LocalTime.of(11, 0), true, "You can't delete you voice after voting end", clock);
        ValidationUtil.checkModification(voteRepository.deleteByUserIdAndDate(userId, LocalDate.now(clock)));
    }

    @Cacheable
    public Map<String, Integer> calculateResult(Clock clock) {
        return voteRepository.getAllByDate(LocalDate.now(clock))
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                vote -> String.format("(id:%s) %s", vote.getRestaurant().getId(), vote.getRestaurant().getName()),
                                vote -> 1, Integer::sum),
                        map -> map.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)))
                );
    }
}
