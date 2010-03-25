
package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("attribute")
public interface LocalAttribute
    extends Annotated, AttributeType, FixedOrDefault, TypedXmlWriter
{


    @XmlAttribute
    public LocalAttribute form(String value);

    @XmlAttribute
    public LocalAttribute name(String value);

    @XmlAttribute
    public LocalAttribute ref(QName value);

    @XmlAttribute
    public LocalAttribute use(String value);

}
