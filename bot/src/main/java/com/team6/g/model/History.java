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
@Table(name = "HISTORY")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    
    @Column(name = "MESSAGE", length = 10000)
    private String message;
            
    @Column(name = "SLACK_TIMESTAMP")
    private String ts;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }


    public static final class HistoryBuilder {
        private User user;
        private String message;
        private String ts;
        private Date dateAdded;

        public HistoryBuilder() {
        }

        public static HistoryBuilder aHistory() {
            return new HistoryBuilder();
        }

        public HistoryBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public HistoryBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public HistoryBuilder withTs(String ts) {
            this.ts = ts;
            return this;
        }

        public HistoryBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public History build() {
            History history = new History();
            history.setUser(user);
            history.setMessage(message);
            history.ts = this.ts;
            history.dateAdded = this.dateAdded;
            return history;
        }
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", user=" + user +
                ", message='" + message + '\'' +
                ", ts='" + ts + '\'' +
                ", dateAdded=" + dateAdded +
                '}';
    }
}
