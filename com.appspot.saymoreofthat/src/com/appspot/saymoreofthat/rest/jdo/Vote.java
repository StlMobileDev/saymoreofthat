package com.appspot.saymoreofthat.rest.jdo;

import java.util.Calendar;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Vote {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Key key;

	@Persistent
	public Event event;
	
	@Persistent
	public Calendar calendar;
	
	@Persistent
	public Integer value;
	
	public Vote(Event event, Calendar calendar, Integer value) {
		super();
		this.event = event;
		this.calendar = calendar;
		this.value = value;
	}
}
