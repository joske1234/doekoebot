package com.team6.g.repository;

import com.team6.g.model.Word;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WordRepository extends JpaRepository<Word, Long> {
    @Override
    @Cacheable("word")
    List<Word> findAll();
    
    @Cacheable("word")
    Word findByWord(String word);

    @Override
    @CacheEvict(value="word", allEntries=true)
    Word save(Word word);

    @Override
    @CacheEvict(value="word", allEntries=true)
    void delete(Word word);
    
    List<Word> findByWordContaining(String word);
}
