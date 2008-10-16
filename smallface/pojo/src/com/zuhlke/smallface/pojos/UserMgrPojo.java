package com.zuhlke.smallface.pojos;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import com.zuhlke.smallface.common.UserMgr;
import com.zuhlke.smallface.common.UserNotFoundException;

public class UserMgrPojo implements UserMgr {

    private static UserMgr instance = new UserMgrPojo();

    private Map<String, User> users = new Hashtable<String, User>(); 
    
    public User findUser(String email) throws UserNotFoundException {
	User user = (User) users.get(email);
	if (user == null)
	    throw new UserNotFoundException(email);	
	return user;
    }

    public void createUser(User user) {
	users.put(user.getEmail(), user);
    }
    
    public void createUser(String email, String password) {
	User user = new User(email, password);
	users.put(email, user);
    }
    
    public void removeUser(String email) throws UserNotFoundException {
	User user = findUser(email);
	for (User friend : user.getFriends()) {
	    friend.removeFriend(user);
	}
	users.remove(user.getEmail());
    }

    public Collection<User> getAllUsers() {
	return users.values();
    }
    
    public void addPost(String userEmail, String content) throws UserNotFoundException {
	User u = findUser(userEmail);
	Post p = new Post(content);
	p.setUser(u);
	u.addPost(p);
    }
    
    public void createPost(Post p) {
    }
    
    @Override
    public void addFriend(String user1, String user2) throws UserNotFoundException {
	User u1 = findUser(user1);
	User u2 = findUser(user2);
	u1.addFriend(u2);
    }

    public static UserMgr getInstance() {
	return instance;
    }
}
