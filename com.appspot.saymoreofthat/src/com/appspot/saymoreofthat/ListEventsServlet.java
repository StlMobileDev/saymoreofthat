package com.appspot.saymoreofthat;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ListEventsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		PersistenceManager persistenceManager = PMF.get()
				.getPersistenceManager();
		try {
			Iterable<Event> eventsIterable = persistenceManager
					.getExtent(Event.class);
			for (Event event : eventsIterable) {
				resp.getWriter().println(
						event.getName() + " "
								+ KeyFactory.keyToString(event.getKey()));
			}
		} finally {
			persistenceManager.close();
		}
	}
}
