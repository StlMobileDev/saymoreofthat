
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("import")
public interface Import
    extends Annotated, TypedXmlWriter
{


    @XmlAttribute
    public Import namespace(String value);

    @XmlAttribute
    public Import schemaLocation(String value);

}
