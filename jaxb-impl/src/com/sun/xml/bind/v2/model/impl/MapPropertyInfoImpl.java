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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.model.core.MapPropertyInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.core.TypeInfo;

/**
 * @author Kohsuke Kawaguchi
 */
class MapPropertyInfoImpl<T,C,F,M> extends PropertyInfoImpl<T,C,F,M> implements MapPropertyInfo<T,C> {

    private final QName xmlName;
    private boolean nil;
    private final T keyType;
    private final T valueType;

    // laziy computed to handle cyclic references
    private NonElement<T,C> keyTypeInfo;
    private NonElement<T,C> valueTypeInfo;


    public MapPropertyInfoImpl(ClassInfoImpl<T,C,F,M> ci, PropertySeed<T,C,F,M> seed) {
        super(ci, seed);

        XmlElementWrapper xe = seed.readAnnotation(XmlElementWrapper.class);
        xmlName = calcXmlName(xe);
        nil = xe!=null && xe.nillable();

        T raw = getRawType();
        T bt = nav().getBaseClass(raw, nav().asDecl(Map.class) );
        assert bt!=null;    // Map property is only for Maps

        if(nav().isParameterizedType(bt)) {
            keyType = nav().getTypeArgument(bt,0);
            valueType = nav().getTypeArgument(bt,1);
        } else {
            keyType = valueType = nav().ref(Object.class);
        }
    }

    public Collection<? extends TypeInfo<T,C>> ref() {
        return Arrays.asList(getKeyType(),getValueType());
    }

    public final PropertyKind kind() {
        return PropertyKind.MAP;
    }

    public QName getXmlName() {
        return xmlName;
    }

    public boolean isCollectionNillable() {
        return nil;
    }

    public NonElement<T,C> getKeyType() {
        if(keyTypeInfo==null)
            keyTypeInfo = getTarget(keyType);
        return keyTypeInfo;
    }

    public NonElement<T,C> getValueType() {
        if(valueTypeInfo==null)
            valueTypeInfo = getTarget(valueType);
        return valueTypeInfo;
    }

    public NonElement<T,C> getTarget(T type) {
        assert parent.builder!=null : "this method must be called during the build stage";
        return parent.builder.getTypeInfo(type,this);
    }
}
