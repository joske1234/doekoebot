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
@Table(name = "WORDS")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORD_ID")
    private Long id;
    
    @Column(name = "WORD", unique = true)
    private String word;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }

    public static final class WordBuilder {
        private String word;
        private Date dateAdded;

        public WordBuilder() {
        }

        public static WordBuilder aWord() {
            return new WordBuilder();
        }

        public WordBuilder withWord(String word) {
            this.word = word;
            return this;
        }

        public WordBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Word build() {
            Word wordObj = new Word();
            wordObj.setWord(word);
            wordObj.dateAdded = this.dateAdded;
            return wordObj;
        }
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", dateAdded=" + dateAdded +
                '}';
    }
}
