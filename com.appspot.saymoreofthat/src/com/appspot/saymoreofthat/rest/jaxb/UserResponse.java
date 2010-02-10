package com.appspot.saymoreofthat.rest.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;

@XmlRootElement
@XmlType(propOrder={"key", "email", "sessions"})
public class UserResponse {
	public Key key;
	public Email email;
	
	@XmlElementWrapper(name="sessions")
	@XmlElement(name="session")
	public List<String> sessions;
}
