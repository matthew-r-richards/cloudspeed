package com.zuhlke.smallface.common;

import java.util.Collection;

import com.zuhlke.smallface.pojos.Post;
import com.zuhlke.smallface.pojos.User;

public interface UserMgr {

    public User findUser(String email) throws UserNotFoundException;

    public void createUser(User user);
    
    public void createUser(String email, String password);
    
    public void removeUser(String user) throws UserNotFoundException;

    public Collection<User> getAllUsers();

    public void createPost(Post post);

    public void addPost(String userEmail, String content)
	    throws UserNotFoundException;
    
    public void addFriend(String user1, String user2) throws UserNotFoundException;
}
