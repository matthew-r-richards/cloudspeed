package com.zuhlke.smallface.common;

import java.util.Collection;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.zuhlke.smallface.ejbs.Post;
import com.zuhlke.smallface.ejbs.User;

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

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Post> getAllFriendsPosts(String userName);

    public int getNumberOfFriends(String userName);
}
