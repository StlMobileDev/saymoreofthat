package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.appengine.api.datastore.Email;

@XmlRootElement
@XmlType(propOrder={"email"})
public class LinkSessionAndEmailRequest {
	@XmlJavaTypeAdapter(EmailXmlAdapter.class)
	public Email email;
}
