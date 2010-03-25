
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;

public interface NoFixedFacet
    extends Annotated, TypedXmlWriter
{


    @XmlAttribute
    public NoFixedFacet value(String value);

}
