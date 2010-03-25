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

package com.sun.xml.bind.v2.runtime.reflect;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.JAXBException;

import com.sun.xml.bind.v2.runtime.Coordinator;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.api.AccessorException;

import org.xml.sax.SAXException;

/**
 * {@link Lister} that adapts individual item types.
 */
final class AdaptedLister<BeanT,PropT,InMemItemT,OnWireItemT,PackT> extends Lister<BeanT,PropT,OnWireItemT,PackT> {
    private final Lister<BeanT,PropT,InMemItemT,PackT> core;
    private final Class<? extends XmlAdapter<OnWireItemT,InMemItemT>> adapter;

    /*package*/ AdaptedLister(
        Lister<BeanT,PropT,InMemItemT,PackT> core,
        Class<? extends XmlAdapter<OnWireItemT,InMemItemT>> adapter) {

        this.core = core;
        this.adapter = adapter;
    }

    private XmlAdapter<OnWireItemT,InMemItemT> getAdapter() {
        return Coordinator._getInstance().getAdapter(adapter);
    }

    public ListIterator<OnWireItemT> iterator(PropT prop, XMLSerializer context) {
        return new ListIteratorImpl( core.iterator(prop,context), context );
    }

    public PackT startPacking(BeanT bean, Accessor<BeanT, PropT> accessor) throws AccessorException {
        return core.startPacking(bean,accessor);
    }

    public void addToPack(PackT pack, OnWireItemT item) throws AccessorException {
        InMemItemT r;
        try {
            r = getAdapter().unmarshal(item);
        } catch (Exception e) {
            throw new AccessorException(e);
        }
        core.addToPack(pack,r);
    }

    public void endPacking(PackT pack, BeanT bean, Accessor<BeanT,PropT> accessor) throws AccessorException {
        core.endPacking(pack,bean,accessor);
    }

    public void reset(BeanT bean, Accessor<BeanT, PropT> accessor) throws AccessorException {
        core.reset(bean,accessor);
    }

    private final class ListIteratorImpl implements ListIterator<OnWireItemT> {
        private final ListIterator<InMemItemT> core;
        private final XMLSerializer serializer;

        public ListIteratorImpl(ListIterator<InMemItemT> core,XMLSerializer serializer) {
            this.core = core;
            this.serializer = serializer;
        }

        public boolean hasNext() {
            return core.hasNext();
        }

        public OnWireItemT next() throws SAXException, JAXBException {
            InMemItemT next = core.next();
            try {
                return getAdapter().marshal(next);
            } catch (Exception e) {
                serializer.reportError(null,e);
                return null; // recover this error by returning null
            }
        }
    }
}
