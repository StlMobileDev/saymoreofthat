
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("complexType")
public interface ComplexType
    extends Annotated, ComplexTypeModel, TypedXmlWriter
{


    @XmlAttribute("final")
    public ComplexType _final(String[] value);

    @XmlAttribute("final")
    public ComplexType _final(String value);

    @XmlAttribute
    public ComplexType block(String[] value);

    @XmlAttribute
    public ComplexType block(String value);

    @XmlAttribute("abstract")
    public ComplexType _abstract(boolean value);

    @XmlAttribute
    public ComplexType name(String value);

}
