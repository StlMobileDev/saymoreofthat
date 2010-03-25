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

package com.sun.xml.bind.v2.model.core;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Property that maps to an element.
 *
 * @author Kohsuke Kawaguchi
 */
// TODO: there seems to be too much interactions between switches, and that's no good.
public interface ElementPropertyInfo<T,C> extends PropertyInfo<T,C> {
    /**
     * Returns the information about the types allowed in this property.
     *
     * <p>
     * In a simple case like the following, an element property only has
     * one {@link TypeRef} that points to {@link String} and tag name "foo".
     * <pre>
     * &#64;XmlElement
     * String abc;
     * </pre>
     *
     * <p>
     * However, in a general case an element property can be heterogeneous,
     * meaning you can put different types in it, each with a different tag name
     * (and a few other settings.)
     * <pre>
     * // list can contain String or Integer.
     * &#64;XmlElements({
     *   &#64;XmlElement(name="a",type=String.class),
     *   &#64;XmlElement(name="b",type=Integer.class),
     * })
     * List&lt;Object> abc;
     * </pre>
     * <p>
     * In this case this method returns a list of two {@link TypeRef}s.
     *
     *
     * @return
     *      Always non-null. Contains at least one entry.
     *      If {@link #isValueList()}==true, there's always exactly one type.
     */
    List<? extends TypeRef<T,C>> getTypes();

    /**
     * Gets the wrapper element name.
     *
     * @return
     *      must be null if {@link #isCollection()}==false or
     *      if {@link #isValueList()}==true.
     *
     *      Otherwise,
     *      this can be null (in which case there'll be no wrapper),
     *      or it can be non-null (in which case there'll be a wrapper)
     */
    QName getXmlName();

    /**
     * Checks if the wrapper element is required.
     *
     * @return
     *      Always false if {@link #getXmlName()}==null.
     */
    boolean isCollectionRequired();

    /**
     * Returns true if this property is nillable
     * (meaning the absence of the value is treated as nil='true')
     *
     * <p>
     * This method is only used when this property is a collection.
     */
    boolean isCollectionNillable();

    /**
     * Returns true if this property is a collection but its XML
     * representation is a list of values, not repeated elements.
     *
     * <p>
     * If {@link #isCollection()}==false, this property is always false.
     *
     * <p>
     * When this flag is true, <tt>getTypes().size()==1</tt> always holds.
     */
    boolean isValueList();

    /**
     * Returns true if this element is mandatory.
     *
     * For collections, this property isn't used.
     * TODO: define the semantics when this is a collection
     */
    boolean isRequired();

    Adapter<T,C> getAdapter();
}
