package com.zuhlke.smallface.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zuhlke.smallface.common.UserMgrFactory;
import com.zuhlke.smallface.common.UserNotFoundException;
import com.zuhlke.smallface.pojos.Post;
import com.zuhlke.smallface.pojos.User;

public class Home extends HttpServlet {
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
	    UserMgrFactory.getUserMgr().addFriend(user.getEmail(), friendName);
//	    System.out.println("adding friend " + friendName);
//	    user.addFriend(getUserMgr().findUser(friendName));
	} catch (UserNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void postContent(HttpServletRequest req, User user) {
	String content = req.getParameter("postContent");
//	System.out.println("adding post " + content);
	Post post = new Post(content);
	post.setUser(user);
	
	UserMgrFactory.getUserMgr().createPost(post);
	user.addPost(post);
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
        StringBuilder buff = new StringBuilder();
        buff.append("You have " + user.getFriends().size() + " friends<br/>");
    
        Set<Post> posts = user.allFriendsPosts();
        
        buff.append("<table>\n");
        for (Post post : posts) {
            buff.append("<tr><td>" + post.getDate() + "</td><td>" + post.getUser().getEmail() + " said " + post.getContent() + "</td></tr>");	    
        }
        buff.append("</table>");
        
        return buff.toString();
    }

}
