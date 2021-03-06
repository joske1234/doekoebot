package com.team6.g.repository;

import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    @Override
    @CacheEvict(value = "userActivity", allEntries = true)
    UserActivity save(UserActivity u);
    
    @Cacheable("userActivity")
    List<UserActivity> findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(User user);

    @Query(value = "SELECT * FROM user_activity WHERE user_id = ?1 and date_added BETWEEN CURDATE() and DATE_ADD(CURDATE(), INTERVAL +1 DAY)", nativeQuery = true)
    UserActivity findByDateTodayAndUser(Long userId);

    @Query(value = "SELECT * FROM user_activity WHERE user_id = ?1 and date_added BETWEEN ?2 and DATE_ADD(?2, INTERVAL +1 DAY)", nativeQuery = true)
    UserActivity findByDateAndUser(Long userId, Date date);

    UserActivity findAllByDateAddedBetweenAndUser(Date start, Date end, User user);
}
