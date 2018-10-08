package com.team6.g.repository;

import com.team6.g.model.User;
import com.team6.g.model.WordCount;
import com.team6.g.model.WordCountStatistics;
import com.team6.g.model.WordTypeWordTypeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, Long> {
    List<WordCount> findByWord(WordTypeWordTypeCount word);

    @Query("SELECT new com.team6.g.model.WordCountStatistics(w.user, w.word, count(w)) FROM WordCount w WHERE w.user = ?1 GROUP BY w.user, w.word ORDER BY count(w) DESC")
    List<WordCountStatistics> findStatisticsByUser(User user);

    @Query("SELECT new com.team6.g.model.WordCountStatistics(w.user, w.word, count(w)) FROM WordCount w WHERE w.user = ?1 AND w.word = ?2 GROUP BY w.user, w.word ORDER BY count(w) DESC")
    List<WordCountStatistics> findStatisticsByUserAndWord(User user, WordTypeWordTypeCount word);

    @Query("SELECT new com.team6.g.model.WordCountStatistics(w.user, w.word, count(w)) FROM WordCount w GROUP BY w.user, w.word ORDER BY count(w) DESC")
    List<WordCountStatistics> findStatistics();

    @Query("SELECT new com.team6.g.model.WordCountStatistics(w.user, w.word, count(w)) FROM WordCount w WHERE w.word = ?1 GROUP BY w.user, w.word ORDER BY count(w) DESC")
    List<WordCountStatistics> findStatisticsByWord(WordTypeWordTypeCount word);
    
    @Query("SELECT new com.team6.g.model.WordCountStatistics(w.user, w.word, count(w)) FROM WordCount w WHERE w.word = ?1 GROUP BY w.user ORDER BY count(w) DESC")
    List<WordCountStatistics> findAllUserStatisticsGroupByUser(WordTypeWordTypeCount word);
}
