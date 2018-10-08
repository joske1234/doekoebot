package com.team6.g.repository;

import com.team6.g.model.WordTypeEmoji;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordTypeEmojiRepository extends JpaRepository<WordTypeEmoji, Long> {
    @Override
    @Cacheable("word")
    List<WordTypeEmoji> findAll();

    @Cacheable("word")
    WordTypeEmoji findByWord(String word);

    @Override
    @CacheEvict(value = "word", allEntries = true)
    WordTypeEmoji save(WordTypeEmoji word);

    @Override
    @CacheEvict(value = "word", allEntries = true)
    void delete(WordTypeEmoji word);

    List<WordTypeEmoji> findByWordContaining(String word);
}
