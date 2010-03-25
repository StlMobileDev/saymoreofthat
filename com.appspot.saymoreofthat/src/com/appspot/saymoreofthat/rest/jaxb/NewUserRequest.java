package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"email"})
public class NewUserRequest {
	@XmlElement(required=true)
	public String email;
}
