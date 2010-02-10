package com.appspot.saymoreofthat.rest.jaxb;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"name", "startCalendar"})
public class NewEventRequest {
	@XmlElement(required=true)
	public String name;
	
	@XmlElement(required=true)
	public Calendar startCalendar;
}
