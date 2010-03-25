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

package com.sun.xml.bind.v2.runtime;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.xml.bind.v2.model.annotation.Locatable;

/**
 * Signals an incorrect use of JAXB annotations.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 * @since JAXB 2.0 EA1
 */
public class IllegalAnnotationException extends JAXBException {

    /**
     * Read-only list of {@link Location}s.
     */
    private final List<List<Location>> pos;

    private static final long serialVersionUID = 1L;

    public IllegalAnnotationException(String message, Locatable src) {
        super(message);
        pos = build(src);
    }

    public IllegalAnnotationException(String message, Annotation src) {
        this(message,cast(src));
    }

    public IllegalAnnotationException(String message, Locatable src1, Locatable src2) {
        super(message);
        pos = build(src1,src2);
    }

    public IllegalAnnotationException(String message, Annotation src1, Annotation src2) {
        this(message,cast(src1),cast(src2));
    }

    public IllegalAnnotationException(String message, Annotation src1, Locatable src2) {
        this(message,cast(src1),src2);
    }

    public IllegalAnnotationException(String message, Throwable cause, Locatable src) {
        super(message, cause);
        pos = build(src);
    }

    private static Locatable cast(Annotation a) {
        if(a instanceof Locatable)
            return (Locatable)a;
        else
            return null;
    }

    private List<List<Location>> build(Locatable... srcs) {
        List<List<Location>> r = new ArrayList<List<Location>>();
        for( Locatable l : srcs ) {
            if(l!=null) {
                List<Location> ll = convert(l);
                if(ll!=null && !ll.isEmpty())
                    r.add(ll);
            }
        }
        return Collections.unmodifiableList(r);
    }

    /**
     * Builds a list of {@link Location}s out of a {@link Locatable}.
     */
    private List<Location> convert(Locatable src) {
        if(src==null)   return null;

        List<Location> r = new ArrayList<Location>();
        for( ; src!=null; src=src.getUpstream())
            r.add(src.getLocation());
        return Collections.unmodifiableList(r);
    }



    /**
     * Returns a read-only list of {@link Location} that indicates
     * where in the source code the problem has happened.
     *
     * <p>
     * Normally, an annotation error happens on one particular
     * annotation, in which case this method returns a list that
     * contains another list, which in turn contains the location
     * information that leads to the error location
     * (IOW, <tt>[ [pos1,pos2,...,posN] ]</tt>)
     *
     * <p>
     * Sometimes, an error could occur because of two or more conflicting
     * annotations, in which case this method returns a list
     * that contains many lists, where each list contains
     * the location information that leads to each of the conflicting
     * annotations
     * (IOW, <tt>[ [pos11,pos12,...,pos1N],[pos21,pos22,...,pos2M], ... ]</tt>)
     *
     * <p>
     * Yet some other time, the runtime can fail to provide any
     * error location, in which case this method returns an empty list.
     * (IOW, <tt>[]</tt>). We do try hard to make sure this won't happen,
     * so please <a href="http://jaxb.dev.java.net/">let us know</a>
     * if you see this behavior.
     *
     *
     * <h3>List of {@link Location}</h3>
     * <p>
     * Each error location is identified not just by one {@link Location}
     * object, but by a sequence of {@link Location}s that shows why
     * the runtime is led to the place of the error.
     * This list is sorted such that the most specific {@link Location} comes
     * to the first in the list, sort of like a stack trace.
     *
     * <p>
     * For example, suppose you specify class <tt>Foo</tt> to {@link JAXBContext},
     * <tt>Foo</tt> derives from <tt>Bar</tt>, <tt>Bar</tt> has a field <tt>pea</tt>
     * that points to <tt>Zot</tt>, <tt>Zot</tt> contains a <tt>gum</tt>
     * property, and this property has an errornous annotation.
     * Then when this exception is thrown, the list of {@link Location}s
     * will look something like
     * <tt>[ "gum property", "Zot class", "pea property", "Bar class", "Foo class" ]</tt>
     *
     *
     * @return
     *      can be empty when no source position is available,
     *      but never null. The returned list will never contain
     *      null nor length-0 {@link List}.
     */
    public List<List<Location>> getSourcePos() {
        return pos;
    }

    /**
     * Returns the exception name, message, and related information
     * together in one string.
     *
     * <p>
     * Overriding this method (instead of {@link #printStackTrace} allows
     * this crucial detail to show up even when this exception is nested
     * inside other exceptions.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(getMessage());

        for( List<Location> locs : pos ) {
            sb.append("\n\tthis problem is related to the following location:");
            for( Location loc : locs )
                sb.append("\n\t\tat ").append(loc.toString());
        }

        return sb.toString();
    }
}
