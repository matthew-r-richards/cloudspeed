package com.zuhlke.smallface.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "SMALLFACEUSER")
public class User implements Serializable {
    @Id
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> friends = new HashSet<>();

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
    
    public User() {}
    
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void addPost(Post p) {
	    posts.add(p);
	    p.setUser(this);
    }
    
    public void addFriend(User friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
            friend.addFriend(this);
        }
    }

    @Override
    public String toString() {
        return email;
    }
}
