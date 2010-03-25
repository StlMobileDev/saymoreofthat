/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.bind.v2.model.impl;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.core.EnumConstant;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.Element;
import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.runtime.Location;

/**
 * {@link EnumLeafInfo} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
class EnumLeafInfoImpl<T,C,F,M> extends TypeInfoImpl<T,C,F,M>
        implements EnumLeafInfo<T,C>, Element<T,C>, Iterable<EnumConstantImpl<T,C,F,M>> {

    /**
     * The enum class whose information this object represents.
     */
    /*package*/ final C clazz;

    NonElement<T,C> baseType;

    private final T type;

    /**
     * Can be null for anonymous types.
     */
    private final QName typeName;

    /**
     * All the {@link EnumConstantImpl}s are linked in this list.
     */
    private EnumConstantImpl<T,C,F,M> firstConstant;

    /**
     * If this enum is also bound to an element, that tag name.
     * Or else null.
     */
    private QName elementName;

    /**
     * @param clazz
     * @param type
     *      clazz and type should both point to the enum class
     *      that this {@link EnumLeafInfo} represents.
     *      Because of the type parameterization we have to take them separately.
     */
    public EnumLeafInfoImpl(ModelBuilder<T,C,F,M> builder,
                            Locatable upstream, C clazz, T type ) {
        super(builder,upstream);
        this.clazz = clazz;
        this.type = type;

        elementName = parseElementName(clazz);

        // compute the type name
        // TODO: I guess it must be allowed for enums to have @XmlElement
        typeName = parseTypeName(clazz);

        // locate the base type.
        // this can be done eagerly because there shouldn't be no cycle.
        XmlEnum xe = builder.reader.getClassAnnotation(XmlEnum.class, clazz, this);
        if(xe!=null) {
            T base = builder.reader.getClassValue(xe, "value");
            baseType = builder.getTypeInfo(base,this);
        } else {
            baseType = builder.getTypeInfo(builder.nav.ref(String.class),this);
        }
    }

    /**
     * Build {@link EnumConstant}s and discover/report any error in it.
     */
    protected void calcConstants() {
        EnumConstantImpl<T,C,F,M> last = null;
        F[] constants = nav().getEnumConstants(clazz);
        for( int i=constants.length-1; i>=0; i-- ) {
            F constant = constants[i];
            String name = nav().getFieldName(constant);
            XmlEnumValue xev = builder.reader.getFieldAnnotation(XmlEnumValue.class, constant, this);

            String literal;
            if(xev==null)   literal = name;
            else            literal = xev.value();

            last = createEnumConstant(name,literal,constant,last);
        }
        this.firstConstant = last;
    }

    protected EnumConstantImpl<T,C,F,M> createEnumConstant(String name, String literal, F constant, EnumConstantImpl<T,C,F,M> last) {
        return new EnumConstantImpl<T,C,F,M>(this, name, literal, last);
    }


    public T getType() {
        return type;
    }

    /**
     * Leaf-type cannot be referenced from IDREF.
     *
     * @deprecated
     *      why are you calling a method whose return value is always known?
     */
    public final boolean canBeReferencedByIDREF() {
        return false;
    }

    public QName getTypeName() {
        return typeName;
    }

    public C getClazz() {
        return clazz;
    }

    public NonElement<T,C> getBaseType() {
        return baseType;
    }

    public boolean isSimpleType() {
        return true;
    }

    public Location getLocation() {
        return nav().getClassLocation(clazz);
    }

    public Iterable<? extends EnumConstantImpl<T,C,F,M>> getConstants() {
        if(firstConstant==null)
            calcConstants();
        return this;
    }

    public void link() {
        // make sure we've computed constants
        getConstants();
        super.link();
    }

    /**
     * No substitution.
     *
     * @deprecated if you are invoking this method directly, there's something wrong.
     */
    public Element<T, C> getSubstitutionHead() {
        return null;
    }

    public QName getElementName() {
        return elementName;
    }

    public boolean isElement() {
        return elementName!=null;
    }

    public Element<T,C> asElement() {
        if(isElement())
            return this;
        else
            return null;
    }

    /**
     * When a bean binds to an element, it's always through {@link XmlRootElement},
     * so this method always return null.
     *
     * @deprecated
     *      you shouldn't be invoking this method on {@link ClassInfoImpl}.
     */
    public ClassInfo<T,C> getScope() {
        return null;
    }

    public Iterator<EnumConstantImpl<T,C,F,M>> iterator() {
        return new Iterator<EnumConstantImpl<T,C,F,M>>() {
            private EnumConstantImpl<T,C,F,M> next = firstConstant;
            public boolean hasNext() {
                return next!=null;
            }

            public EnumConstantImpl<T,C,F,M> next() {
                EnumConstantImpl<T,C,F,M> r = next;
                next = next.next;
                return r;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
