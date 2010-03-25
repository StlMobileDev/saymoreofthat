
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("appinfo")
public interface Appinfo
    extends TypedXmlWriter
{


    @XmlAttribute
    public Appinfo source(String value);

}
