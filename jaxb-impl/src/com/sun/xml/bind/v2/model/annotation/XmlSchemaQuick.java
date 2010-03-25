
package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

final class XmlSchemaQuick
    extends Quick
    implements XmlSchema
{

    private final XmlSchema core;

    public XmlSchemaQuick(Locatable upstream, XmlSchema core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlSchemaQuick(upstream, ((XmlSchema) core));
    }

    public Class<XmlSchema> annotationType() {
        return XmlSchema.class;
    }

    public String location() {
        return core.location();
    }

    public String namespace() {
        return core.namespace();
    }

    public XmlNs[] xmlns() {
        return core.xmlns();
    }

    public XmlNsForm elementFormDefault() {
        return core.elementFormDefault();
    }

    public XmlNsForm attributeFormDefault() {
        return core.attributeFormDefault();
    }

}
