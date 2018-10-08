package com.team6.g.repository;

import com.team6.g.model.WordTypeWordTypeCount;
import org.springframework.stereotype.Repository;

@Repository
public interface WordTypeWordCountRepository extends AbstractWordRepository<WordTypeWordTypeCount, Long> {
}
