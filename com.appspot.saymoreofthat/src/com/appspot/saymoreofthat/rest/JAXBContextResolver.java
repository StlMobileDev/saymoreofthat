package com.appspot.saymoreofthat.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.appspot.saymoreofthat.rest.jaxb.UserResponse;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	private final Set<Class<?>> types;
	private final JAXBContext jaxbContext;

	public JAXBContextResolver() throws JAXBException {
		Class<?>[] typesArray = new Class<?>[] { UserResponse.class };
		this.types = new HashSet<Class<?>>(Arrays.asList(typesArray));
		this.jaxbContext = new JSONJAXBContext(JSONConfiguration.natural()
				.build(), typesArray);
	}

	@Override
	public JAXBContext getContext(Class<?> type) {
		if (types.contains(type)) {
			return jaxbContext;
		}
		
		return null;
	}
}
