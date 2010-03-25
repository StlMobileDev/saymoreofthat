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

package com.sun.xml.bind.v2.runtime.unmarshaller;

import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;

import com.sun.xml.bind.v2.runtime.JAXBContextImpl;

import org.xml.sax.SAXException;

/**
 * Loads a DOM.
 *
 * @author Kohsuke Kawaguchi
 */
public class DomLoader<ResultT extends Result> extends Loader {

    private final DomHandler<?,ResultT> dom;

    /**
     * Used to capture the state.
     *
     * This instance is created for each unmarshalling episode.
     */
    private final class State {
        /** This handler will receive SAX events. */
        private final TransformerHandler handler = JAXBContextImpl.createTransformerHandler();

        /** {@link #handler} will produce this result. */
        private final ResultT result;

        // nest level of elements.
        int depth = 1;

        public State( UnmarshallingContext context ) throws SAXException {
            result = dom.createUnmarshaller(context);

            handler.setResult(result);

            // emulate the start of documents
            try {
                handler.setDocumentLocator(context.getLocator());
                handler.startDocument();
                declarePrefixes( context, context.getAllDeclaredPrefixes() );
            } catch( SAXException e ) {
                context.handleError(e);
                throw e;
            }
        }

        public Object getElement() {
            return dom.getElement(result);
        }

        private void declarePrefixes( UnmarshallingContext context, String[] prefixes ) throws SAXException {
            for( int i=prefixes.length-1; i>=0; i-- ) {
                String nsUri = context.getNamespaceURI(prefixes[i]);
                if(nsUri==null)     throw new IllegalStateException("prefix \'"+prefixes[i]+"\' isn't bound");
                handler.startPrefixMapping(prefixes[i],nsUri );
            }
        }

        private void undeclarePrefixes( String[] prefixes ) throws SAXException {
            for( int i=prefixes.length-1; i>=0; i-- )
                handler.endPrefixMapping( prefixes[i] );
        }
    }

    public DomLoader(DomHandler<?, ResultT> dom) {
        super(true);
        this.dom = dom;
    }

    public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        UnmarshallingContext context = state.getContext();
        if (state.target == null)
            state.target = new State(context);

        State s = (State) state.target;
        try {
            s.declarePrefixes(context, context.getNewlyDeclaredPrefixes());
            s.handler.startElement(ea.uri, ea.local, ea.getQname(), ea.atts);
        } catch (SAXException e) {
            context.handleError(e);
            throw e;
        }
    }


    public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        state.loader = this;
        State s = (State) state.prev.target;
        s.depth++;
        state.target = s;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        if(text.length()==0)
            return;     // there's no point in creating an empty Text node in DOM. 
        try {
            State s = (State) state.target;
            s.handler.characters(text.toString().toCharArray(),0,text.length());
        } catch( SAXException e ) {
            state.getContext().handleError(e);
            throw e;
        }
    }

    public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        State s = (State) state.target;
        UnmarshallingContext context = state.getContext();

        try {
            s.handler.endElement(ea.uri, ea.local, ea.getQname());
            s.undeclarePrefixes(context.getNewlyDeclaredPrefixes());
        } catch( SAXException e ) {
            context.handleError(e);
            throw e;
        }

        if((--s.depth)==0) {
            // emulate the end of the document
            try {
                s.undeclarePrefixes(context.getAllDeclaredPrefixes());
                s.handler.endDocument();
            } catch( SAXException e ) {
                context.handleError(e);
                throw e;
            }

            // we are done
            state.target = s.getElement();
        }
    }

}
