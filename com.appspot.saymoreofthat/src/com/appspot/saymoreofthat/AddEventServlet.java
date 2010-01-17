package com.appspot.saymoreofthat;

import java.io.IOException;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AddEventServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			resp.setContentType("text/html");
			resp.getWriter().println(
					"<html><body><a href=\""
							+ userService.createLoginURL(req.getRequestURI())
							+ "\">Sign in</a></body></html>");
		} else {
			resp.setContentType("text/plain");
			PersistenceManager persistenceManager = PMF.get()
					.getPersistenceManager();
			try {
				persistenceManager.makePersistent(new Event(user, req
						.getParameter("name"), new Date()));
			} finally {
				persistenceManager.close();
			}

			resp.getWriter().println("Event created");
		}
	}
}
