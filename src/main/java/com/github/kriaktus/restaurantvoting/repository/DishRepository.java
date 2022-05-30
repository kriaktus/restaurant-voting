package com.github.kriaktus.restaurantvoting.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.github.kriaktus.restaurantvoting.model.Dish;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    Optional<Dish> findByIdAndRestaurantId(int id, int restaurantId);

    Optional<Dish> findByTitleAndRestaurantId(String title, Integer restaurantId);

    List<Dish> findAllByRestaurantIdOrderByTitle(int restaurantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurantId =:restaurantId")
    int delete(@Param("id") int id, @Param("restaurantId") int restaurantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.restaurantId =:restaurantId")
    int deleteAllByRestaurantId(@Param("restaurantId") int restaurantId);
}
