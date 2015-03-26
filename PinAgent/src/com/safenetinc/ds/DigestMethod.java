// $Id: PinAgent/src/com/safenetinc/ds/DigestMethod.java 1.1 2008/09/04 10:45:09IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the digest method element of an XML Digital Signature document
 * 
 * @author Stuart Horler
 *
 *
 */
public class DigestMethod
{
    /**
     * The SHA-1 digest method namespace
     */
    public static final String DIGEST_METHOD_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    
    private String algorithm = null;
    
    /**
     * Constructor
     * 
     * @param algorithm The digest method algorithm to use
     */
    public DigestMethod(String algorithm)
    {
        super();
        
        setAlgorithm(algorithm);
    }
    
    private void setAlgorithm(String algorithm)
    {
        this.algorithm = algorithm;
    }
    
    /**
     * 
     * @return The digest method algorithm being represented by this class
     */
    public String getAlgorithm()
    {
        return this.algorithm;
    }
    
    Element toDom(Document document)
    {
        Element digestMethodElement;
        
        digestMethodElement = null;
        
        digestMethodElement = document.createElementNS(SignatureConstants.DS_NS, "ds:DigestMethod");
        digestMethodElement.setAttribute("Algorithm", getAlgorithm());
        
        return digestMethodElement;
    }
}