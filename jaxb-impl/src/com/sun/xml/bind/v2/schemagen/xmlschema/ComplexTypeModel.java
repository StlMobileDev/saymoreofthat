
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

public interface ComplexTypeModel
    extends AttrDecls, TypeDefParticle, TypedXmlWriter
{


    @XmlElement
    public SimpleContent simpleContent();

    @XmlElement
    public ComplexContent complexContent();

    @XmlAttribute
    public ComplexTypeModel mixed(boolean value);

}
