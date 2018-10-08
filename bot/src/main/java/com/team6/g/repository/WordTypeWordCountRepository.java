package com.team6.g.repository;

import com.team6.g.model.WordTypeWordCount;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordTypeWordCountRepository extends JpaRepository<WordTypeWordCount, Long> {
    @Override
    @Cacheable("word")
    List<WordTypeWordCount> findAll();

    @Cacheable("word")
    WordTypeWordCount findByWord(String word);

    @Override
    @CacheEvict(value="word", allEntries=true)
    WordTypeWordCount save(WordTypeWordCount word);

    @Override
    @CacheEvict(value="word", allEntries=true)
    void delete(WordTypeWordCount word);

    List<WordTypeWordCount> findByWordContaining(String word);
}
