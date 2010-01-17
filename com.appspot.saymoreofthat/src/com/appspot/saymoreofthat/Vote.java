package com.appspot.saymoreofthat;

import java.util.Date;

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
    private Key key;
	
	@Persistent
	private int value;
	
	@Persistent
	private Date timestampDate;

	public Vote(int value) {
		this.value = value;
		this.timestampDate = new Date();
	}
	
	public int getValue() {
		return value;
	}
	
	public Date getTimestampDate() {
		return timestampDate;
	}
}
