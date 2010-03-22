package com.appspot.saymoreofthat.rest.jdo;

import java.io.Serializable;
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
    private Key key;
	
	@Persistent
	private User user;

	@Persistent
	private String name;
	
	@Persistent
	private long startTimeMillisUtc;
	
	@Persistent
	private long endTimeMillisUtc;
	
	@Persistent(mappedBy="event")
	@Element(dependent="true")
	private Set<Vote> votes;
	
	public Event(User user, String name, long startTimeMillisUtc) {
		super();
		this.user = user;
		this.name = name;
		this.startTimeMillisUtc = startTimeMillisUtc;
		this.votes = new HashSet<Vote>();
	}

	public Key getKey() {
		return key;
	}

	public User getUser() {
		return user;
	}

	public String getName() {
		return name;
	}

	public long getStartTimeMillisUtc() {
		return startTimeMillisUtc;
	}

	public Set<Vote> getVotes() {
		return votes;
	}
	
	public long getEndTimeMillisUtc() {
		return endTimeMillisUtc;
	}

	public void setEndTimeMillisGmt(long endTimeMillisGmt) {
		this.endTimeMillisUtc = endTimeMillisGmt;
	}
}
