package com.appspot.saymoreofthat.rest;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.appspot.saymoreofthat.rest.jaxb.UserResponse;
import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
import com.google.appengine.api.datastore.Email;

@Path("/users")
public class UserResource {
	@Path("/new")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response newUser(@FormParam("email") String emailValue,
			@Context HttpServletRequest httpServletRequest) {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		final Response response;
		try {
			final Email email = new Email(emailValue);
			Query userByEmailQuery = persistenceManager.newQuery(User.class);
			userByEmailQuery
					.declareParameters("com.google.appengine.api.datastore.Email emailParam");
			userByEmailQuery.setFilter("email == emailParam");
			List<User> oldUsersByEmail = (List<User>) userByEmailQuery
					.execute(email);
			if (oldUsersByEmail.isEmpty()) {
				User user = new User(email);
				Session session = new Session(user, httpServletRequest
						.getSession(true).getId());
				user.getSessions().add(session);
				Transaction transaction = persistenceManager.currentTransaction();
				try {
					transaction.begin();
					persistenceManager.makePersistent(user);
					transaction.commit();
				} finally {
					if (transaction.isActive()) {
						transaction.rollback();
					}
				}
				
				response = Response.ok().build();
			} else {
				response = Response.serverError().build();
			}
		} finally {
			persistenceManager.close();
		}

		return response;
	}

	@Path("/show/session")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response showUserWithSession(
			@Context HttpServletRequest httpServletRequest) {
		final Response response;
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession == null) {
			response = Response.serverError().build();
		} else {
			PersistenceManager persistenceManager = PMF.getPersistenceManager();

			try {
				Query sessionBySessionIdQuery = persistenceManager
						.newQuery(Session.class);
				sessionBySessionIdQuery
						.declareParameters("String sessionIdParam");
				sessionBySessionIdQuery.setFilter("sessionId == sessionIdParam");
				List<Session> sessionsBySessionId = (List<Session>) sessionBySessionIdQuery
						.execute(httpSession.getId());
				if (sessionsBySessionId.isEmpty()) {
					response = Response.serverError().build();
				} else {
					final UserResponse userResponse = new UserResponse();
					Session sessionWithSessionId = sessionsBySessionId.get(0);
					User user = sessionWithSessionId.getUser();
					userResponse.email = user.getEmail();
					userResponse.sessions = new ArrayList<String>(user.getSessions()
							.size());
					for (Session session : user.getSessions()) {
						userResponse.sessions.add(session.getSessionId());
					}

					response = Response.ok(userResponse).build();
				}
			} finally {
				persistenceManager.close();
			}
		}

		return response;
	}
}
