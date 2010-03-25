
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("documentation")
public interface Documentation
    extends TypedXmlWriter
{


    @XmlAttribute
    public Documentation source(String value);

    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    public Documentation lang(String value);

}
