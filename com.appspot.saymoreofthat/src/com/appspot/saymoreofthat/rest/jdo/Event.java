package com.appspot.saymoreofthat.rest.jdo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Key key;
	
	@Persistent
	public User user;

	@Persistent
	public String name;
	
	@Persistent
	public Calendar startCalendar;
	
	@Persistent
	public Calendar endCalendar;
	
	@Persistent(mappedBy="event")
	@Element(dependent="true")
	public Set<Vote> votes;
	
	public Event(User user, String name, Calendar startCalendar) {
		super();
		this.user = user;
		this.name = name;
		this.startCalendar = startCalendar;
		this.votes = new HashSet<Vote>();
	}
}
