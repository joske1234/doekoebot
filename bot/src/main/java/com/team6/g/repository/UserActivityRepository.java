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
    List<UserActivity> findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(User user);

    @Query(value = "SELECT * FROM USER_ACTIVITY WHERE USER_ID = ?1 and DATE_ADDED BETWEEN CURDATE() and DATE_ADD(CURDATE(), INTERVAL +1 DAY)", nativeQuery = true)
    UserActivity findByDateTodayAndUser(Long userId);
}
