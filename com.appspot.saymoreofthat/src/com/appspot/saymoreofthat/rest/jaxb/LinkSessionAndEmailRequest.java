package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.appengine.api.datastore.Email;

@XmlRootElement
@XmlType(propOrder={"email"})
public class LinkSessionAndEmailRequest {
	public Email email;
}
