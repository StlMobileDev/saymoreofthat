package com.appspot.saymoreofthat.rest.jdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Session {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private User user;
	
	@Persistent
	private String sessionId;
	
	public Session(User user, String sessionId) {
		this.user = user;
		this.sessionId = sessionId;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public Key getKey() {
		return key;
	}
}
