package com.appspot.saymoreofthat.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringEscapeUtils;

import com.appspot.saymoreofthat.rest.jaxb.EventResponse;
import com.appspot.saymoreofthat.rest.jaxb.VoteResponse;
import com.appspot.saymoreofthat.rest.jdo.Event;
import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
import com.appspot.saymoreofthat.rest.jdo.Vote;
import com.google.appengine.api.datastore.KeyFactory;

@Path("/events")
public class EventsResource {
	@Path("/new")
	@Consumes(value = { MediaType.APPLICATION_FORM_URLENCODED })
	@POST
	public Response newEvent(@FormParam("name") String eventName,
			@FormParam("startTimeMillis") long startTimeMillis,
			@FormParam("timeZoneId") String timeZoneId,
			@Context HttpServletRequest httpServletRequest,
			@Context UriInfo uriInfo) {
		final Response response;
		String cleanEventName = StringEscapeUtils.escapeHtml(eventName);
		if (cleanEventName.length() == 0) {
			response = Response.status(Status.BAD_REQUEST).build();
		} else {
			HttpSession httpSession = httpServletRequest.getSession(false);
			if (httpSession == null) {
				response = Response.status(Status.BAD_REQUEST).build();
			} else {
				String sessionId = httpSession.getId();
				PersistenceManager persistenceManager = PMF
						.getPersistenceManager();
				try {
					Query sessionBySessionIdQuery = persistenceManager
							.newQuery(Session.class);
					sessionBySessionIdQuery
							.declareParameters("String sessionIdParam");
					sessionBySessionIdQuery
							.setFilter("sessionId == sessionIdParam");
					List<Session> sessionsWithSessionId = (List<Session>) sessionBySessionIdQuery
							.execute(sessionId);
					if (sessionsWithSessionId.size() == 0) {
						response = Response.status(Status.BAD_REQUEST).build();
					} else if (sessionsWithSessionId.size() == 1) {
						Transaction transaction = persistenceManager
								.currentTransaction();
						Session session = sessionsWithSessionId.get(0);
						User user = session.getUser();
						TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
						long startTimeMillisUtc = startTimeMillis
								- timeZone.getOffset(startTimeMillis);
						Event event = new Event(user, cleanEventName,
								startTimeMillisUtc);
						user.getEvents().add(event);
						try {
							transaction.begin();
							persistenceManager.makePersistent(user);
							transaction.commit();
						} finally {
							if (transaction.isActive()) {
								transaction.rollback();
							}
						}

						URI eventUri = uriInfo.getBaseUriBuilder().path(
								"events").path(
								KeyFactory.keyToString(event.getKey())).build();
						response = Response.seeOther(eventUri).build();
					} else {
						response = Response.serverError().build();
					}
				} finally {
					persistenceManager.close();
				}
			}
		}
		return response;
	}

