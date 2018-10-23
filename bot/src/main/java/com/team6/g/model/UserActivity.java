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
@Table(name = "USER_ACTIVITY", uniqueConstraints={
        @UniqueConstraint(columnNames={"USER_ID", "DATE_ADDED"})
})
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ACTIVITY_ID")
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_ADDED", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateAdded;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_IN", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateIn;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_OUT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateOut;

    @PrePersist
    void dateAdded() {
        this.dateAdded = this.dateAdded = new Date();
    }

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

    public Date getDateAdded() {
        return dateAdded;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public Date getDateOut() {
        return dateOut;
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }

    public void setDateOut(Date dateOut) {
        this.dateOut = dateOut;
    }

    

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", user=" + user +
                ", dateAdded=" + dateAdded +
                ", dateIn=" + dateIn +
                ", dateOut=" + dateOut +
                '}';
    }

    public static final class UserActivityBuilder {
        private Long id;
        private User user;
        private Date dateAdded;
        private Date dateIn;
        private Date dateOut;

        public UserActivityBuilder() {
        }

        public static UserActivityBuilder anUserActivity() {
            return new UserActivityBuilder();
        }

        public UserActivityBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserActivityBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public UserActivityBuilder withDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public UserActivityBuilder withDateIn(Date dateIn) {
            this.dateIn = dateIn;
            return this;
        }

        public UserActivityBuilder withDateOut(Date dateOut) {
            this.dateOut = dateOut;
            return this;
        }

        public UserActivity build() {
            UserActivity userActivity = new UserActivity();
            userActivity.setId(id);
            userActivity.setUser(user);
            userActivity.setDateIn(dateIn);
            userActivity.setDateOut(dateOut);
            userActivity.dateAdded = this.dateAdded;
            return userActivity;
        }
    }
}
