/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.istack.logging;

import com.sun.istack.NotNull;

import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This is a helper class that provides some conveniece methods wrapped around the
 * standard {@link java.util.logging.Logger} interface.
 *
 * The class also makes sure that logger names of each Metro subsystem are consistent
 * with each other.
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 * @author Fabian Ritzmann
 */
public class Logger {

    private static final String WS_LOGGING_SUBSYSTEM_NAME_ROOT = "com.sun.metro";
    private static final String ROOT_WS_PACKAGE = "com.sun.xml.ws.";
    //
    private static final Level METHOD_CALL_LEVEL_VALUE = Level.FINEST;
    //
    private final String componentClassName;
    private final java.util.logging.Logger logger;

    /**
     * Prevents creation of a new instance of this Logger unless used by a subclass.
     */
    protected Logger(final String systemLoggerName, final String componentName) {
        this.componentClassName = "[" + componentName + "] ";
        this.logger = java.util.logging.Logger.getLogger(systemLoggerName);
    }

    /**
     * <p>
     * The factory method returns preconfigured Logger wrapper for the class. Method calls
     * {@link #getSystemLoggerName(java.lang.Class)} to generate default logger name.
     * </p>
     * <p>
     * Since there is no caching implemented, it is advised that the method is called only once
     * per a class in order to initialize a final static logger variable, which is then used
     * through the class to perform actual logging tasks.
     * </p>
     *
     * @param componentClass class of the component that will use the logger instance. Must not be {@code null}.
     * @return logger instance preconfigured for use with the component
     * @throws NullPointerException if the componentClass parameter is {@code null}.
     */
    public static @NotNull Logger getLogger(final @NotNull Class<?> componentClass) {
        return new Logger(getSystemLoggerName(componentClass), componentClass.getName());
    }

    /**
     * The factory method returns preconfigured Logger wrapper for the class. Since there is no caching implemented,
     * it is advised that the method is called only once per a class in order to initialize a final static logger variable,
     * which is then used through the class to perform actual logging tasks.
     *
     * This method should be only used in a special cases when overriding of a default logger name derived from the
     * package of the component class is needed. For all common use cases please use {@link #getLogger(java.lang.Class)}
     * method.
     *
     * @param customLoggerName custom name of the logger.
     * @param componentClass class of the component that will use the logger instance. Must not be {@code null}.
     * @return logger instance preconfigured for use with the component
     * @throws NullPointerException if the componentClass parameter is {@code null}.
     *
     * @see #getLogger(java.lang.Class)
     */
    public static @NotNull Logger getLogger(final @NotNull String customLoggerName, final @NotNull Class<?> componentClass) {
        return new Logger(customLoggerName, componentClass.getName());
    }

    /**
     * Calculates the subsystem suffix based on the package of the component class
     * @param componentClass class of the component that will use the logger instance. Must not be {@code null}.
     * @return system logger name for the given {@code componentClass} instance
     */
    static final String getSystemLoggerName(@NotNull Class<?> componentClass) {
        StringBuilder sb = new StringBuilder(componentClass.getPackage().getName());
        final int lastIndexOfWsPackage = sb.lastIndexOf(ROOT_WS_PACKAGE);
        if (lastIndexOfWsPackage > -1) {
            sb.replace(0, lastIndexOfWsPackage + ROOT_WS_PACKAGE.length(), "");

            StringTokenizer st = new StringTokenizer(sb.toString(), ".");
            sb = new StringBuilder(WS_LOGGING_SUBSYSTEM_NAME_ROOT).append(".");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if ("api".equals(token)) {
                    token = st.nextToken();
                }
                sb.append(token);
            }
        }

