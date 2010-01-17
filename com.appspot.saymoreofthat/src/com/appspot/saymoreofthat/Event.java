package com.appspot.saymoreofthat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Event {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
    private User ownerUser;

	@Persistent
    private String name;

    @Persistent
    private Date startDate;

    @Persistent
    private Date endDate;
    
    @Persistent
    private Set<Vote> votes;

	public Event(User ownerUser, String name, Date startDate) {
		super();
		this.ownerUser = ownerUser;
		this.name = name;
		this.startDate = startDate;
		this.votes = new HashSet<Vote>();
	}
	
	public void end() {
		endDate = new Date();
	}

	public String getName() {
		return name;
	}

	public Key getKey() {
		return key;
	}

	public User getOwnerUser() {
		return ownerUser;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public Set<Vote> getVotes() {
		return votes;
	}
}
