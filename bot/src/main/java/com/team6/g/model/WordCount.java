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
@Table(name = "WORDCOUNTS")
public class WordCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORDCOUNT_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne
    @JoinColumn(name = "WORD_ID")
    private WordTypeWordTypeCount word;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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

    public WordTypeWordTypeCount getWord() {
        return word;
    }

    public void setWord(WordTypeWordTypeCount word) {
        this.word = word;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }

    public static final class WordCountBuilder {
        private User user;
        private WordTypeWordTypeCount word;
        private Date dateAdded;

        public WordCountBuilder() {
        }

        public static WordCountBuilder aWordCount() {
            return new WordCountBuilder();
        }

        public WordCountBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public WordCountBuilder withWord(WordTypeWordTypeCount word) {
            this.word = word;
            return this;
        }

        public WordCountBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public WordCount build() {
            WordCount wordCount = new WordCount();
            wordCount.setUser(user);
            wordCount.setWord(word);
            wordCount.dateAdded = this.dateAdded;
            return wordCount;
        }
    }
}
