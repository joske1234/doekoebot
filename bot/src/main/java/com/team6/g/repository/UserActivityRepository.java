package com.team6.g.repository;

import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    @Override
    @CacheEvict(value = "userActivity", allEntries = true)
    UserActivity save(UserActivity u);
    
    @Cacheable("userActivity")
    List<UserActivity> findAllByUser(User user);

    @Query("SELECT e FROM UserActivity e where e.user = ?1 and e.dateAdded BETWEEN CURRENT_DATE and CURRENT_DATE + 1")
    UserActivity findByDateTodayAndUser(User user);
}
