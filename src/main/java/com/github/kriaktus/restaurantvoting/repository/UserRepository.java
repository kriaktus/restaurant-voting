package com.github.kriaktus.restaurantvoting.repository;

import com.github.kriaktus.restaurantvoting.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {
    @Cacheable("user")
    Optional<User> getByEmail(String email);
}