	@Path("/{key}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public EventResponse byKey(@PathParam("key") String rawKey) {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		try {
			Event event = persistenceManager.getObjectById(Event.class,
					KeyFactory.stringToKey(rawKey));
			if (event == null) {
				throw new WebApplicationException(Status.NOT_FOUND);
			}

			EventResponse eventResponse = new EventResponse();
			eventResponse.endTimeMillisUtc = event.getEndTimeMillisUtc();
			eventResponse.id = KeyFactory.keyToString(event.getKey());
			eventResponse.name = event.getName();
			eventResponse.startTimeMillisUtc = event.getStartTimeMillisUtc();
			eventResponse.votes = new ArrayList<VoteResponse>(event.getVotes()
					.size());
			for (Vote vote : event.getVotes()) {
				VoteResponse voteResponse = new VoteResponse();
				voteResponse.timeMillisUtc = vote.getTimeMillisUtc();
				voteResponse.value = vote.getValue();
				eventResponse.votes.add(voteResponse);
			}

			return eventResponse;
		} finally {
			persistenceManager.close();
		}
	}

	@Path("/{key}/vote/{voteValue}")
	@POST
	public Response vote(@PathParam("key") String rawKey,
			@PathParam("voteValue") String rawVoteValue) {
		try {
			int voteValue = Integer.parseInt(rawVoteValue);
			final Response response;
			PersistenceManager persistenceManager = PMF.getPersistenceManager();
			try {
				Event event = persistenceManager.getObjectById(Event.class,
						KeyFactory.stringToKey(rawKey));
				if (event == null) {
					response = Response.status(Status.BAD_REQUEST).build();
				} else {
					if (event.getEndTimeMillisUtc() == 0) {
						event.getVotes().add(
								new Vote(event, System.currentTimeMillis(),
										voteValue));
						Transaction transaction = persistenceManager
								.currentTransaction();
						try {
							transaction.begin();
							persistenceManager.makePersistent(event);
							transaction.commit();
						} finally {
							if (transaction.isActive()) {
								transaction.rollback();
							}
						}

						response = Response.status(Status.NO_CONTENT).build();
					} else {
						response = Response.status(Status.BAD_REQUEST).build();
					}
				}
			} finally {
				persistenceManager.close();
			}

			return response;
		} catch (NumberFormatException numberFormatException) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@Path("/{key}/end")
	@POST
	public Response end(@PathParam("key") String rawKey,
			@Context HttpServletRequest httpServletRequest) {
		final Response response;
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession == null) {
			response = Response.status(Status.BAD_REQUEST).build();
		} else {
			String sessionId = httpSession.getId();
			PersistenceManager persistenceManager = PMF.getPersistenceManager();
			try {
				Event event = persistenceManager.getObjectById(Event.class,
						KeyFactory.stringToKey(rawKey));
				if (event.getEndTimeMillisUtc() == 0) {
					User user = event.getUser();
					boolean userHasSessionWithSessionId = false;
					for (Session session : user.getSessions()) {
						if (sessionId.equals(session.getSessionId())) {
							userHasSessionWithSessionId = true;
							break;
						}
					}

					if (userHasSessionWithSessionId) {
						event.setEndTimeMillisGmt(System.currentTimeMillis());
						Transaction currentTransaction = persistenceManager
								.currentTransaction();
						try {
							currentTransaction.begin();
							persistenceManager.makePersistent(event);
							currentTransaction.commit();
						} finally {
							if (currentTransaction.isActive()) {
								currentTransaction.rollback();
							}
						}

						response = Response.status(Status.NO_CONTENT).build();
					} else {
						response = Response.status(Status.BAD_REQUEST).build();
					}
				} else {
					response = Response.status(Status.BAD_REQUEST).build();
				}
			} finally {
				persistenceManager.close();
			}
		}

		return response;
	}

	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EventResponse> listEvents(
			@Context HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession == null) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}

		String sessionId = httpSession.getId();
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		try {
			Query sessionBySessionIdQuery = persistenceManager
					.newQuery(Session.class);
			sessionBySessionIdQuery.declareParameters("String sessionIdParam");
			sessionBySessionIdQuery.setFilter("sessionId == sessionIdParam");
			List<Session> sessionsWithSessionId = (List<Session>) sessionBySessionIdQuery
					.execute(sessionId);
			if (sessionsWithSessionId.size() == 0) {
				throw new WebApplicationException(Status.BAD_REQUEST);
			} else if (sessionsWithSessionId.size() == 1) {
				Session session = sessionsWithSessionId.get(0);
				User user = session.getUser();
				List<EventResponse> eventResponses = new ArrayList<EventResponse>(
						user.getEvents().size());
				for (Event event : user.getEvents()) {
					EventResponse eventResponse = new EventResponse();
					eventResponse.endTimeMillisUtc = event
							.getEndTimeMillisUtc();
					eventResponse.id = KeyFactory.keyToString(event.getKey());
					eventResponse.name = event.getName();
					eventResponse.startTimeMillisUtc = event
							.getStartTimeMillisUtc();
					eventResponse.votes = new ArrayList<VoteResponse>(event
							.getVotes().size());
					eventResponses.add(eventResponse);
					for (Vote vote : event.getVotes()) {
						VoteResponse voteResponse = new VoteResponse();
						voteResponse.timeMillisUtc = vote.getTimeMillisUtc();
						voteResponse.value = vote.getValue();
						eventResponse.votes.add(voteResponse);
					}
				}

				return eventResponses;
			} else {
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			}
		} finally {
			persistenceManager.close();
		}
	}
}
