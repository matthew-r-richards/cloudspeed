package com.zuhlke.smallface.entities;

import org.hibernate.annotations.CascadeType;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Post implements Serializable {
    @Id @GeneratedValue private Long id;
    private Date date;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post() {
	this.date = new Date();
    }
    
    public Post(String content) {
        this();
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
