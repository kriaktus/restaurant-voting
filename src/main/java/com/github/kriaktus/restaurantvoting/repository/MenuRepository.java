package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.model.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface MenuRepository extends BaseRepository<Menu> {

    @EntityGraph(attributePaths = {"items"})
    @Query(value = """
            SELECT m
            FROM Menu m
            WHERE m.menuDate=:date AND m.restaurantId=:restaurantId
            """)
    Optional<Menu> findByDateAndRestaurantId(@Param("date") LocalDate date, @Param("restaurantId") int restaurantId);

    @Query(value = """
            SELECT m
            FROM Menu m
            WHERE m.menuDate=:date AND m.restaurantId=:restaurantId
            """)
    Optional<Menu> findByDateAndRestaurantIdWithoutItems(@Param("date") LocalDate date, @Param("restaurantId") int restaurantId);

    @Query(value = """
            DELETE
            FROM Menu m
            WHERE m.menuDate=:date AND m.restaurantId=:restaurantId
            """)
    @Modifying
    @Transactional
    int deleteByDateAndRestaurantId(@Param("date") LocalDate date, @Param("restaurantId") int restaurantId);
}
