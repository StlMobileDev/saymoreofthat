package com.appspot.saymoreofthat.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.User;

public class PushUsersServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(req
					.getInputStream());
			List<User> users;
			try {
				users = (List<User>) objectInputStream.readObject();
			} catch (ClassNotFoundException classNotFoundException) {
				throw new ServletException(classNotFoundException);
			} finally {
				try {
					objectInputStream.close();
				} catch (IOException ioException) {
				}
			}
			
			persistenceManager.makePersistentAll(users);
		} finally {
			persistenceManager.close();
		}
	}
}
