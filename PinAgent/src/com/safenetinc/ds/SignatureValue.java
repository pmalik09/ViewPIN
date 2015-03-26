// $Id: PinAgent/src/com/safenetinc/ds/SignatureValue.java 1.1 2008/09/04 10:45:24IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the Signature Value element of an XML Digital Signature document
 * @author Stuart Horler
 *
 *
 */
public class SignatureValue
{
    private String value = null;
    
    /**
     * Construct the class with a specified signature value
     * @param value
     */
    public SignatureValue(String value)
    {
        super();
        
        setValue(value);
    }
    
    private void setValue(String value)
    {
        this.value = value;
    }
    
    /**
     * @return The signature value held by this class
     */
    public String getValue()
    {
        return this.value;
    }
    
    Element toDom(Document document)
    {
        Element signatureValueElement;
        
        signatureValueElement = null;
        
        signatureValueElement = document.createElementNS(SignatureConstants.DS_NS, "ds:SignatureValue");
        signatureValueElement.appendChild(document.createTextNode(getValue()));
        
        return signatureValueElement;
    }
}
