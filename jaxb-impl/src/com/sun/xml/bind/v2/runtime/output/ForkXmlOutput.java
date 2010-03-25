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

package com.sun.xml.bind.v2.runtime.output;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.Name;

import org.xml.sax.SAXException;

/**
 * {@link XmlOutput} that writes to two {@link XmlOutput}s.
 * @author Kohsuke Kawaguchi
 */
public final class ForkXmlOutput extends XmlOutputAbstractImpl {
    private final XmlOutput lhs;
    private final XmlOutput rhs;

    public ForkXmlOutput(XmlOutput lhs, XmlOutput rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        lhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        rhs.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
    }

    @Override
    public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
        lhs.endDocument(fragment);
        rhs.endDocument(fragment);
    }

    @Override
    public void beginStartTag(Name name) throws IOException, XMLStreamException {
        lhs.beginStartTag(name);
        rhs.beginStartTag(name);
    }

    @Override
    public void attribute(Name name, String value) throws IOException, XMLStreamException {
        lhs.attribute(name, value);
        rhs.attribute(name, value);
    }

    @Override
    public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
        lhs.endTag(name);
        rhs.endTag(name);
    }

    public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
        lhs.beginStartTag(prefix,localName);
        rhs.beginStartTag(prefix,localName);
    }

    public void attribute(int prefix, String localName, String value) throws IOException, XMLStreamException {
        lhs.attribute(prefix,localName,value);
        rhs.attribute(prefix,localName,value);
    }

    public void endStartTag() throws IOException, SAXException {
        lhs.endStartTag();
        rhs.endStartTag();
    }

    public void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException {
        lhs.endTag(prefix,localName);
        rhs.endTag(prefix,localName);
    }

    public void text(String value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        lhs.text(value,needsSeparatingWhitespace);
        rhs.text(value,needsSeparatingWhitespace);
    }

    public void text(Pcdata value, boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        lhs.text(value,needsSeparatingWhitespace);
        rhs.text(value,needsSeparatingWhitespace);
    }
}
