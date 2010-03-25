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

package com.sun.xml.bind.api;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * Holds thread specific state information for {@link Bridge}s,
 * to make {@link Bridge} thread-safe.
 *
 * <p>
 * This object cannot be used concurrently; two threads cannot
 * use the same object with {@link Bridge}s at the same time, nor
 * a thread can use a {@link BridgeContext} with one {@link Bridge} while
 * the same context is in use by another {@link Bridge}.
 *
 * <p>
 * {@link BridgeContext} is relatively a heavy-weight object, and
 * therefore it is expected to be cached by the JAX-RPC RI.
 *
 * <p>
 * <b>Subject to change without notice</b>.
 *
 * @author Kohsuke Kawaguchi
 * @since 2.0 EA1
 * @see Bridge
 * @deprecated
 *      The caller no longer needs to use this, as {@link Bridge} has
 *      methods that can work without {@link BridgeContext}.
 */
public abstract class BridgeContext {
    protected BridgeContext() {}
    
    /**
     * Registers the error handler that receives unmarshalling/marshalling errors.
     *
     * @param handler
     *      can be null, in which case all errors will be considered fatal.
     *
     * @since 2.0 EA1
     */
    public abstract void setErrorHandler(ValidationEventHandler handler);

    /**
     * Sets the {@link AttachmentMarshaller}.
     *
     * @since 2.0 EA1
     */
    public abstract void setAttachmentMarshaller(AttachmentMarshaller m);

    /**
     * Sets the {@link AttachmentUnmarshaller}.
     *
     * @since 2.0 EA1
     */
    public abstract void setAttachmentUnmarshaller(AttachmentUnmarshaller m);

    /**
     * Gets the last {@link AttachmentMarshaller} set through
     * {@link AttachmentMarshaller}.
     *
     * @since 2.0 EA2
     */
    public abstract AttachmentMarshaller getAttachmentMarshaller();

    /**
     * Gets the last {@link AttachmentUnmarshaller} set through
     * {@link AttachmentUnmarshaller}.
     *
     * @since 2.0 EA2
     */
    public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}
