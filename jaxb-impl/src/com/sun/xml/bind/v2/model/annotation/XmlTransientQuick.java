
package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick
    extends Quick
    implements XmlTransient
{

    private final XmlTransient core;

    public XmlTransientQuick(Locatable upstream, XmlTransient core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlTransientQuick(upstream, ((XmlTransient) core));
    }

    public Class<XmlTransient> annotationType() {
        return XmlTransient.class;
    }

}
