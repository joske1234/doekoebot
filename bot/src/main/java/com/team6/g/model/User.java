package com.team6.g.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "NAME", unique = true)
    private String name;
    
    @Column(name = "WORK_PERIOD_MINUTES")
    private Integer workPeriodMinutes;

    public Integer getWorkPeriodMinutes() {
        return workPeriodMinutes;
    }

    public void setWorkPeriodMinutes(Integer workPeriodMinutes) {
        this.workPeriodMinutes = workPeriodMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final class UserBuilder {
        private Long id;
        private String name;
        private Integer workPeriodMinutes;

        public UserBuilder() {
        }

        public static UserBuilder anUser() {
            return new UserBuilder();
        }

        public UserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder withWorkPeriodMinutes(Integer workPeriodMinutes) {
            this.workPeriodMinutes = workPeriodMinutes;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setWorkPeriodMinutes(workPeriodMinutes);
            return user;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", workPeriodMinutes=" + workPeriodMinutes +
                '}';
    }
}
