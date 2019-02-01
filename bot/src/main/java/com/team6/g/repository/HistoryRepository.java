package com.team6.g.repository;

import com.team6.g.model.History;
import com.team6.g.model.statistics.HistoryStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByDateAddedBetween(Date begin, Date end);

    @Query("SELECT new com.team6.g.model.statistics.HistoryStatistics(h.user, count(h)) FROM History h WHERE h.dateAdded BETWEEN ?1 AND ?2 GROUP BY h.user ORDER BY count(h) DESC")
    List<HistoryStatistics> findByDateBetweenAndGroup(Date startDate, Date endDate);

    @Query("SELECT new com.team6.g.model.statistics.HistoryStatistics(h.user, count(h)) FROM History h GROUP BY h.user ORDER BY count(h) DESC")
    List<HistoryStatistics> findAllGroupByUser();

    @Query("SELECT new com.team6.g.model.statistics.HistoryStatistics(count(h), h.dateAdded) FROM History h GROUP BY YEAR(h.dateAdded), MONTH(h.dateAdded), DAY(h.dateAdded) ORDER BY count(h) DESC")
    List<HistoryStatistics> findAllGroupByDate();
}
