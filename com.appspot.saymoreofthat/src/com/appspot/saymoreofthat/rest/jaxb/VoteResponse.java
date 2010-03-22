package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "value", "timeMillisUtc" })
public class VoteResponse {
	public int value;
	public long timeMillisUtc;
}
