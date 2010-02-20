package com.appspot.saymoreofthat.steps;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import junit.framework.Assert;

import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
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

	@Given("^a client without a session$")
	public void clientWithoutASession() {
	}

	@Given("^a client with session \"(.*)\"$")
	public void clientWithSession(String sessionKey) {
		enrollUser(sessionKey, "");
	}

	@Given("^a user in the database with session \"(.*)\" and email \"(.*)\"$")
	public void userInTheDatabaseWithSessionAndEmail(String sessionKey,
			String email) {
		enrollUser(sessionKey, email);
	}

	@When("^I hit the enrollment form with email \"(.*)\" and session \"(.*)\"$")
	public void hitTheEnrollmentFormWithEmailAndSessionKey(String email,
			String sessionKey) {
		clientResponse = enrollUser(sessionKey, email);
	}

	@Then("^I should get a response with a status code of (\\d+)$")
	public void shouldGetAResponseWithAStatusCodeOf(int statusCode) {
		Assert.assertEquals(statusCode, clientResponse.getStatus());
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
