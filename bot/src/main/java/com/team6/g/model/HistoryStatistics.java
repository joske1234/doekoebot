package com.team6.g.model;

public class HistoryStatistics {
    private User user;
    private long count;

    public HistoryStatistics(User user, long count) {
        this.user = user;
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
