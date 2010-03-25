
package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick
    extends Quick
    implements XmlValue
{

    private final XmlValue core;

    public XmlValueQuick(Locatable upstream, XmlValue core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlValueQuick(upstream, ((XmlValue) core));
    }

    public Class<XmlValue> annotationType() {
        return XmlValue.class;
    }

}
