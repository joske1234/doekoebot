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
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(name = "WORDEMOJIS", uniqueConstraints={
        @UniqueConstraint(columnNames={"WORD_ID", "EMOJI_ID"})
})
public class WordEmoji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORDEMOJI_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "WORD_ID")
    private WordTypeTypeEmoji word;

    @OneToOne
    @JoinColumn(name = "EMOJI_ID")
    private Emoji emoji;
    
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User addedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WordTypeTypeEmoji getWord() {
        return word;
    }

    public void setWord(WordTypeTypeEmoji word) {
        this.word = word;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }


    public static final class WordEmojiBuilder {
        private WordTypeTypeEmoji word;
        private Emoji emoji;
        private User addedBy;
        private Date dateAdded;

        public WordEmojiBuilder() {
        }

        public static WordEmojiBuilder aWordEmoji() {
            return new WordEmojiBuilder();
        }

        public WordEmojiBuilder withWord(WordTypeTypeEmoji word) {
            this.word = word;
            return this;
        }

        public WordEmojiBuilder withEmoji(Emoji emoji) {
            this.emoji = emoji;
            return this;
        }

        public WordEmojiBuilder withAddedBy(User addedBy) {
            this.addedBy = addedBy;
            return this;
        }

        public WordEmojiBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public WordEmoji build() {
            WordEmoji wordEmoji = new WordEmoji();
            wordEmoji.setWord(word);
            wordEmoji.setEmoji(emoji);
            wordEmoji.setAddedBy(addedBy);
            wordEmoji.dateAdded = this.dateAdded;
            return wordEmoji;
        }
    }

    @Override
    public String toString() {
        return "WordEmoji{" +
                "id=" + id +
                ", word=" + word +
                ", emoji=" + emoji +
                ", addedBy=" + addedBy +
                ", dateAdded=" + dateAdded +
                '}';
    }
}
