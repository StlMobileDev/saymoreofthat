package com.appspot.saymoreofthat.steps;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import junit.framework.Assert;

import com.appspot.saymoreofthat.rest.jdo.Session;
import com.appspot.saymoreofthat.rest.jdo.User;
import com.google.appengine.api.datastore.Email;
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

	@Given("^a user in the database with session \"(.*)\" and email \"(.*)\"$")
	public void userInTheDatabaseWithSessionAndEmail(String session,
			String email) {
		User user = new User(new Email(email));
		user.getSessions().add(new Session(user, session));
		URI uri = UriBuilder.fromPath("http://localhost:8888/admin/pushUsers")
				.build();
		ClientRequest clientRequest = adminClientRequest(uri, "POST",
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		clientRequest
				.setEntity(new ArrayList<User>(Collections.singleton(user)));
		ClientResponse clientResponse = handleAdminClientRequest(clientRequest);
		if (clientResponse.getStatus() != 200) {
			throw new AssertionError(
					"Pushing user resulted in unexpected status: "
							+ clientResponse.getStatus());
		}
	}

	@When("^I hit the enrollment form with \"(.*)\"$")
	public void hitTheEnrollmentFormWith(String email) {
		Form form = new Form();
		form.add("email", email);
		URI uri = UriBuilder.fromUri("http://localhost:8888/rest/users/new")
				.build();
		ClientRequest clientRequest = ClientRequest.create().entity(form)
				.build(uri, "POST");
		clientResponse = client.handle(clientRequest);
	}

	@Then("^I should get a response with a status code of (\\d+)$")
	public void shouldGetAResponseWithAStatusCodeOf(int statusCode) {
		Assert.assertEquals(statusCode, clientResponse.getStatus());
	}

	@Then("there should be 1 session in the database for email \"(.*)\"")
	public void thereShouldBeOneSessionInTheDatabaseForEmail(String email) {
		URI uri = UriBuilder.fromUri("http://localhost:8888/admin/dumpUsers")
				.build();
		ClientRequest clientRequest = adminClientRequest(uri, "GET",
				MediaType.TEXT_PLAIN_TYPE);
		ClientResponse clientResponse = handleAdminClientRequest(clientRequest);
		List<User> users = clientResponse
				.getEntity(new GenericType<List<User>>() {
				});
		for (User user : users) {
			if (email.equals(user.getEmail().getEmail())) {
				if (user.getSessions().size() == 1) {
					return;
				} else {
					throw new AssertionError("Expected 1 session, but found "
							+ user.getSessions().size());
				}
			}
		}

		throw new AssertionError("A user does not exist with email: " + email);
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
