package com.team6.g.repository;

import com.team6.g.model.EmojiCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiCountRepository extends JpaRepository<EmojiCount, Long> {
}
