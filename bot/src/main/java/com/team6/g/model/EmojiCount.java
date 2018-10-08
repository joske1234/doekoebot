package com.team6.g.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "EMOJICOUNTS")
public class EmojiCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMOJICOUNT_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne
    @JoinColumn(name = "EMOJI_ID")
    private Emoji emoji;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }


    public static final class EmojiCountBuilder {
        private User user;
        private Emoji emoji;
        private Date dateAdded;

        private EmojiCountBuilder() {
        }

        public static EmojiCountBuilder anEmojiCount() {
            return new EmojiCountBuilder();
        }

        public EmojiCountBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public EmojiCountBuilder withEmoji(Emoji emoji) {
            this.emoji = emoji;
            return this;
        }

        public EmojiCountBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public EmojiCount build() {
            EmojiCount emojiCount = new EmojiCount();
            emojiCount.setUser(user);
            emojiCount.setEmoji(emoji);
            emojiCount.dateAdded = this.dateAdded;
            return emojiCount;
        }
    }
}
