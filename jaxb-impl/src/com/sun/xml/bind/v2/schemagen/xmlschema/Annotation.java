
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("annotation")
public interface Annotation
    extends TypedXmlWriter
{


    @XmlElement
    public Appinfo appinfo();

    @XmlElement
    public Documentation documentation();

    @XmlAttribute
    public Annotation id(String value);

}
