package com.zuhlke.smallface.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.zuhlke.smallface.common.UserNotFoundException;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String password;
    private Set<User> friends = new HashSet<User>();
    private Collection<Post> posts = new ArrayList<Post>();
    
    public User(String email, String password) {
	setEmail(email);
	setPassword(password);
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Set<User> getFriends() { return friends; }
    public void setFriends(Set<User> friends) { this.friends = friends; }
    
    public Collection<Post> getPosts() { return posts; } 
    public void setPosts(Collection<Post> posts) { this.posts = posts; }

    public void addPost(Post p) {
	getPosts().add(p);
    }
    
    public void addFriend(User friend) throws UserNotFoundException {
	if (!friends.contains(friend)) {
	    friends.add(friend);
	    friend.addFriend(this);
	}
    }
    
    public void removeFriend(User friend) {
	if (friends.contains(friend)) {
	    friends.remove(friend);
	    friend.removeFriend(this);
	}
    }

    public Set<Post> allFriendsPosts() {
	Set<Post> posts = new TreeSet<Post>(new User.PostComparator()); 

	posts.addAll(getPosts());
	for (User friend : friends) {
	    posts.addAll(friend.getPosts());
	}

	return posts;
    }
    
    @Override
    public boolean equals(Object other) {
	if (other == null) return false;
	if (other instanceof User) {
	    return getEmail().equals(((User) other).getEmail());
	} else return false;
    }
    
    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }
    
    public static class PostComparator implements Comparator<Post> {
        public int compare(Post p1, Post p2) {
            return p1.getDate().compareTo(p2.getDate());
        }
    }
}
