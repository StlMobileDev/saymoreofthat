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

import java.util.Collection;

import javax.xml.bind.JAXBElement;

/**
 * A particular use (specialization) of {@link JAXBElement}.
 *
 * TODO: is ElementInfo adaptable?
 *
 * @author Kohsuke Kawaguchi
 */
public interface ElementInfo<T,C> extends Element<T,C> {

    /**
     * Gets the object that represents the value property.
     *
     * @return
     *      non-null.
     */
    ElementPropertyInfo<T,C> getProperty();

    /**
     * Short for <code>getProperty().ref().get(0)</code>.
     *
     * The type of the value this element holds.
     *
     * Normally, this is the T of {@code JAXBElement<T>}.
     * But if the property is adapted, this is the on-the-wire type.
     *
     * Or if the element has a list of values, then this field
     * represents the type of the individual item.
     *
     * @see #getContentInMemoryType()
     */
    NonElement<T,C> getContentType();

    /**
     * T of {@code JAXBElement<T>}.
     *
     * <p>
     * This is tied to the in-memory representation.
     *
     * @see #getContentType()
     */
    T getContentInMemoryType();

    /**
     * Returns the representation for {@link JAXBElement}&lt;<i>contentInMemoryType</i>&gt;.
     *
     * <p>
     * This returns the signature in Java and thus isn't affected by the adapter.
     */
    T getType();

    /**
     * @inheritDoc
     *
     * {@link ElementInfo} can only substitute {@link ElementInfo}. 
     */
    ElementInfo<T,C> getSubstitutionHead();

    /**
     * All the {@link ElementInfo}s whose {@link #getSubstitutionHead()} points
     * to this object.
     *
     * @return
     *      can be empty but never null.
     */
    Collection<? extends ElementInfo<T,C>> getSubstitutionMembers();
}
