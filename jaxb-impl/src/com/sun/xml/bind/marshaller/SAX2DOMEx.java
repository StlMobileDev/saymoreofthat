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

/*
 * SAX2DOMEx.java
 *
 * Created on February 22, 2002, 1:55 PM
 */

package com.sun.xml.bind.marshaller;

import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.xml.bind.util.Which;
import com.sun.istack.FinalArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * Builds a DOM tree from SAX2 events.
 * 
 * @author  Vivek Pandey
 * @since 1.0
 */
public class SAX2DOMEx implements ContentHandler {

    private Node node=null;
    private final Stack<Node> nodeStack = new Stack<Node>();
    private final FinalArrayList<String> unprocessedNamespaces = new FinalArrayList<String>();


    /**
     * Document object that owns the specified node.
     */
    private final Document document;

    /**
     * @param   node
     *      Nodes will be created and added under this object.
     */
    public SAX2DOMEx(Node node)
    {
        this.node = node;
        nodeStack.push(this.node);

        if( node instanceof Document )
            this.document = (Document)node;
        else
            this.document = node.getOwnerDocument();
    }

    /**
     * Creates a fresh empty DOM document and adds nodes under this document.
     */
    public SAX2DOMEx() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        document = factory.newDocumentBuilder().newDocument();
        node = document;
        nodeStack.push( document );
    }

    public final Element getCurrentElement() {
        return (Element) nodeStack.peek();
    }

    public Node getDOM() {
        return node;
    }

    public void startDocument() {
    }

    public void endDocument(){
    }

    public void startElement(String namespace, String localName, String qName, Attributes attrs){
        Node parent = nodeStack.peek();
        
        // some broken DOM implementatino (we confirmed it with SAXON)
        // return null from this method.
        Element element = document.createElementNS(namespace, qName);
        
        if( element==null ) {
            // if so, report an user-friendly error message,
            // rather than dying mysteriously with NPE.
            throw new AssertionError(
                Messages.format(Messages.DOM_IMPL_DOESNT_SUPPORT_CREATELEMENTNS,
                    document.getClass().getName(),
                    Which.which(document.getClass())));
        }
        
        // process namespace bindings
        for( int i=0; i<unprocessedNamespaces.size(); i+=2 ) {
            String prefix = unprocessedNamespaces.get(i+0);
            String uri = unprocessedNamespaces.get(i+1);
            
            String qname;
            if( "".equals(prefix) || prefix==null )
                qname = "xmlns";
            else
                qname = "xmlns:"+prefix;
            
            // older version of Xerces (I confirmed that the bug is gone with Xerces 2.4.0)
            // have a problem of re-setting the same namespace attribute twice.
            // work around this bug removing it first.
            if( element.hasAttributeNS("http://www.w3.org/2000/xmlns/",qname) ) {
                // further workaround for an old Crimson bug where the removeAttribtueNS
                // method throws NPE when the element doesn't have any attribute.
                // to be on the safe side, check the existence of attributes before
                // attempting to remove it.
                // for details about this bug, see org.apache.crimson.tree.ElementNode2
                // line 540 or the following message:
                // https://jaxb.dev.java.net/servlets/ReadMsg?list=users&msgNo=2767
                element.removeAttributeNS("http://www.w3.org/2000/xmlns/",qname);
            }
            // workaround until here
            
            element.setAttributeNS("http://www.w3.org/2000/xmlns/",qname, uri);
        }
        unprocessedNamespaces.clear();
        
        
        int length = attrs.getLength();
        for(int i=0;i<length;i++){
            String namespaceuri = attrs.getURI(i);
            String value = attrs.getValue(i);
            String qname = attrs.getQName(i);
            element.setAttributeNS(namespaceuri, qname, value);
        }
        // append this new node onto current stack node
        parent.appendChild(element);
        // push this node onto stack
        nodeStack.push(element);
    }
    
    public void endElement(String namespace, String localName, String qName){
        nodeStack.pop();
    }


    public void characters(char[] ch, int start, int length) {
        Node parent = nodeStack.peek();
        Text text = document.createTextNode(new String(ch, start, length));
        parent.appendChild(text);
    }



    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    public void processingInstruction(String target, String data) throws org.xml.sax.SAXException{
        Node parent = nodeStack.peek();
        Node node = document.createProcessingInstruction(target, data);
        parent.appendChild(node);	
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) {
    }

    public void startPrefixMapping(String prefix, String uri) {
        unprocessedNamespaces.add(prefix);
        unprocessedNamespaces.add(uri);
    }

    public void endPrefixMapping(String prefix) {
    }
}
