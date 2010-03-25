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

import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.TODO;
import com.sun.xml.bind.v2.WellKnownNamespace;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.core.ArrayInfo;
import com.sun.xml.bind.v2.model.core.NonElement;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;

/**
 *
 * <p>
 * Public because XJC needs to access it
 *
 * @author Kohsuke Kawaguchi
 */
public class ArrayInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    extends TypeInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    implements ArrayInfo<TypeT,ClassDeclT>, Location {

    private final NonElement<TypeT,ClassDeclT> itemType;

    private final QName typeName;

    /**
     * The representation of T[] in the underlying reflection library.
     */
    private final TypeT arrayType;

    public ArrayInfoImpl(ModelBuilder<TypeT,ClassDeclT,FieldT,MethodT> builder,
                         Locatable upstream, TypeT arrayType) {
        super(builder, upstream);
        this.arrayType = arrayType;
        TypeT componentType = nav().getComponentType(arrayType);
        this.itemType = builder.getTypeInfo(componentType, this);

        QName n = itemType.getTypeName();
        if(n==null) {
            builder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(
                nav().getTypeName(componentType)),this));
            n = new QName("#dummy"); // for error recovery
        }
        this.typeName = calcArrayTypeName(n);
    }

    /**
     * Computes the type name of the array from that of the item type.
     */
    public static QName calcArrayTypeName(QName n) {
        String uri;
        if(n.getNamespaceURI().equals(WellKnownNamespace.XML_SCHEMA)) {
            TODO.checkSpec("this URI");
            uri = "http://jaxb.dev.java.net/array";
        } else
            uri = n.getNamespaceURI();
        return new QName(uri,n.getLocalPart()+"Array");
    }

    public NonElement<TypeT, ClassDeclT> getItemType() {
        return itemType;
    }

    public QName getTypeName() {
        return typeName;
    }

    public boolean isSimpleType() {
        return false;
    }

    public TypeT getType() {
        return arrayType;
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

    public Location getLocation() {
        return this;
    }
    public String toString() {
        return nav().getTypeName(arrayType);
    }
}