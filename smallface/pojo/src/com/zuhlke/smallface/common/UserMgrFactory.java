package com.zuhlke.smallface.common;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.zuhlke.smallface.pojos.UserMgrPojo;

public class UserMgrFactory {
    public static UserMgr getUserMgr() {
//	return getUserMgrBean();
	return getUserMgrPojo();	
    }
    
    private static UserMgr getUserMgrBean() {
	InitialContext ctx;
	try {
	    ctx = new InitialContext();
	    UserMgr mgr = (UserMgr) ctx.lookup("/smallface/UserMgrBean/local");
	    return mgr;
	} catch (NamingException e) {
	    throw new RuntimeException(e);
	}
    }
    
    private static UserMgr getUserMgrPojo() {
	return UserMgrPojo.getInstance();
    }
}
