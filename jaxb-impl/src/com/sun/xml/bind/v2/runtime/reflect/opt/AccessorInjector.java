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

package com.sun.xml.bind.v2.runtime.reflect.opt;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.Util;
import com.sun.xml.bind.v2.bytecode.ClassTailor;

/**
 * @author Kohsuke Kawaguchi
 */
class AccessorInjector {

    private static final Logger logger = Util.getClassLogger();

    protected static final boolean noOptimize =
        Util.getSystemProperty(ClassTailor.class.getName()+".noOptimize")!=null;

    static {
        if(noOptimize)
            logger.info("The optimized code generation is disabled");
    }

    /**
     * Loads the optimized class and returns it.
     *
     * @return null
     *      if it fails for some reason.
     */
    public static Class<?> prepare(
        Class beanClass, String templateClassName, String newClassName, String... replacements ) {

        if(noOptimize)
            return null;

        try {
            ClassLoader cl = beanClass.getClassLoader();
            if(cl==null)    return null;    // how do I inject classes to this "null" class loader? for now, back off.

            Class c = null;
            synchronized (AccessorInjector.class) {
                c = Injector.find(cl,newClassName);
                if(c==null) {
                    byte[] image = tailor(templateClassName,newClassName,replacements);
    //                try {
    //                    new FileOutputStream("debug.class").write(image);
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
                    if(image==null)
                        return null;
                    c = Injector.inject(cl,newClassName,image);
                }
            }
            return c;
        } catch(SecurityException e) {
            // we don't have enough permission to do this
            logger.log(Level.INFO,"Unable to create an optimized TransducedAccessor ",e);
            return null;
        }
    }


    /**
     * Customizes a class file by replacing constant pools.
     *
     * @param templateClassName
     *      The resouce that contains the template class file.
     * @param replacements
     *      A list of pair of strings that specify the substitution
     *      {@code String[]{search_0, replace_0, search_1, replace_1, ..., search_n, replace_n }
     *
     *      The search strings found in the constant pool will be replaced by the corresponding
     *      replacement string.
     */
    private static byte[] tailor( String templateClassName, String newClassName, String... replacements ) {
        InputStream resource;
        if(CLASS_LOADER!=null)
            resource = CLASS_LOADER.getResourceAsStream(templateClassName+".class");
        else
            resource = ClassLoader.getSystemResourceAsStream(templateClassName+".class");
        if(resource==null)
            return null;

        return ClassTailor.tailor(resource,templateClassName,newClassName,replacements);
    }

    private static final ClassLoader CLASS_LOADER = AccessorInjector.class.getClassLoader();
}
