package com.zuhlke.smallface.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zuhlke.smallface.common.UserMgr;
import com.zuhlke.smallface.common.UserMgrFactory;
import com.zuhlke.smallface.common.UserNotFoundException;
import com.zuhlke.smallface.ejbs.Post;
import com.zuhlke.smallface.ejbs.User;

public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	User user = getCurrentUser();
	if (user == null) {
	    forwardToLogin(resp);
	} else {
	    displayHome(resp, user);
	}
    }

    private void displayHome(HttpServletResponse resp, User user)
	    throws IOException {
	resp.setContentType("text/html");
	PrintWriter out = resp.getWriter();
	out.append(
	"<html>" +
	"  <body>" +
	"  Wellcome " + user.getEmail() + "<br/>" +
	printPostsFromFriends(user) +
	"  <form method=\"POST\">" +
	"    <input type=\"text\" name=\"postContent\"/>" +
	"    <input type=\"submit\" name=\"post\" value=\"New Post\">" +
	"  </form>" + 
	"  <form method=\"POST\">" +
	"    <input type=\"text\"/ name=\"friendName\">" +
	"    <input type=\"submit\" name=\"addFriend\" value=\"Add Friend\">" +
	"  </form>" +
	"  <form method=\"POST\">" +
	"    <input type=\"submit\" name=\"logout\" value=\"Logout\">" +
	"  </form>" +
	"  </body>" +
	"</html>"
	);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
	User user = getCurrentUser();
	if (user == null) {
	    forwardToLogin(resp);
	} else {
	    if (postingContent(req)) {
		postContent(req, user);
	    } else if (addingFriend(req)) {
		addFriend(req, user);
	    } else if (loggingOut(req)) {
		logout();
		resp.sendRedirect("login");
	    }
	    displayHome(resp, user);
	}
    }

    private void logout() {
	getServletContext().removeAttribute("user");
    }

    private boolean loggingOut(HttpServletRequest req) {
	return req.getParameter("logout") != null;
    }

    private User getCurrentUser() {
	try {
	    String id = (String) getServletContext().getAttribute("user");
	    if (id == null) return null;
	    return UserMgrFactory.getUserMgr().findUser(id);
	} catch (UserNotFoundException e) {
	    return null;
	}
    }

    private void addFriend(HttpServletRequest req, User user) {
	try {
	    String friendName = req.getParameter("friendName");
//	    System.out.println("adding friend " + friendName);
	    UserMgrFactory.getUserMgr().addFriend(user.getEmail(), friendName);
	} catch (UserNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private void postContent(HttpServletRequest req, User user) {
	try { 
	    String content = req.getParameter("postContent");
	    UserMgrFactory.getUserMgr().addPost(user.getEmail(), content);
	} catch (UserNotFoundException e) {
	    throw new RuntimeException(e);
	}
    }

    private boolean addingFriend(HttpServletRequest req) {
	return req.getParameter("addFriend") != null;
    }

    private boolean postingContent(HttpServletRequest req) {
	return req.getParameter("post") != null;
    }

    private void forwardToLogin(HttpServletResponse resp) throws IOException {
	resp.sendRedirect("login");
    }
    
    public String printPostsFromFriends(User user) {
	UserMgr mgr = UserMgrFactory.getUserMgr();
	String email = user.getEmail();

	StringBuilder buff = new StringBuilder();
	buff.append("You have " + mgr.getNumberOfFriends(email) + " friends<br/>");
    
	List<Post> posts = mgr.getAllFriendsPosts(email);
        
        buff.append("<table>\n");
        for (Post post : posts) {
            buff.append("<tr><td>" + post.getDate() + "</td><td>" + post.getUser().getEmail() + " said " + post.getContent() + "</td></tr>");	    
        }
        buff.append("</table>");
        
        return buff.toString();
    }

}
