package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.model.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Query("SELECT v FROM Vote v WHERE v.userId =:userId AND v.votingDate =:date")
    Optional<Vote> getByUserIdAndDate(@Param("userId") int userId, @Param("date") LocalDate date);
}
