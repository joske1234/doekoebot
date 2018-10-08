package com.team6.g.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "EMOJIS")
public class Emoji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMOJI_ID")
    private Long id;
    
    @Column(name = "EMOJI", unique = true)
    private String emoji;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }
    
    public static final class EmojiBuilder {
        private String emoji;
        private Date dateAdded;

        public EmojiBuilder() {
        }

        public static EmojiBuilder anEmoji() {
            return new EmojiBuilder();
        }

        public EmojiBuilder withEmoji(String emoji) {
            this.emoji = emoji;
            return this;
        }

        public EmojiBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Emoji build() {
            Emoji emojiObj = new Emoji();
            emojiObj.setEmoji(emoji);
            emojiObj.dateAdded = this.dateAdded;
            return emojiObj;
        }
    }
}
