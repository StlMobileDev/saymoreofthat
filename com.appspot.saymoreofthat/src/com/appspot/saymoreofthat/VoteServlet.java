package com.appspot.saymoreofthat;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class VoteServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			Key key = KeyFactory.stringToKey(req.getParameter("key"));
			Event event = persistenceManager.getObjectById(Event.class, key);
			Vote vote = new Vote(Integer.parseInt(req
					.getParameter("value")));
			event.getVotes().add(vote);
			persistenceManager.makePersistentAll(event, vote);
			resp.getWriter().println("Vote cast");
		} finally {
			persistenceManager.close();
		}
	}
}
