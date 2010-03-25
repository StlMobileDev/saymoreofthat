
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("schema")
public interface Schema
    extends SchemaTop, TypedXmlWriter
{


    @XmlElement
    public Annotation annotation();

    @XmlElement("import")
    public Import _import();

    @XmlAttribute
    public Schema targetNamespace(String value);

    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    public Schema lang(String value);

    @XmlAttribute
    public Schema id(String value);

    @XmlAttribute
    public Schema elementFormDefault(String value);

    @XmlAttribute
    public Schema attributeFormDefault(String value);

    @XmlAttribute
    public Schema blockDefault(String[] value);

    @XmlAttribute
    public Schema blockDefault(String value);

    @XmlAttribute
    public Schema finalDefault(String[] value);

    @XmlAttribute
    public Schema finalDefault(String value);

    @XmlAttribute
    public Schema version(String value);

}
