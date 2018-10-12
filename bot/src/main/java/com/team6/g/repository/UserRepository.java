package com.team6.g.repository;

import com.team6.g.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable("findByName")
    User findByName(String name);

    @Override
    @CacheEvict(value="findByName", allEntries = true)
    User save(User user);
}
