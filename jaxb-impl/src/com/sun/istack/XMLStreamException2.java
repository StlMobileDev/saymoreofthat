package com.sun.istack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;

/**
 * {@link XMLStreamException} that properly handles exception chaining.
 *
 * @author Kohsuke Kawaguchi
 */
public class XMLStreamException2 extends XMLStreamException {
    public XMLStreamException2(String msg) {
        super(msg);
    }

    public XMLStreamException2(Throwable th) {
        super(th);
    }

    public XMLStreamException2(String msg, Throwable th) {
        super(msg, th);
    }

    public XMLStreamException2(String msg, Location location) {
        super(msg, location);
    }

    public XMLStreamException2(String msg, Location location, Throwable th) {
        super(msg, location, th);
    }

    /**
     * {@link XMLStreamException} doesn't return the correct cause.
     */
    public Throwable getCause() {
        return getNestedException();
    }
}
