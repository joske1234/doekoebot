package com.team6.g.model;

public class AbstractStatistics {
    private User user;
    private WordTypeWordTypeCount word;
    private long count;

    public AbstractStatistics(User user, WordTypeWordTypeCount word, long count) {
        this.user = user;
        this.word = word;
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WordTypeWordTypeCount getWord() {
        return word;
    }

    public void setWord(WordTypeWordTypeCount word) {
        this.word = word;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "AbstractStatistics{" +
                "user=" + user +
                ", word=" + word +
                ", count=" + count +
                '}';
    }
}
