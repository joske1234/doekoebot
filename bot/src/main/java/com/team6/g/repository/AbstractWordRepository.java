package com.team6.g.repository;

import com.team6.g.model.AbstractWordType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AbstractWordRepository<T extends AbstractWordType, ID extends Long> extends JpaRepository<T, ID> {
    @Override
    @Cacheable("word")
    List<T> findAll();

    @Cacheable("word")
    T findByWord(String word);

    @Override
    @CacheEvict(value = "word", allEntries = true)
    <S extends T> S save(S entity);

    @Override
    @CacheEvict(value = "word", allEntries = true)
    void delete(T word);

    List<T> findByWordContaining(String word);
}
