
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;

public interface SimpleTypeHost
    extends TypeHost, TypedXmlWriter
{


    @XmlElement
    public SimpleType simpleType();

}
