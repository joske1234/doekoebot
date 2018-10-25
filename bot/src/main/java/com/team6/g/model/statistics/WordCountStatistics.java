package com.team6.g.model.statistics;

import com.team6.g.model.User;
import com.team6.g.model.WordTypeWordTypeCount;

public class WordCountStatistics extends AbstractStatistics {
    public WordCountStatistics(User user, WordTypeWordTypeCount word, long count) {
        super(user, word, count);
    }
}
