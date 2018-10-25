package com.team6.g.model.statistics;

import com.team6.g.model.Emoji;
import com.team6.g.model.User;

public class EmojiCountStatistics {
    private User user;
    private Emoji emoji;
    private long count;

    public EmojiCountStatistics(User user, Emoji emoji, long count) {
        this.user = user;
        this.emoji = emoji;
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
