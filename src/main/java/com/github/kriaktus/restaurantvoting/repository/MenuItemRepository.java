package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.model.MenuItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuItemRepository extends BaseRepository<MenuItem> {

    @Query(value = "SELECT mi FROM MenuItem mi WHERE mi.restaurantId=:restaurantId ")
    HashSet<MenuItem> findAllByRestaurantId(@Param("restaurantId") int restaurantId);

    @Query(value = "SELECT mi FROM Menu m JOIN m.items as mi WHERE m.menuDate = current_date AND m.restaurantId=:restaurantId AND mi.id=:id")
    Optional<MenuItem> findFromActiveMenuByIdAndRestaurantId(@Param("id") int id, @Param("restaurantId") int restaurantId);
}
