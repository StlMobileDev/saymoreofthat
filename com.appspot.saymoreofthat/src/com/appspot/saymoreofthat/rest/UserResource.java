package com.appspot.saymoreofthat.rest;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.appspot.saymoreofthat.rest.jaxb.UserResponse;
import com.appspot.saymoreofthat.rest.jdo.PMF;
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
			List<User> oldUsersByEmail = queryUsersByEmail(persistenceManager,
					email);
			final User user;
			if (oldUsersByEmail.isEmpty()) {
				user = new User(email);
				user.sessionIds
						.add(httpServletRequest.getSession(true).getId());
				persistenceManager.makePersistent(user);
				response = Response.ok().build();
			} else {
				response = Response.serverError().build();
			}
		} finally {
			persistenceManager.close();
		}

		return Response.ok().build();
	}

	// @Path("/show/session")
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// public UserResponse showUserWithSession(
	// @Context HttpServletRequest httpServletRequest) {
	// PersistenceManager persistenceManager = PMF.getPersistenceManager();
	//
	// final UserResponse userResponse;
	// try {
	// Query userByEmailQuery = persistenceManager.newQuery(User.class);
	// userByEmailQuery.declareParameters("com.google.appengine.api.datastore.Email emailParam");
	// userByEmailQuery.setFilter("email == emailParam");
	//			
	// List<User> usersByEmail = (List<User>) userByEmailQuery.execute(new
	// Email(emailValue));
	// if (usersByEmail.isEmpty()) {
	// userResponse = null;
	// } else {
	// userResponse = new UserResponse();
	// User user = usersByEmail.get(0);
	// userResponse.email = user.email;
	// userResponse.key = user.key;
	// userResponse.sessions = new ArrayList<String>(user.sessionIds);
	// }
	// } finally {
	// persistenceManager.close();
	// }
	//		
	// return userResponse;
	// }

	@Path("/show/email/{email}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserResponse showUserWithEmail(@PathParam("email") String emailValue) {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();

		final UserResponse userResponse;
		try {
			List<User> usersByEmail = queryUsersByEmail(persistenceManager,
					new Email(emailValue));
			if (usersByEmail.isEmpty()) {
				userResponse = null;
			} else {
				userResponse = new UserResponse();
				User user = usersByEmail.get(0);
				userResponse.email = user.email;
				userResponse.key = user.key;
				userResponse.sessions = new ArrayList<String>(user.sessionIds);
			}
		} finally {
			persistenceManager.close();
		}

		return userResponse;
	}

	private List<User> queryUsersByEmail(PersistenceManager persistenceManager,
			Email email) {
		Query userByEmailQuery = persistenceManager.newQuery(User.class);
		userByEmailQuery
				.declareParameters("com.google.appengine.api.datastore.Email emailParam");
		userByEmailQuery.setFilter("email == emailParam");

		return (List<User>) userByEmailQuery.execute(email);
	}
}
