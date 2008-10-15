package com.zuhlke.smallface.ejbs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.zuhlke.smallface.common.UserNotFoundException;


@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    public String email;
    public String password;
    
    @ManyToMany
    public Set<User> friends;
    
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL)
    public Collection<Post> posts;
    
    public User() {}
    
    public User(String email, String password) {
	setEmail(email);
	setPassword(password);
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<User> getFriends() { return friends; }
    public void setFriends(Set<User> friends) { this.friends = friends; }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
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

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Set<Post> allFriendsPosts() {
	Set<Post> posts = new TreeSet<Post>(new User.PostComparator()); 

	posts.addAll(getPosts());
	for (User friend : friends) {
	    posts.addAll(friend.getPosts());
	}

	return posts;
    }
    
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean equals(Object other) {
	if (other == null) return false;
	if (other instanceof User) {
	    return getEmail().equals(((User) other).getEmail());
	} else return false;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public int hashCode() {
        return getEmail().hashCode();
    }
    
    public static class PostComparator implements Comparator<Post> {
        public int compare(Post p1, Post p2) {
            return p1.getDate().compareTo(p2.getDate());
        }
    }

    public int numberOfFriends() {
	return getFriends().size();
    }
}
