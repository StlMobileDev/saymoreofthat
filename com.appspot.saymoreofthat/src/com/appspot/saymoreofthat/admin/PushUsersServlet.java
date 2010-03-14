package com.appspot.saymoreofthat.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.saymoreofthat.rest.jdo.AddSessionRequest;
import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;

public class PushUsersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ObjectInputStream objectInputStream = new ObjectInputStream(req
				.getInputStream());
		try {
			List<User> users = (List<User>) objectInputStream.readObject();
			PersistenceManager persistenceManager = PMF.getPersistenceManager();
			try {
				for (User user : users) {
					Query userByEmailQuery = persistenceManager
							.newQuery(User.class);
					userByEmailQuery
							.declareParameters("com.google.appengine.api.datastore.Email emailParam");
					userByEmailQuery.setFilter("email == emailParam");
					List<User> oldUsersByEmail = (List<User>) userByEmailQuery
							.execute(user.getEmail());
					if (oldUsersByEmail.isEmpty()) {
						persistenceManager.makePersistent(user);
					} else if (oldUsersByEmail.size() == 1) {
						User existingUser = oldUsersByEmail.get(0);
						for (Session session : user.getSessions()) {
							existingUser.getSessions().add(
									new Session(existingUser, session
											.getSessionId()));
						}
						for (AddSessionRequest addSessionRequest : user
								.getAddSessionRequests()) {
							existingUser.getAddSessionRequests().add(
									new AddSessionRequest(existingUser,
											addSessionRequest.getSessionId()));
						}
						persistenceManager.makePersistent(existingUser);
					} else {
						throw new ServletException(
								"There should never be more than 1 user with the email address: "
										+ user.getEmail());
					}
				}
			} finally {
				persistenceManager.close();
			}
		} catch (ClassNotFoundException classNotFoundException) {
			throw new ServletException(classNotFoundException);
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
			}
		}
	}
}
