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

package com.sun.xml.bind.marshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 * 
 * @since JAXB1.0
 */
public class Messages
{
    public static String format( String property ) {
        return format( property, null );
    }
    
    public static String format( String property, Object arg1 ) {
        return format( property, new Object[]{arg1} );
    }
    
    public static String format( String property, Object arg1, Object arg2 ) {
        return format( property, new Object[]{arg1,arg2} );
    }
    
    public static String format( String property, Object arg1, Object arg2, Object arg3 ) {
        return format( property, new Object[]{arg1,arg2,arg3} );
    }
    
    // add more if necessary.
    
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object[] args ) {
        String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text,args);
    }
    
//
//
// Message resources
//
//
    public static final String NOT_MARSHALLABLE = // 0 args
        "MarshallerImpl.NotMarshallable";
        
    public static final String UNSUPPORTED_RESULT = // 0 args
        "MarshallerImpl.UnsupportedResult";
        
    public static final String UNSUPPORTED_ENCODING = // 1 arg
        "MarshallerImpl.UnsupportedEncoding";
    
    public static final String NULL_WRITER = // 0 args
        "MarshallerImpl.NullWriterParam";
    
    public static final String ASSERT_FAILED = // 0 args
        "SAXMarshaller.AssertFailed";
    
    /**
     * @deprecated use ERR_MISSING_OBJECT2
     */
    public static final String ERR_MISSING_OBJECT = // 0 args
        "SAXMarshaller.MissingObject";
    
    /**
     * @deprecated
     *  use {@link com.sun.xml.bind.v2.runtime.XMLSerializer#reportMissingObjectError(String)} 
     */
    public static final String ERR_MISSING_OBJECT2 = // 1 arg
        "SAXMarshaller.MissingObject2";
    
    /**
     * @deprecated only used from 1.0
     */
    public static final String ERR_DANGLING_IDREF = // 1 arg
        "SAXMarshaller.DanglingIDREF";

    /**
     * @deprecated only used from 1.0
     */
    public static final String ERR_NOT_IDENTIFIABLE = // 0 args
        "SAXMarshaller.NotIdentifiable";

    public static final String DOM_IMPL_DOESNT_SUPPORT_CREATELEMENTNS = // 2 args
        "SAX2DOMEx.DomImplDoesntSupportCreateElementNs";
}
