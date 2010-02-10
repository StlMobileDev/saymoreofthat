@XmlJavaTypeAdapters(value={@XmlJavaTypeAdapter(KeyXmlAdapter.class), @XmlJavaTypeAdapter(EmailXmlAdapter.class)})
package com.appspot.saymoreofthat.rest.jaxb;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

