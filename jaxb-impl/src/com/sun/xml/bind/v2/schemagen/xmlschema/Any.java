
package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement("any")
public interface Any
    extends Occurs, Wildcard, TypedXmlWriter
{


}
