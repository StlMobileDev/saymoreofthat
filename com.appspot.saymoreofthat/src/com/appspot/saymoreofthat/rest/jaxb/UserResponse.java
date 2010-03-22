package com.appspot.saymoreofthat.rest.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.appengine.api.datastore.Email;

@XmlRootElement
@XmlType(propOrder={"email", "sessions"})
public class UserResponse {
	public Email email;
	
	@XmlElement(name="sessions")
	public List<String> sessions;
}
