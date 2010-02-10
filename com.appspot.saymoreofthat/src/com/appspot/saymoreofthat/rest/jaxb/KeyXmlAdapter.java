package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class KeyXmlAdapter extends XmlAdapter<String, Key>{
	@Override
	public String marshal(Key key) throws Exception {
		return KeyFactory.keyToString(key);
	}
	
	@Override
	public Key unmarshal(String keyAsString) throws Exception {
		return KeyFactory.stringToKey(keyAsString);
	}
}
