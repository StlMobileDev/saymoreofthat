package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.appengine.api.datastore.Email;

public class EmailXmlAdapter extends XmlAdapter<String, Email> {

	@Override
	public String marshal(Email email) throws Exception {
		return email.getEmail();
	}

	@Override
	public Email unmarshal(String emailValue) throws Exception {
		return new Email(emailValue);
	}

}
