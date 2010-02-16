package com.appspot.saymoreofthat.admin;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.appspot.saymoreofthat.rest.jdo.PMF;
import com.appspot.saymoreofthat.rest.jdo.User;

public class DumpUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManager persistenceManager = PMF.getPersistenceManager();
		try {
			resp.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(resp
					.getOutputStream());
			Query userQuery = persistenceManager.newQuery(User.class);
			userQuery.getFetchPlan().setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
			List<User> users = (List<User>) userQuery.execute();
			objectOutputStream.writeObject(new ArrayList<User>(users));
		} finally {
			persistenceManager.close();
		}
	}
}
