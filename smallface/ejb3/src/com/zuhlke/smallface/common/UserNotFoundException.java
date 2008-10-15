package com.zuhlke.smallface.common;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String user;

    public UserNotFoundException(String user) {
	super("Could not find user " + user);
	this.user = user;
    }
    
    public String getUser() {
	return user;
    }
}
