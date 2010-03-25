package com.sun.istack;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;

/**
 * {@link XMLFilterImpl} that masks start/end document SAX events.
 * @author Kohsuke Kawaguchi
 */
public class FragmentContentHandler extends XMLFilterImpl {
    public FragmentContentHandler() {
    }

    public FragmentContentHandler(XMLReader parent) {
        super(parent);
    }

    public FragmentContentHandler(ContentHandler handler) {
        super();
        setContentHandler(handler);
    }

    public void startDocument() throws SAXException {
        // noop
    }

    public void endDocument() throws SAXException {
        // noop
    }
}
