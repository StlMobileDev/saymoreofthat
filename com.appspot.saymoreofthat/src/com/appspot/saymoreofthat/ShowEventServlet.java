package com.appspot.saymoreofthat;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ShowEventServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			Key key = KeyFactory.stringToKey(req.getParameter("key"));
			Event event = persistenceManager.getObjectById(Event.class, key);
			resp.getWriter().println("Name: " + event.getName());
			resp.getWriter().println(
					"Owner: " + event.getOwnerUser().getNickname());
			resp.getWriter().println("StartDate: " + event.getStartDate());
			resp.getWriter().println("----");
			for (Vote vote : event.getVotes()) {
				resp.getWriter().println(
						vote.getTimestampDate() + " " + vote.getValue());
			}
			resp.getWriter().println("----");
		} finally {
			persistenceManager.close();
		}
	}
}
