
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;

public interface SchemaTop
    extends Redefinable, TypedXmlWriter
{


    @XmlElement
    public TopLevelAttribute attribute();

    @XmlElement
    public TopLevelElement element();

}
