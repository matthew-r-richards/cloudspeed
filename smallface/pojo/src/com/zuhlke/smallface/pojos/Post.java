package com.zuhlke.smallface.pojos;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private User user;
    private Date date;
    private String content;
    
    public Post(String content) {
	this.date = new Date();
	this.content = content;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getContent() { return content; } 
    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
