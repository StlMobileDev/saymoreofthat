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

import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Property that maps to the following schema fragment.
 *
 * <pre><xmp>
 * <xs:complexType>
 *   <xs:sequence>
 *     <xs:element name="entry" minOccurs="0" maxOccurs="unbounded">
 *       <xs:complexType>
 *         <xs:sequence>
 *           <xs:element name="key"   type="XXXX"/>
 *           <xs:element name="value" type="YYYY"/>
 *         </xs:sequence>
 *       </xs:complexType>
 *     </xs:element>
 *   </xs:sequence>
 * </xs:complexType>
 * </xmp></pre>
 *
 * <p>
 * This property is used to represent a default binding of a {@link Map} property.
 * ({@link Map} properties with adapters will be represented by {@link ElementPropertyInfo}.)
 *
 *
 * <h2>Design Thinking Led to This</h2>
 * <p>
 * I didn't like the idea of adding such a special-purpose {@link PropertyInfo} to a model.
 * The alternative was to implicitly assume an adapter, and have internal representation of
 * the Entry class ready.
 * But the fact that the key type and the value type changes with the parameterization makes
 * it very difficult to have such a class (especially inside APT, where we can't even generate
 * classes.)
 *
 * @author Kohsuke Kawaguchi
 */
public interface MapPropertyInfo<T,C> extends PropertyInfo<T,C> {
    /**
     * Gets the wrapper element name.
     *
     * @return
     *      always non-null.
     */
    QName getXmlName();

    /**
     * Returns true if this property is nillable
     * (meaning the absence of the value is treated as nil='true')
     *
     * <p>
     * This method is only used when this property is a collection.
     */
    boolean isCollectionNillable();

    /**
     * Type of the key of the map. K of {@code HashMap<K,V>}
     *
     * @return never null.
     */
    NonElement<T,C> getKeyType();

    /**
     * Type of the value of the map. V of {@code HashMap<K,V>}
     *
     * @return never null.
     */
    NonElement<T,C> getValueType();

    // TODO
    // Adapter<T,C> getKeyAdapter();
    // Adapter<T,C> getValueAdapter();
}
