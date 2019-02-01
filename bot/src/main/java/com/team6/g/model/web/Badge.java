package com.team6.g.model.web;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Badge {
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Brussels")
    private Date badge;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBadge() {
        return badge;
    }

    public void setBadge(Date badge) {
        this.badge = badge;
    }
}
