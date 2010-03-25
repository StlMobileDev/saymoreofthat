
package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("element")
public interface TopLevelElement
    extends Element, TypedXmlWriter
{


    @XmlAttribute("final")
    public TopLevelElement _final(String[] value);

    @XmlAttribute("final")
    public TopLevelElement _final(String value);

    @XmlAttribute("abstract")
    public TopLevelElement _abstract(boolean value);

    @XmlAttribute
    public TopLevelElement substitutionGroup(QName value);

    @XmlAttribute
    public TopLevelElement name(String value);

}
