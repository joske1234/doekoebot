package com.team6.g.model.statistics;

import com.team6.g.model.User;

import java.util.Date;

public class HistoryStatistics {
    private User user;
    private long count;
    private Date date;
    
    public HistoryStatistics(User user, long count) {
        this.user = user;
        this.count = count;
    }

    public HistoryStatistics(long count, Date date) {
        this.count = count;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HistoryStatistics{" +
                "user=" + user +
                ", count=" + count +
                ", date=" + date +
                '}';
    }
}
