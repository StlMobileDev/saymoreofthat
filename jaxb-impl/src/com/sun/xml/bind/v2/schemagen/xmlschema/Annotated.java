
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

public interface Annotated
    extends TypedXmlWriter
{


    @XmlElement
    public Annotation annotation();

    @XmlAttribute
    public Annotated id(String value);

}
