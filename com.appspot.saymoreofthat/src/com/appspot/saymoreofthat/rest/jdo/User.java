package com.appspot.saymoreofthat.rest.jdo;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent(mappedBy="user")
	@Element(dependent="true")
	private Set<Session> sessions;
	
	@Persistent
	private Email email;
	
	@Persistent(mappedBy="user")
	@Element(dependent="true")
	private Set<Event> events;
	
	public User(Email email) {
		this.email = email;
		this.sessions = new HashSet<Session>();
		this.events = new HashSet<Event>();
	}
	
	public Email getEmail() {
		return email;
	}
	
	public Set<Event> getEvents() {
		return events;
	}
	
	public Set<Session> getSessions() {
		return sessions;
	}
	
	public Key getKey() {
		return key;
	}
}
