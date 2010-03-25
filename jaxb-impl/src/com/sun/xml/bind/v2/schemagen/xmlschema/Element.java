
package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;

public interface Element
    extends Annotated, ComplexTypeHost, FixedOrDefault, SimpleTypeHost, TypedXmlWriter
{


    @XmlAttribute
    public Element type(QName value);

    @XmlAttribute
    public Element block(String[] value);

    @XmlAttribute
    public Element block(String value);

    @XmlAttribute
    public Element nillable(boolean value);

}
