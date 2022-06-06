package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.model.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @EntityGraph(attributePaths = {"menu"})
    @Query(value = "SELECT r FROM Restaurant r JOIN r.menu AS m WHERE r.id=:id AND m.menuDate = current_date")
    Optional<Restaurant> findActiveById(@Param("id") int id);

    @EntityGraph(attributePaths = {"menu"})
    @Query(value = "SELECT r FROM Restaurant r JOIN r.menu AS m WHERE m.menuDate = current_date")
    List<Restaurant> findAllActive();

    @EntityGraph(attributePaths = {"menu", "menu.items"})
    @Query(value = "SELECT r FROM Restaurant r JOIN r.menu AS m JOIN m.items AS mi WHERE r.id=:id AND m.menuDate = current_date")
    Optional<Restaurant> findByIdWithActualMenu(@Param("id") int id);

    @EntityGraph(attributePaths = {"menu", "menu.items"})
    @Query(value = "SELECT r FROM Restaurant r JOIN r.menu AS m JOIN m.items AS mi")
    List<Restaurant> findAllWithActualMenu();

    Optional<Restaurant> getRestaurantByName(String name);
}
