package com.appspot.saymoreofthat.steps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import junit.framework.Assert;

import com.appspot.saymoreofthat.rest.jdo.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
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
		client = Client.create();
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
					"POST");
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
		ClientRequest clientRequest = adminClientRequest(uri, "GET");
		ClientResponse clientResponse = handleAdminClientRequest(clientRequest);
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(clientResponse
					.getEntityInputStream());
			List<User> users = (List<User>) objectInputStream.readObject();
			for (User user : users) {
				if (email.equals(user.getEmail().getEmail())) {
					if (user.getSessions().size() == 1) {
						return;
					} else {
						throw new AssertionError(
								"Expected 1 session, but found "
										+ user.getSessions().size());
					}
				}
			}

			throw new AssertionError("A user does not exist with email: "
					+ email);
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException ioException) {
				}
			}
		}
	}

	private ClientRequest adminClientRequest(URI uri, String method) {
		ClientRequest.Builder clientRequestBuilder = ClientRequest
				.create();
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
