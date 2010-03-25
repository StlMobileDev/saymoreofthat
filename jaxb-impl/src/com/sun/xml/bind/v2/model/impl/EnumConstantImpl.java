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

import com.sun.xml.bind.v2.model.core.EnumConstant;
import com.sun.xml.bind.v2.model.core.EnumLeafInfo;

/**
 * @author Kohsuke Kawaguchi
 */
class EnumConstantImpl<T,C,F,M> implements EnumConstant<T,C> {
    protected final String lexical;
    protected final EnumLeafInfoImpl<T,C,F,M> owner;
    protected final String name;

    /**
     * All the constants of the {@link EnumConstantImpl} is linked in one list.
     */
    protected final EnumConstantImpl<T,C,F,M> next;

    public EnumConstantImpl(EnumLeafInfoImpl<T,C,F,M> owner, String name, String lexical, EnumConstantImpl<T,C,F,M> next) {
        this.lexical = lexical;
        this.owner = owner;
        this.name = name;
        this.next = next;
    }

    public EnumLeafInfo<T,C> getEnclosingClass() {
        return owner;
    }

    public final String getLexicalValue() {
        return lexical;
    }

    public final String getName() {
        return name;
    }
}
