package ru.javaops.topjava2.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Vote;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote>{

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT v FROM Vote v WHERE v.userId =:userId AND v.votingDate =:date")
    Vote getByUserIdAndDate(@Param("userId") int userId, @Param("date") LocalDate date);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.userId =:userId AND v.votingDate =:date")
    int deleteByUserIdAndDate(@Param("userId") int userId, @Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"restaurant"})
    @Query("SELECT v FROM Vote v WHERE v.votingDate =:date")
    List<Vote> getAllByDate(@Param("date") LocalDate date);
}
