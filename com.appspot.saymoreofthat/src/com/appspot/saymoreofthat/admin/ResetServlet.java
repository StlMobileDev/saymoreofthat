package com.appspot.saymoreofthat.admin;

import java.io.IOException;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.User;

public class ResetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		try {
			Extent<User> userExtent = persistenceManager.getExtent(User.class,
					false);
			for (User user : userExtent) {
				persistenceManager.deletePersistent(user);
			}
		} finally {
			persistenceManager.close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().println(
				"<html>" + "<body>"
						+ "<form action=\"/admin/reset\" method=\"post\">"
						+ "<input type=\"submit\" />" + "</form>" + "</body>"
						+ "</html>");
	}
}
