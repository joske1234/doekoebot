package com.team6.g.repository;

import com.team6.g.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable("user")
    User findByName(String name);

    @Override
    @CacheEvict(value="user", allEntries = true)
    User save(User user);
    
    @Cacheable("user")
    List<User> findByWorkPeriodMinutesIsNotNull();
}
