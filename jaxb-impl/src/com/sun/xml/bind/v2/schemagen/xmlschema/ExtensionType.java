
package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;

public interface ExtensionType
    extends Annotated, TypedXmlWriter
{


    @XmlAttribute
    public ExtensionType base(QName value);

}
