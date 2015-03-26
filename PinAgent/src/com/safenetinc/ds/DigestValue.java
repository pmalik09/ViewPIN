// $Id: PinAgent/src/com/safenetinc/ds/DigestValue.java 1.1 2008/09/04 10:45:10IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the digest value element of an XML Digital Signature document
 * @author Stuart Horler
 *
 *
 */
public class DigestValue
{
    private String digestValue = null;
    
    /**
     * Constructor
     * @param digestValue The digest value for ths class to represent
     */
    public DigestValue(String digestValue)
    {
        super();
        
        setDigestValue(digestValue);
    }
    
    private void setDigestValue(String digestValue)
    {
        this.digestValue = digestValue;
    }
    
    /**
     * @return The digest value being represented by this class
     */
    public String getDigestValue()
    {
        return this.digestValue;
    }
    
    Element toDom(Document document)
    {
        Element digestValueElement;
        
        digestValueElement = null;
        
        digestValueElement = document.createElementNS(SignatureConstants.DS_NS, "ds:DigestValue");
        digestValueElement.appendChild(document.createTextNode(getDigestValue()));
        
        return digestValueElement;
    }
}
