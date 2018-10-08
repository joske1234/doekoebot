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
@Table(name = "QUOTES")
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUOTE_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "TEXT", unique = true)
    private String text;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }
    
    public static final class QuoteBuilder {
        private User user;
        private String text;
        private Date dateAdded;

        private QuoteBuilder() {
        }

        public static QuoteBuilder aQuote() {
            return new QuoteBuilder();
        }

        public QuoteBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public QuoteBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public QuoteBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public Quote build() {
            Quote quote = new Quote();
            quote.setUser(user);
            quote.setText(text);
            quote.dateAdded = this.dateAdded;
            return quote;
        }
    }
}
