package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"key"})
public class AddSessionRequestKey {
	@XmlElement(required=true)
	public String key;
}
