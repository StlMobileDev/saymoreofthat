
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;

public interface NestedParticle
    extends TypedXmlWriter
{


    @XmlElement
    public LocalElement element();

    @XmlElement
    public Any any();

    @XmlElement
    public ExplicitGroup sequence();

    @XmlElement
    public ExplicitGroup choice();

}
