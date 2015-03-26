// $Id: PinAgent/src/com/safenetinc/ds/SignatureMethod.java 1.1 2008/09/04 10:45:19IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the Signature Method element of an XML Digital Signature document
 * @author Stuart Horler
 *
 *
 */
public class SignatureMethod
{
    /**
     * The RSA_SHA1 URI
     */
    public static final String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    
    private String algorithm = null;
    
    /**
     * Create a new instancde of this class
     * @param algorithm The signature method algorithm to use
     */
    public SignatureMethod(String algorithm)
    {
        super();
        
        setAlgorithm(algorithm);
    }
    
    private void setAlgorithm(String algorithm)
    {
        this.algorithm = algorithm;
    }
    
    /**
     * @return The signature method algorithm used by this class
     */
    public String getAlgorithm()
    {
        return this.algorithm;
    }
    
    Element toDom(Document document)
    {
        Element signatureMethodElement;
        
        signatureMethodElement = null;
        
        signatureMethodElement = document.createElementNS(SignatureConstants.DS_NS, "ds:SignatureMethod");
        signatureMethodElement.setAttribute("Algorithm", getAlgorithm());
        
        return signatureMethodElement;
    }
}