package com.github.kriaktus.restaurantvoting.util;

import com.github.kriaktus.restaurantvoting.to.VoteTo;
import lombok.experimental.UtilityClass;
import com.github.kriaktus.restaurantvoting.model.Restaurant;
import com.github.kriaktus.restaurantvoting.model.Vote;
import com.github.kriaktus.restaurantvoting.to.RestaurantTo;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class VoteUtil {

    public static VoteTo toVoteTo(Vote vote) {
        Restaurant restaurant = vote.getRestaurant();
        return new VoteTo(vote.getId(), vote.getVotingDate(), new RestaurantTo(restaurant.getId(), restaurant.getName()));
    }
    public static List<VoteTo> toVoteTo(Collection<Vote> votes) {
        return votes.stream()
                .map(VoteUtil::toVoteTo)
                .toList();
    }
}