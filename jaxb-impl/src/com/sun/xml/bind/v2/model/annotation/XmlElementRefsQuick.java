
package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

final class XmlElementRefsQuick
    extends Quick
    implements XmlElementRefs
{

    private final XmlElementRefs core;

    public XmlElementRefsQuick(Locatable upstream, XmlElementRefs core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementRefsQuick(upstream, ((XmlElementRefs) core));
    }

    public Class<XmlElementRefs> annotationType() {
        return XmlElementRefs.class;
    }

    public XmlElementRef[] value() {
        return core.value();
    }

}
