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
    public Key key;
	
	@Persistent
	@Element(dependent="true")
	public Set<String> sessionIds;
	
	@Persistent
	public Email email;
	
	@Persistent(mappedBy="user")
	@Element(dependent="true")
	public Set<Event> events;
	
	public User(Email email) {
		this.email = email;
		this.sessionIds = new HashSet<String>();
		this.events = new HashSet<Event>();
	}
}
