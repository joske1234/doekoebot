package com.team6.g.repository;

import com.team6.g.model.Emoji;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiRepository extends JpaRepository<Emoji, Long> {
    @Cacheable("emoji")
    Emoji findByEmoji(String emoji);

    @Override
    @CacheEvict(value="emoji")
    Emoji save(Emoji emoji);

    @Override
    @CacheEvict(value="emoji")
    void delete(Emoji emoji);
}
