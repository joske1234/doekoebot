package com.team6.g.repository;

import com.team6.g.model.EmojiCount;
import com.team6.g.model.User;
import com.team6.g.model.statistics.EmojiCountStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmojiCountRepository extends JpaRepository<EmojiCount, Long> {
    @Query("SELECT new com.team6.g.model.statistics.EmojiCountStatistics(h.user, h.emoji, count(h)) FROM EmojiCount h WHERE h.user = ?1 GROUP BY h.emoji ORDER BY count(h) DESC")
    List<EmojiCountStatistics> findAllGroupByUser(User user);

    @Query("SELECT new com.team6.g.model.statistics.EmojiCountStatistics(h.user, h.emoji, count(h)) FROM EmojiCount h GROUP BY h.emoji,h.user ORDER BY count(h) DESC")
    List<EmojiCountStatistics> findAllGroupByUser();
}
