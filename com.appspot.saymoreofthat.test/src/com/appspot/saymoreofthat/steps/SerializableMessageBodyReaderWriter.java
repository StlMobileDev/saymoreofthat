package com.appspot.saymoreofthat.steps;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces( { "application/octet-stream" })
@Consumes( { "application/octet-stream" })
public class SerializableMessageBodyReaderWriter extends
		AbstractMessageReaderWriterProvider<Serializable> {

	private boolean isReadableOrWritable(Class<?> clazz, Type type,
			MediaType mediaType) {
		if (MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(mediaType)) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				if (parameterizedType.getRawType() instanceof Class<?>) {
					if (Iterable.class
							.isAssignableFrom((Class<?>) parameterizedType
									.getRawType())) {
						if (parameterizedType.getActualTypeArguments()[0] instanceof Class<?>) {
							return Serializable.class
									.isAssignableFrom((Class<?>) parameterizedType
											.getActualTypeArguments()[0]);
						}
					}
				}
			} else if (type instanceof Class<?>) {
				return Serializable.class.isAssignableFrom((Class<?>) type);
			}
		}

		return false;
	}

	public boolean isReadable(Class<?> clazz, Type type,
			Annotation annotations[], MediaType mediaType) {
		return isReadableOrWritable(clazz, type, mediaType);
	}

	public Serializable readFrom(Class<Serializable> type, Type genericType,
			Annotation annotations[], MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		ObjectInputStream objectInputStream = new ObjectInputStream(
				entityStream);
		try {
			return (Serializable) objectInputStream.readObject();
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RuntimeException(classNotFoundException);
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
			}
		}
	}

	public boolean isWriteable(Class<?> clazz, Type type,
			Annotation annotations[], MediaType mediaType) {
		return isReadableOrWritable(clazz, type, mediaType);
	}

	public void writeTo(Serializable serializable, Class<?> type,
			Type genericType, Annotation annotations[], MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				entityStream);
		try {
			objectOutputStream.writeObject(serializable);
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException ioException) {
			}
		}
	}
}
