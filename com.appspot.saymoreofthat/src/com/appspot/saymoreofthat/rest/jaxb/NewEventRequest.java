package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"name", "startTimeMillisUtc"})
public class NewEventRequest {
	@XmlElement(required=true)
	public String name;
	
	@XmlElement(required=true)
	public long startTimeMillisUtc;
}
