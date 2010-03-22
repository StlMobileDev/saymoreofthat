package com.appspot.saymoreofthat.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import javax.ws.rs.core.Response.Status;

import com.appspot.saymoreofthat.rest.jaxb.UserResponse;
import com.appspot.saymoreofthat.rest.jdo.AddSessionRequest;
import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.KeyFactory;

@Path("/users")
public class UserResource {
	@Path("/session/confirm")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@POST
	public Response confirmAddSessionRequest(
			@FormParam("key") String addSessionRequestKey) {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();

		final Response response;
		try {
			Transaction transaction = persistenceManager.currentTransaction();

			try {
				transaction.begin();
				AddSessionRequest addSessionRequest = persistenceManager
						.getObjectById(AddSessionRequest.class, KeyFactory
								.stringToKey(addSessionRequestKey));
				if (addSessionRequest == null) {
					response = Response.status(Status.BAD_REQUEST).build();
				} else {
					User user = addSessionRequest.getUser();
					user.getSessions()
							.add(
									new Session(user, addSessionRequest
											.getSessionId()));
					user.getAddSessionRequests().remove(addSessionRequest);

					persistenceManager.makePersistent(user);
					response = Response.status(Status.NO_CONTENT).build();
				}

				transaction.commit();
			} finally {
				if (transaction.isActive()) {
					transaction.rollback();
				}
			}
		} finally {
			persistenceManager.close();
		}

		return response;
	}

	@Path("/session/deny")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@POST
	public Response denyAddSessionRequest(
			@FormParam("key") String addSessionRequestKey) {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();

		final Response response;
		try {
			Transaction transaction = persistenceManager.currentTransaction();

			try {
				transaction.begin();
				AddSessionRequest addSessionRequest = persistenceManager
						.getObjectById(AddSessionRequest.class, KeyFactory
								.stringToKey(addSessionRequestKey));
				if (addSessionRequest == null) {
					response = Response.status(Status.BAD_REQUEST).build();
				} else {
					User user = addSessionRequest.getUser();
					user.getAddSessionRequests().remove(addSessionRequest);

					persistenceManager.makePersistent(user);
					response = Response.status(Status.NO_CONTENT).build();
				}

				transaction.commit();
			} finally {
				if (transaction.isActive()) {
					transaction.rollback();
				}
			}
		} finally {
			persistenceManager.close();
		}

		return response;
	}

	@Path("/session/new")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@POST
	public Response newSession(@Context HttpServletRequest httpServletRequest) {
		httpServletRequest.getSession(true);
		return Response.ok().build();
	}

	@Path("/new")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@POST
	public Response newUser(@FormParam("email") String emailValue,
			@Context HttpServletRequest httpServletRequest)
			throws AddressException, MessagingException {
		final Response response;
		if (emailValue.length() == 0) {
			response = Response.status(Status.BAD_REQUEST).build();
		} else {
			PersistenceManager persistenceManager = PMF.getPersistenceManager();

			try {
				final Email email = new Email(emailValue);
				Query userByEmailQuery = persistenceManager
						.newQuery(User.class);
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
					Transaction transaction = persistenceManager
							.currentTransaction();
					try {
						transaction.begin();
						persistenceManager.makePersistent(user);
						transaction.commit();
					} finally {
						if (transaction.isActive()) {
							transaction.rollback();
						}
					}

					response = Response.noContent().build();
				} else {
					if (oldUsersByEmail.size() > 1) {
						response = Response.serverError().build();
					} else {
						User user = oldUsersByEmail.get(0);
						boolean userHasSessionWithSessionId = false;
						String sessionId = httpServletRequest.getSession(true)
								.getId();
						for (Session session : user.getSessions()) {
							if (session.getSessionId().equals(sessionId)) {
								userHasSessionWithSessionId = true;
								break;
							}
						}

						if (userHasSessionWithSessionId) {
							response = Response.noContent().build();
						} else {
							Transaction transaction = persistenceManager
									.currentTransaction();
							AddSessionRequest addSessionRequest = new AddSessionRequest(
									user, sessionId);
							try {
								transaction.begin();
								user.getAddSessionRequests().add(
										addSessionRequest);
								persistenceManager.makePersistent(user);
								transaction.commit();
							} finally {
								if (transaction.isActive()) {
									transaction.rollback();
								}
							}

							javax.mail.Session mailSession = javax.mail.Session
									.getDefaultInstance(new Properties(), null);

							Message message = new MimeMessage(mailSession);
							message
									.addFrom(new Address[] { new InternetAddress(
											"stl.mobile.dev@gmail.com") });
							message.addRecipient(Message.RecipientType.TO,
									new InternetAddress(emailValue));
							message
									.setSubject("Say More of That: Allow Session?");
							message.setText(KeyFactory
									.keyToString(addSessionRequest.getKey()));
							Transport.send(message);

							response = Response.status(Status.ACCEPTED).build();
						}
					}
				}
			} finally {
				persistenceManager.close();
			}
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
				sessionBySessionIdQuery
						.setFilter("sessionId == sessionIdParam");
				List<Session> sessionsBySessionId = (List<Session>) sessionBySessionIdQuery
						.execute(httpSession.getId());
				if (sessionsBySessionId.isEmpty()) {
					response = Response.serverError().build();
				} else {
					final UserResponse userResponse = new UserResponse();
					Session sessionWithSessionId = sessionsBySessionId.get(0);
					User user = sessionWithSessionId.getUser();
					userResponse.email = user.getEmail();
					userResponse.sessions = new ArrayList<String>(user
							.getSessions().size());
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
