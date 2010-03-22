package com.appspot.saymoreofthat.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import junit.framework.Assert;

import com.appspot.saymoreofthat.rest.jaxb.EventResponse;
import com.appspot.saymoreofthat.rest.jdo.AddSessionRequest;
import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.KeyFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import cuke4duke.Before;
import cuke4duke.Given;
import cuke4duke.Then;
import cuke4duke.When;

public class ClientSteps {
	private Client client;
	private Map<String, List<NewCookie>> sessionKeyToNewCookies = new HashMap<String, List<NewCookie>>();
	private List<NewCookie> adminNewCookies = new ArrayList<NewCookie>();
	private ClientResponse clientResponse;

	@Before
	public void resetDatabase() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses()
				.add(SerializableMessageBodyReaderWriter.class);
		client = Client.create(clientConfig);

		client.setFollowRedirects(false);
		Form loginForm = new Form();
		loginForm.add("email", "test@example.com");
		loginForm.add("isAdmin", "on");
		loginForm.add("continue", "/admin/reset");
		loginForm.add("action", "Log In");
		URI loginUri = UriBuilder.fromUri(
				"http://localhost:8888/_ah/login?continue=%2Fadmin%2Freset")
				.build();
		ClientRequest loginClientRequest = ClientRequest.create().entity(
				loginForm, MediaType.APPLICATION_FORM_URLENCODED_TYPE).build(
				loginUri, "POST");
		ClientResponse loginClientResponse = handleAdminClientRequest(loginClientRequest);
		if (loginClientResponse.getStatus() == 302) {
			URI resetUri = UriBuilder.fromUri(
					"http://localhost:8888/admin/reset").build();
			ClientRequest resetClientRequest = adminClientRequest(resetUri,
					"POST", MediaType.APPLICATION_FORM_URLENCODED_TYPE);
			ClientResponse resetClientResponse = handleAdminClientRequest(resetClientRequest);
			if (resetClientResponse.getStatus() != 200) {
				throw new AssertionError("Resetting database failed while "
						+ "invoking reset method with status: "
						+ resetClientResponse.getStatus());
			}
		} else {
			throw new AssertionError("Resetting database failed while "
					+ "creating admin user with status: "
					+ loginClientResponse.getStatus());
		}
	}

	@Given("^a user in the database with session \"(.*)\" and email \"(.*)\"$")
	public void userInTheDatabaseWithSessionAndEmail(String sessionKey,
			String email) {
		fetchNewSessionId(sessionKey);

		User user = new User(new Email(email));
		List<NewCookie> sessionKeyNewCookies = sessionKeyToNewCookies
				.get(sessionKey);
		for (NewCookie newCookie : sessionKeyNewCookies) {
			user.getSessions().add(new Session(user, newCookie.getValue()));
		}

		pushUsers(new ArrayList<User>(Collections.singleton(user)));
	}

	private void pushUsers(List<User> users) {
		URI uri = UriBuilder.fromUri("http://localhost:8888/admin/pushUsers")
				.build();
		ClientRequest pushClientRequest = adminClientRequest(uri, "POST",
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		pushClientRequest.setEntity(users);
		ClientResponse clientResponse = handleAdminClientRequest(pushClientRequest);
		Assert.assertEquals(200, clientResponse.getStatus());
	}

	private void fetchNewSessionId(String sessionKey) {
		URI newSessionUri = UriBuilder.fromUri(
				"http://localhost:8888/rest/users/session/new").build();
		ClientRequest newSessionClientRequest = clientRequest(newSessionUri,
				"POST", sessionKey);
		ClientResponse newSessionClientResponse = handleClientRequest(
				newSessionClientRequest, sessionKey);
		Assert.assertEquals(200, newSessionClientResponse.getStatus());
	}

	@Given("^a requested session \"(.*)\" in the database for email \"(.*)\"$")
	public void requestedSessionInTheDatabaseForEmail(String sessionKey,
			String email) {
		fetchNewSessionId(sessionKey);

		String sessionId = sessionKeyToNewCookies.get(sessionKey).get(0)
				.getValue();

		User user = new User(new Email(email));
		user.getAddSessionRequests()
				.add(new AddSessionRequest(user, sessionId));

		pushUsers(new ArrayList<User>(Collections.singleton(user)));
	}

	@When("^I hit the enrollment form with email \"(.*)\" and session \"(.*)\"$")
	public void hitTheEnrollmentFormWithEmailAndSessionKey(String email,
			String sessionKey) {
		clientResponse = enrollUser(sessionKey, email);
	}

	@When("^I confirm session \"(.*)\"$")
	public void confirmSession(String sessionKey) {
		URI uri = UriBuilder.fromPath(
				"http://localhost:8888/rest/users/session/confirm").build();
		ClientRequest clientRequest = clientRequest(uri, "POST", sessionKey);
		String addSessionRequestEncodedKey = fetchAddSessionRequestEncodedKeyForSessionKey(sessionKey);
		Form form = new Form();
		form.add("key", addSessionRequestEncodedKey);
		clientRequest.setEntity(form);

		clientResponse = handleClientRequest(clientRequest, sessionKey);
	}

	@When("^I deny session \"(.*)\"$")
	public void denySession(String sessionKey) {
		URI uri = UriBuilder.fromPath(
				"http://localhost:8888/rest/users/session/deny").build();
		ClientRequest clientRequest = clientRequest(uri, "POST", sessionKey);
		String addSessionRequestEncodedKey = fetchAddSessionRequestEncodedKeyForSessionKey(sessionKey);
		Form form = new Form();
		form.add("key", addSessionRequestEncodedKey);
		clientRequest.setEntity(form);

		clientResponse = handleClientRequest(clientRequest, sessionKey);
	}

	@When("^I use session \"(.*)\" to add event \"(.*)\" starting on \"(.*)\" in time zone \"(.*)\"$")
	public void useSessionToAddEventNamedStartingOn(String sessionKey,
			String eventName, String startTime, String timeZoneId)
			throws ParseException {
		long startTimeMillis = new SimpleDateFormat("yyyy-MM-dd-HHmm").parse(
				startTime).getTime();

		URI uri = UriBuilder.fromPath("http://localhost:8888/rest/events/new")
				.build();
		ClientRequest clientRequest = clientRequest(uri, "POST", sessionKey);
		Form form = new Form();
		form.add("name", eventName);
		form.add("startTimeMillis", Long.toString(startTimeMillis));
		form.add("timeZoneId", timeZoneId);
		clientRequest.setEntity(form);

		clientResponse = handleClientRequest(clientRequest, sessionKey);
	}

	@Then("^no requested sessions should exist in the database for email \"(.*)\"$")
	public void noRequestedSessionsShouldExistInTheDatabaseForEmail(String email) {
		List<User> users = fetchUsers();
		User emailUser = null;
		for (User user : users) {
			if (email.equals(user.getEmail().getEmail())) {
				emailUser = user;
				break;
			}
		}

		if (emailUser == null) {
			throw new AssertionError("No user matches email: " + email);
		}

		Assert.assertEquals(Collections.emptySet(), emailUser
				.getAddSessionRequests());
	}

	@Then("^I should get a response with a status code of (\\d+)$")
	public void shouldGetAResponseWithAStatusCodeOf(int statusCode) {
		Assert.assertEquals(statusCode, clientResponse.getStatus());
	}

	@Then("^session \"(.*)\" should not be associated with email \"(.*)\"$")
	public void sessionKeyShouldNotBeAssociatedWithEmail(String sessionKey,
			String email) {
		List<User> users = fetchUsers();
		User emailUser = null;
		for (User user : users) {
			if (email.equals(user.getEmail().getEmail())) {
				emailUser = user;
				break;
			}
		}

		if (emailUser == null) {
			throw new AssertionError("A user does not exist with email: "
					+ email);
		}

		List<NewCookie> newCookies = sessionKeyToNewCookies.get(sessionKey);
		if (newCookies == null) {
			throw new AssertionError("No cookies exist for session: "
					+ sessionKey);
		}

		NewCookie sessionNewCookie = null;
		for (NewCookie newCookie : newCookies) {
			if (newCookie.getName().equals("JSESSIONID")) {
				sessionNewCookie = newCookie;
				break;
			}
		}

		if (sessionNewCookie == null) {
			throw new AssertionError("No session cookie exists for session: "
					+ sessionKey);
		}

		Session sessionKeySession = null;
		for (Session session : emailUser.getSessions()) {
			if (session.getSessionId().equals(sessionNewCookie.getValue())) {
				sessionKeySession = session;
				break;
			}
		}

		Assert.assertNull("SessionIds matched session " + sessionKey
				+ "'s value: " + sessionNewCookie.getValue()
				+ ".  User contains sessions: " + emailUser.getSessions(),
				sessionKeySession);
	}

	@Then("^session \"(.*)\" should be associated with email \"(.*)\"$")
	public void sessionKeyShouldBeAssociatedWithEmail(String sessionKey,
			String email) {
		List<User> users = fetchUsers();
		User emailUser = null;
		for (User user : users) {
			if (email.equals(user.getEmail().getEmail())) {
				emailUser = user;
				break;
			}
		}

		if (emailUser == null) {
			throw new AssertionError("A user does not exist with email: "
					+ email);
		}

		List<NewCookie> newCookies = sessionKeyToNewCookies.get(sessionKey);
		if (newCookies == null) {
			throw new AssertionError("No cookies exist for session: "
					+ sessionKey);
		}

		NewCookie sessionNewCookie = null;
		for (NewCookie newCookie : newCookies) {
			if (newCookie.getName().equals("JSESSIONID")) {
				sessionNewCookie = newCookie;
				break;
			}
		}

		if (sessionNewCookie == null) {
			throw new AssertionError("No session cookie exists for session: "
					+ sessionKey);
		}

		Session sessionKeySession = null;
		for (Session session : emailUser.getSessions()) {
			if (session.getSessionId().equals(sessionNewCookie.getValue())) {
				sessionKeySession = session;
				break;
			}
		}

		Assert.assertNotNull("No sessionIds matched session " + sessionKey
				+ "'s value: " + sessionNewCookie.getValue()
				+ ".  User contains sessions: " + emailUser.getSessions(),
				sessionKeySession);
	}

	@Then("^there should be no users in the database$")
	public void thereShouldBeNoUsersInTheDatabase() {
		List<User> users = fetchUsers();

		Assert.assertEquals(Collections.emptyList(), users);
	}

	@Then("^an email should be sent to \"(.*)\" with session \"(.*)\"$")
	public void anEmailShouldBeSentWithSession(String email, String sessionKey)
			throws Exception {
		File mailServiceLogFile = new File(
				"/tmp/com.appspot.saymoreofthat-LocalMailService.log");
		String mailServiceLog = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				mailServiceLogFile));
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader
				.readLine()) {
			mailServiceLog += line + "\n";
		}

		bufferedReader.close();

		Assert.assertTrue("Log is missing To. Log:\n" + mailServiceLog,
				mailServiceLog.contains("To: " + email));
		String addSessionRequestEncodedKey = fetchAddSessionRequestEncodedKeyForSessionKey(sessionKey);

		Assert.assertTrue("Log is missing session Key.  Log:\n"
				+ mailServiceLog, mailServiceLog
				.contains(addSessionRequestEncodedKey));
	}

	@Then("^the returned URI should point to an event named \"(.*)\" starting on \"(.*)\" in time zone \"(.*)\"$")
	public void theReturnedUriShouldPointToAnEventNamedStartingOn(String name,
			String startTime, String timeZoneId) throws ParseException {
		long startTimeMillis = new SimpleDateFormat("yyyy-MM-dd-HHmm").parse(
				startTime).getTime();
		long startTimeMillisUtc = startTimeMillis
				- TimeZone.getTimeZone(timeZoneId).getOffset(startTimeMillis);

		URI uri = clientResponse.getLocation();
		ClientRequest clientRequest = ClientRequest.create().build(uri, "GET");
		ClientResponse clientResponse = client.handle(clientRequest);
		EventResponse eventResponse = clientResponse.getEntity(EventResponse.class);
		
		Assert.assertEquals(name, eventResponse.name);
		Assert.assertEquals(startTimeMillisUtc, eventResponse.startTimeMillisUtc);
	}

	private String fetchAddSessionRequestEncodedKeyForSessionKey(
			String sessionKey) {
		List<User> users = fetchUsers();
		String addSessionRequestEncodedKey = null;
		String sessionId = sessionKeyToNewCookies.get(sessionKey).get(0)
				.getValue();
		for (User user : users) {
			for (AddSessionRequest addSessionRequest : user
					.getAddSessionRequests()) {
				if (sessionId.equals(addSessionRequest.getSessionId())) {
					addSessionRequestEncodedKey = KeyFactory
							.keyToString(addSessionRequest.getKey());
					break;
				}
			}
		}

		if (addSessionRequestEncodedKey == null) {
			throw new AssertionError(
					"No AddSessionRequest exists for sessionKey:" + sessionKey
							+ "(" + sessionId + ")");
		}

		return addSessionRequestEncodedKey;
	}

	private List<User> fetchUsers() {
		URI uri = UriBuilder.fromUri("http://localhost:8888/admin/dumpUsers")
				.build();
		ClientRequest clientRequest = adminClientRequest(uri, "GET",
				MediaType.TEXT_PLAIN_TYPE);
		ClientResponse clientResponse = handleAdminClientRequest(clientRequest);
		return clientResponse.getEntity(new GenericType<List<User>>() {
		});
	}

	private ClientResponse enrollUser(String sessionKey, String email) {
		Form form = new Form();
		form.add("email", email);
		URI uri = UriBuilder.fromUri("http://localhost:8888/rest/users/new")
				.build();
		ClientRequest clientRequest = clientRequest(uri, "POST", sessionKey);
		clientRequest.setEntity(form);
		return handleClientRequest(clientRequest, sessionKey);
	}

	private ClientRequest clientRequest(URI uri, String method,
			String sessionKey) {
		ClientRequest.Builder clientRequestBuilder = ClientRequest.create();
		List<NewCookie> newCookies = sessionKeyToNewCookies.get(sessionKey);
		if (newCookies != null) {
			for (NewCookie newCookie : newCookies) {
				clientRequestBuilder.cookie(newCookie.toCookie());
			}
		}
		return clientRequestBuilder.build(uri, method);
	}

	private ClientResponse handleClientRequest(ClientRequest clientRequest,
			String sessionKey) {
		ClientResponse clientResponse = client.handle(clientRequest);
		List<NewCookie> newCookies = sessionKeyToNewCookies.get(sessionKey);
		if (newCookies == null) {
			newCookies = new ArrayList<NewCookie>();
			sessionKeyToNewCookies.put(sessionKey, newCookies);
		}

		newCookies.addAll(clientResponse.getCookies());

		return clientResponse;
	}

	private ClientRequest adminClientRequest(URI uri, String method,
			MediaType mediaType) {
		ClientRequest.Builder clientRequestBuilder = ClientRequest.create()
				.type(mediaType);
		for (NewCookie newCookie : adminNewCookies) {
			clientRequestBuilder.cookie(newCookie.toCookie());
		}
		return clientRequestBuilder.build(uri, method);
	}

	private ClientResponse handleAdminClientRequest(ClientRequest clientRequest) {
		ClientResponse clientResponse = client.handle(clientRequest);
		adminNewCookies.addAll(clientResponse.getCookies());
		return clientResponse;
	}
}
