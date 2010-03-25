
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("attribute")
public interface TopLevelAttribute
    extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{


    @XmlAttribute
    public TopLevelAttribute name(String value);

}