        return sb.toString();
    }

    public void log(final Level level, final String message) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        logger.logp(level, componentClassName, getCallerMethodName(), message);
    }

    public void log(final Level level, final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        logger.logp(level, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void finest(final String message) {
        if (!this.logger.isLoggable(Level.FINEST)) {
            return;
        }
        logger.logp(Level.FINEST, componentClassName, getCallerMethodName(), message);
    }

    public void finest(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINEST)) {
            return;
        }
        logger.logp(Level.FINEST, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void finer(final String message) {
        if (!this.logger.isLoggable(Level.FINER)) {
            return;
        }
        logger.logp(Level.FINER, componentClassName, getCallerMethodName(), message);
    }

    public void finer(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINER)) {
            return;
        }
        logger.logp(Level.FINER, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void fine(final String message) {
        if (!this.logger.isLoggable(Level.FINE)) {
            return;
        }
        logger.logp(Level.FINE, componentClassName, getCallerMethodName(), message);
    }

    public void fine(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINE)) {
            return;
        }
        logger.logp(Level.FINE, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void info(final String message) {
        if (!this.logger.isLoggable(Level.INFO)) {
            return;
        }
        logger.logp(Level.INFO, componentClassName, getCallerMethodName(), message);
    }

    public void info(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.INFO)) {
            return;
        }
        logger.logp(Level.INFO, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void config(final String message) {
        if (!this.logger.isLoggable(Level.CONFIG)) {
            return;
        }
        logger.logp(Level.CONFIG, componentClassName, getCallerMethodName(), message);
    }

    public void config(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.CONFIG)) {
            return;
        }
        logger.logp(Level.CONFIG, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void warning(final String message) {
        if (!this.logger.isLoggable(Level.WARNING)) {
            return;
        }
        logger.logp(Level.WARNING, componentClassName, getCallerMethodName(), message);
    }

    public void warning(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.WARNING)) {
            return;
        }
        logger.logp(Level.WARNING, componentClassName, getCallerMethodName(), message, thrown);
    }

    public void severe(final String message) {
        if (!this.logger.isLoggable(Level.SEVERE)) {
            return;
        }
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), message);
    }

    public void severe(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.SEVERE)) {
            return;
        }
        logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), message, thrown);
    }

    public boolean isMethodCallLoggable() {
        return this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE);
    }

    public boolean isLoggable(final Level level) {
        return this.logger.isLoggable(level);
    }

    public void setLevel(final Level level) {
        this.logger.setLevel(level);
    }

    public void entering() {
        if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
            return;
        }

        logger.entering(componentClassName, getCallerMethodName());
    }

    public void entering(final Object... parameters) {
        if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
            return;
        }

        logger.entering(componentClassName, getCallerMethodName(), parameters);
    }

    public void exiting() {
        if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        logger.exiting(componentClassName, getCallerMethodName());
    }

    public void exiting(final Object result) {
        if (!this.logger.isLoggable(METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        logger.exiting(componentClassName, getCallerMethodName(), result);
    }

    /**
     * Method logs {@code exception}'s message as a {@code SEVERE} logging level
     * message.
     * <p/>
     * If {@code cause} parameter is not {@code null}, it is logged as well and
     * {@code exception} original cause is initialized with instance referenced
     * by {@code cause} parameter.
     *
     * @param exception exception whose message should be logged. Must not be
     *        {@code null}.
     * @param cause initial cause of the exception that should be logged as well
     *        and set as {@code exception}'s original cause. May be {@code null}.
     * @return the same exception instance that was passed in as the {@code exception}
     *         parameter.
     */
    public <T extends Throwable> T logSevereException(final T exception, final Throwable cause) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (cause == null) {
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage());
            } else {
                exception.initCause(cause);
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage(), cause);
            }
        }

        return exception;
    }

    /**
     * Method logs {@code exception}'s message as a {@code SEVERE} logging level
     * message.
     * <p/>
     * If {@code logCause} parameter is {@code true}, {@code exception}'s original
     * cause is logged as well (if exists). This may be used in cases when
     * {@code exception}'s class provides constructor to initialize the original
     * cause. In such case you do not need to use
     * {@link #logSevereException(Throwable, Throwable)}
     * method version but you might still want to log the original cause as well.
     *
     * @param exception exception whose message should be logged. Must not be
     *        {@code null}.
     * @param logCause deterimnes whether initial cause of the exception should
     *        be logged as well
     * @return the same exception instance that was passed in as the {@code exception}
     *         parameter.
     */
    public <T extends Throwable> T logSevereException(final T exception, final boolean logCause) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (logCause && exception.getCause() != null) {
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            } else {
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage());
            }
        }

        return exception;
    }

    /**
     * Same as {@link #logSevereException(Throwable, boolean) logSevereException(exception, true)}.
     */
    public <T extends Throwable> T logSevereException(final T exception) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (exception.getCause() == null) {
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage());
            } else {
                logger.logp(Level.SEVERE, componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
        }

        return exception;
    }

    /**
     * Method logs {@code exception}'s message at the logging level specified by the
     * {@code level} argument.
     * <p/>
     * If {@code cause} parameter is not {@code null}, it is logged as well and
     * {@code exception} original cause is initialized with instance referenced
     * by {@code cause} parameter.
     *
     * @param exception exception whose message should be logged. Must not be
     *        {@code null}.
     * @param cause initial cause of the exception that should be logged as well
     *        and set as {@code exception}'s original cause. May be {@code null}.
     * @param level loging level which should be used for logging
     * @return the same exception instance that was passed in as the {@code exception}
     *         parameter.
     */
    public <T extends Throwable> T logException(final T exception, final Throwable cause, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (cause == null) {
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage());
            } else {
                exception.initCause(cause);
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage(), cause);
            }
        }

        return exception;
    }

    /**
     * Method logs {@code exception}'s message at the logging level specified by the
     * {@code level} argument.
     * <p/>
     * If {@code logCause} parameter is {@code true}, {@code exception}'s original
     * cause is logged as well (if exists). This may be used in cases when
     * {@code exception}'s class provides constructor to initialize the original
     * cause. In such case you do not need to use
     * {@link #logException(Throwable, Throwable, Level) logException(exception, cause, level)}
     * method version but you might still want to log the original cause as well.
     *
     * @param exception exception whose message should be logged. Must not be
     *        {@code null}.
     * @param logCause deterimnes whether initial cause of the exception should
     *        be logged as well
     * @param level loging level which should be used for logging
     * @return the same exception instance that was passed in as the {@code exception}
     *         parameter.
     */
    public <T extends Throwable> T logException(final T exception, final boolean logCause, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (logCause && exception.getCause() != null) {
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            } else {
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage());
            }
        }

        return exception;
    }

    /**
     * Same as {@link #logException(Throwable, Throwable, Level)
     * logException(exception, true, level)}.
     */
    public <T extends Throwable> T logException(final T exception, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (exception.getCause() == null) {
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage());
            } else {
                logger.logp(level, componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
        }

        return exception;
    }

    /**
     * Function returns the name of the caller method for the method executing this
     * function.
     *
     * @return caller method name from the call stack of the current {@link Thread}.
     */
    private static String getCallerMethodName() {
        return getStackMethodName(5);
    }

    /**
     * Method returns the name of the method that is on the {@code methodIndexInStack}
     * position in the call stack of the current {@link Thread}.
     *
     * @param methodIndexInStack index to the call stack to get the method name for.
     * @return the name of the method that is on the {@code methodIndexInStack}
     *         position in the call stack of the current {@link Thread}.
     */
    private static String getStackMethodName(final int methodIndexInStack) {
        final String methodName;

        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > methodIndexInStack + 1) {
            methodName = stack[methodIndexInStack].getMethodName();
        } else {
            methodName = "UNKNOWN METHOD";
        }

        return methodName;
    }

}