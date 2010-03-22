package com.appspot.saymoreofthat.rest.jdo;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Vote implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Event event;

	@Persistent
	private long timeMillisUtc;

	@Persistent
	private int value;

	public Vote(Event event, long timeMillisUtc, int value) {
		super();
		this.event = event;
		this.timeMillisUtc = timeMillisUtc;
		this.value = value;
	}

	public Key getKey() {
		return key;
	}

	public Event getEvent() {
		return event;
	}

	public long getTimeMillisUtc() {
		return timeMillisUtc;
	}

	public int getValue() {
		return value;
	}
}
