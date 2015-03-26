// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/NullEntityResolver.java 1.1 2008/09/04 10:47:42IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Stuart Horler
 *
 *
 */
public class NullEntityResolver implements EntityResolver
{
    private static final Logger logger = Logger.getLogger(NullEntityResolver.class);
    
    /**
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
    {
        getLogger().warn("attempt to resolve unauthorised external entity, publicId = " + publicId + " systemId = " + systemId);
        
        return new InputSource(new ByteArrayInputStream(new byte[] { }));
    }
    
    private static Logger getLogger()
    {
        return NullEntityResolver.logger;
    }
}
