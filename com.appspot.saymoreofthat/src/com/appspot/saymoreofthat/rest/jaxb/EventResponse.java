package com.appspot.saymoreofthat.rest.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "id", "name", "startTimeMillisUtc", "endTimeMillisUtc", "votes" })
public class EventResponse {
	public String id;
	public String name;
	public long startTimeMillisUtc;
	public long endTimeMillisUtc;
	
	public List<VoteResponse> votes;
}
