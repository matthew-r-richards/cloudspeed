package com.zuhlke.smallface.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zuhlke.smallface.common.UserMgrFactory;
import com.zuhlke.smallface.common.UserNotFoundException;
import com.zuhlke.smallface.ejbs.User;

public class LoginPage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	displayLogin(resp, null);
    }
    
    private void printUsers() {
	Collection<User> allUsers = UserMgrFactory.getUserMgr().getAllUsers();
	for (Object element : allUsers) {
	    User user = (User) element;
	    System.out.println(user.getEmail());
	}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
	String email = req.getParameter("email");
	String password = req.getParameter("password");

	if (email == null) {
	    displayLogin(resp, null);
	} else {
	    if (req.getParameter("register") != null) {
		System.out.println("registering user " + email);
		UserMgrFactory.getUserMgr().createUser(new User(email, password));
		displayLogin(resp, null);
	    } else if (req.getParameter("login") != null) {
		try {
		    User user = verify(email, password);
		    getServletContext().setAttribute("user", user.getEmail());
		    forwardHome(resp);
		} catch (Exception e) {
		    displayLogin(resp, e.getMessage());
		}
	    }
	}
	
//	printUsers();
    }
    
    /**
     * Forward the user to the home page.
     * @param resp 
     * @throws IOException 
     */
    private void forwardHome(HttpServletResponse resp) throws IOException {
	resp.sendRedirect("");
    }

    private User verify(String email, String password) throws UserNotFoundException, IncorrectPasswordException {
	User user = UserMgrFactory.getUserMgr().findUser(email);
	if (!user.getPassword().equals(password)) {
	    throw new IncorrectPasswordException();		
	}
	return user;
    }
    
    /**
     * @param resp
     * @param errorMessage - the error message, can be <code>null</code>  
     * @throws IOException
     */
    private void displayLogin(HttpServletResponse resp, String errorMessage) throws IOException {
	resp.setContentType("text/html");
	PrintWriter out = resp.getWriter();
	out.append(
		"<html>" +
		"  <body>" +
		((errorMessage != null) ? "<b>" + errorMessage + "</b>" : "") +  
		"     <form method=\"POST\">" +
		"       <table>" +
		"       <tr><td>email:</td><td><input name=\"email\" type=\"text\"/></td></tr>" +
		"       <tr><td>password:</td><td><input name=\"password\" type=\"password\"/></td></tr>" +
		"       <tr><td colspan=\"2\"><input type=\"submit\" name=\"login\" value=\"Login\"/></td></tr>" +
		"       <tr><td colspan=\"2\"><input type=\"submit\" name=\"register\" value=\"Register\"/></td></tr>" +
		"       </table>" +
		"     </form>" +
		"  </body>" +
		"</html>"
	);
    }
}
