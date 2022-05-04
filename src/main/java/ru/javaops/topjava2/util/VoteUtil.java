package ru.javaops.topjava2.util;

import lombok.experimental.UtilityClass;
import ru.javaops.topjava2.model.Restaurant;
import ru.javaops.topjava2.model.Vote;
import ru.javaops.topjava2.to.RestaurantTo;
import ru.javaops.topjava2.to.VoteTo;

@UtilityClass
public class VoteUtil {

    public static VoteTo toVoteTo(Vote vote) {
        Restaurant restaurant = vote.getRestaurant();
        return new VoteTo(vote.getId(), vote.getVotingDate(), new RestaurantTo(restaurant.getId(), restaurant.getName()));
    }
}