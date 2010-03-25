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

/**
 * {@link NonElement} that represents an {@link Enum} class.
 *
 * @author Kohsuke Kawaguchi
 */
public interface EnumLeafInfo<T,C> extends LeafInfo<T,C> {
    /**
     * The same as {@link #getType()} but an {@link EnumLeafInfo}
     * is guaranteed to represent an enum declaration, which is a
     * kind of a class declaration.
     *
     * @return
     *      always non-null.
     */
    C getClazz();

    /**
     * Returns the base type of the enumeration.
     *
     * <p>
     * For example, with the following enum class, this method
     * returns {@link BuiltinLeafInfo} for {@link Integer}.
     *
     * <pre>
     * &amp;XmlEnum(Integer.class)
     * enum Foo {
     *   &amp;XmlEnumValue("1")
     *   ONE,
     *   &amp;XmlEnumValue("2")
     *   TWO
     * }
     * </pre>
     *
     * @return
     *      never null.
     */
    NonElement<T,C> getBaseType();

    /**
     * Returns the read-only list of enumeration constants.
     *
     * @return
     *      never null. Can be empty (really?).
     */
    Iterable<? extends EnumConstant> getConstants();
}
