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

/**
 * Buffer for UTF-8 encoded string.
 *
 * See http://www.cl.cam.ac.uk/~mgk25/unicode.html#utf-8 for the UTF-8 encoding.
 *
 * @author Kohsuke Kawaguchi
 */
public final class Encoded {
    public byte[] buf;

    public int len;

    public Encoded() {}

    public Encoded(String text) {
        set(text);
    }

    public void ensureSize(int size) {
        if(buf==null || buf.length<size)
            buf = new byte[size];
    }

    public final void set( String text ) {
        int length = text.length();

        ensureSize(length*3+1); // +1 for append

        int ptr = 0;

        for (int i = 0; i < length; i++) {
            final char chr = text.charAt(i);
            if (chr > 0x7F) {
                if (chr > 0x7FF) {
                    if(Character.MIN_HIGH_SURROGATE<=chr && chr<=Character.MAX_LOW_SURROGATE) {
                        // surrogate
                        int uc = (((chr & 0x3ff) << 10) | (text.charAt(++i) & 0x3ff)) + 0x10000;

                        buf[ptr++] = (byte)(0xF0 | ((uc >> 18)));
                        buf[ptr++] = (byte)(0x80 | ((uc >> 12) & 0x3F));
                        buf[ptr++] = (byte)(0x80 | ((uc >> 6) & 0x3F));
                        buf[ptr++] = (byte)(0x80 + (uc & 0x3F));
                        continue;
                    }
                    buf[ptr++] = (byte)(0xE0 + (chr >> 12));
                    buf[ptr++] = (byte)(0x80 + ((chr >> 6) & 0x3F));
                } else {
                    buf[ptr++] = (byte)(0xC0 + (chr >> 6));
                }
                buf[ptr++] = (byte)(0x80 + (chr & 0x3F));
            } else {
                buf[ptr++] = (byte)chr;
            }
        }

        len = ptr;
    }

    /**
     * Fill in the buffer by encoding the specified characters
     * while escaping characters like &lt;
     *
     * @param isAttribute
     *      if true, characters like \t, \r, and \n are also escaped.
     */
    public final void setEscape(String text, boolean isAttribute) {
        int length = text.length();
        ensureSize(length*6+1);     // in the worst case the text is like """""", so we need 6 bytes per char

        int ptr = 0;

        for (int i = 0; i < length; i++) {
            final char chr = text.charAt(i);

            int ptr1 = ptr;
            if (chr > 0x7F) {
                if (chr > 0x7FF) {
                    if(Character.MIN_HIGH_SURROGATE<=chr && chr<=Character.MAX_LOW_SURROGATE) {
                        // surrogate
                        int uc = (((chr & 0x3ff) << 10) | (text.charAt(++i) & 0x3ff)) + 0x10000;

                        buf[ptr++] = (byte)(0xF0 | ((uc >> 18)));
                        buf[ptr++] = (byte)(0x80 | ((uc >> 12) & 0x3F));
                        buf[ptr++] = (byte)(0x80 | ((uc >> 6) & 0x3F));
                        buf[ptr++] = (byte)(0x80 + (uc & 0x3F));
                        continue;
                    }
                    buf[ptr1++] = (byte)(0xE0 + (chr >> 12));
                    buf[ptr1++] = (byte)(0x80 + ((chr >> 6) & 0x3F));
                } else {
                    buf[ptr1++] = (byte)(0xC0 + (chr >> 6));
                }
                buf[ptr1++] = (byte)(0x80 + (chr & 0x3F));
            } else {
                byte[] ent;

                if((ent=attributeEntities[chr])!=null) {
                    // the majority of the case is just printed as a char,
                    // so it's very important to reject them as quickly as possible

                    // check again to see if this really needs to be escaped
                    if(isAttribute || entities[chr]!=null)
                        ptr1 = writeEntity(ent,ptr1);
                    else
                        buf[ptr1++] = (byte)chr;
                } else
                    buf[ptr1++] = (byte)chr;
            }
            ptr = ptr1;
        }
        len = ptr;
    }

    private int writeEntity( byte[] entity, int ptr ) {
        System.arraycopy(entity,0,buf,ptr,entity.length);
        return ptr+entity.length;
    }

    /**
     * Writes the encoded bytes to the given output stream.
     */
    public final void write(UTF8XmlOutput out) throws IOException {
        out.write(buf,0,len);
    }

    /**
     * Appends a new character to the end of the buffer.
     * This assumes that you have enough space in the buffer.
     */
    public void append(char b) {
        buf[len++] = (byte)b;
    }

    /**
     * Reallocate the buffer to the exact size of the data
     * to reduce the memory footprint.
     */
    public void compact() {
        byte[] b = new byte[len];
        System.arraycopy(buf,0,b,0,len);
        buf = b;
    }

    /**
     * UTF-8 encoded entities keyed by their character code.
     * e.g., entities['&'] == AMP_ENTITY.
     *
     * In attributes we need to encode more characters.
     */
    private static final byte[][] entities = new byte[0x80][];
    private static final byte[][] attributeEntities = new byte[0x80][];

    static {
        add('&',"&amp;",false);
        add('<',"&lt;",false);
        add('>',"&gt;",false);
        add('"',"&quot;",false);
        add('\t',"&#x9;",true);
        add('\r',"&#xD;",false);
        add('\n',"&#xA;",true);
    }

    private static void add(char c, String s, boolean attOnly) {
        byte[] image = UTF8XmlOutput.toBytes(s);
        attributeEntities[c] = image;
        if(!attOnly)
            entities[c] = image;
    }
}
