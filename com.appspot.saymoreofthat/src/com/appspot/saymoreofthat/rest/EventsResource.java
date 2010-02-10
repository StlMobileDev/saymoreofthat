package com.appspot.saymoreofthat.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.appspot.saymoreofthat.rest.jdo.Event;

@Path("/events")
public class EventsResource {

//	@Path("/new")
//	@POST
//	@Consumes(value = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//	@Produces(MediaType.APPLICATION_JSON)
//	public Event newEvent(NewEventRequest newEventRequest,
//			@Context HttpServletRequest httpServletRequest) {
//		User user = 
//		Event event = new Event();
//		
//		return event;
//	}

	@Path("/list")
	@GET
	@Produces("test/plain")
	public List<Event> list() {
		return Collections.emptyList();
	}
}
