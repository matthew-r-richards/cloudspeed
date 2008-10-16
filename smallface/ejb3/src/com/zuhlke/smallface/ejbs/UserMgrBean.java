package com.zuhlke.smallface.ejbs;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.zuhlke.smallface.common.UserMgr;
import com.zuhlke.smallface.common.UserNotFoundException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class UserMgrBean implements UserMgr {

    @PersistenceContext
    public EntityManager em;

    public User findUser(String email) throws UserNotFoundException {
	User user = (User) em.find(User.class, email);
	if (user == null)
	    throw new UserNotFoundException(email);	
	return user;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUser(User user) {
	em.persist(user);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUser(String email, String password) {
	User user = new User(email, password);
	em.persist(user);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeUser(String email) {
	User user = (User) em.find(User.class, email);
	for (User friend : user.getFriends()) {
	    friend.removeFriend(user);
	}
	em.remove(user);
    }

    public List getAllUsers() {
	return em.createQuery("SELECT u FROM User u").getResultList();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addPost(String userEmail, String content) throws UserNotFoundException {
	User u = findUser(userEmail);
	Post p = new Post(content);
	p.setUser(u);
	u.addPost(p);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createPost(Post p) {
	em.persist(p);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addFriend(String user1, String user2) throws UserNotFoundException {
	if (user1.equals(user2)) return; // adding self doesn't count
	
	User u1 = findUser(user1);
	User u2 = findUser(user2);
	u1.addFriend(u2);
    }
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Post> getAllFriendsPosts(String userName) {
	return em.createQuery("SELECT DISTINCT p " +
			"FROM User u JOIN u.friends f JOIN f.posts p JOIN p.user o " +
			"WHERE u.email=?1 OR o.email = ?2")
			.setParameter(1, userName)
			.setParameter(2, userName)
			.getResultList();
    }
    
    public int getNumberOfFriends(String userName) {
	Long count = (Long) em.createQuery("SELECT count(f) " +
		"FROM User u JOIN u.friends f " +
		"WHERE u.email=?1")
		.setParameter(1, userName)
		.getSingleResult();
	return count.intValue();
    }
}
