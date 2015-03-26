package com.safenetinc.viewpin.authority.xml.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An EntityResolver implementation for null entities
 * 
 * @author Stuart Horler
 * 
 */
public class NullEntityResolver implements EntityResolver
{
    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity (@SuppressWarnings("unused") String publicId, @SuppressWarnings("unused") String systemId) throws SAXException, IOException
    {
        return new InputSource(new ByteArrayInputStream(new byte[] {}));
    }
}
