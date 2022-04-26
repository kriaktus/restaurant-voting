package ru.javaops.topjava2.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.topjava2.model.Dish;

import java.util.List;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    @Query("SELECT d FROM Dish d WHERE d.id=:id AND d.restaurantId =:restaurantId")
    Dish get(@Param("id") int id, @Param("restaurantId") int restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.restaurantId =:restaurantId ORDER BY d.title")
    List<Dish> getAllByRestaurantId(@Param("restaurantId") int restaurantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurantId =:restaurantId")
    int delete(@Param("id") int id, @Param("restaurantId") int restaurantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.restaurantId =:restaurantId")
    int deleteAllByRestaurantId(@Param("restaurantId") int restaurantId);
}
