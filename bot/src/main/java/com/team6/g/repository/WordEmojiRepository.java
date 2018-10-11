package com.team6.g.repository;

import com.team6.g.model.WordEmoji;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordEmojiRepository extends JpaRepository<WordEmoji, Long> {
    @Cacheable("wordEmoji")
    WordEmoji findByEmojiEmojiAndWordWord(String emoji, String word);
    
    @Override
    @Cacheable("wordEmoji")
    List<WordEmoji> findAll();

    @Override
    @CacheEvict(value="wordEmoji")
    WordEmoji save(WordEmoji wordEmoji);

    @Override
    @CacheEvict(value="wordEmoji")
    void delete(WordEmoji wordEmoji);
}
