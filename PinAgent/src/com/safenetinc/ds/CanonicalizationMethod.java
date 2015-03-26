// $Id: PinAgent/src/com/safenetinc/ds/CanonicalizationMethod.java 1.1 2008/09/04 10:45:07IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A representation of the Canonicalization system used within ViewPIN
 * 
 * @author Stuart Horler
 */
public class CanonicalizationMethod
{
    /**
     * The URL for the Exclusive XML Canonicalization standard
     */
    public static final String EXCLUSIVE_WITHOUT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    
    private String algorithm = null;
    
    /**
     * Constructs a new instance of the CanonicalizationMethod class
     * @param algorithm 
     */
    public CanonicalizationMethod(String algorithm)
    {
        super();
        
        setAlgorithm(algorithm);
    }
    
    private void setAlgorithm(String algorithm)
    {
        this.algorithm = algorithm;
    }
    
    /**
     * @return The algorithm used by this canonicalization method
     */
    public String getAlgorithm()
    {
        return this.algorithm;
    }
    
    Element toDom(Document document)
    {
        Element canonicalizationMethodElement;
        
        canonicalizationMethodElement = null;
        
        canonicalizationMethodElement = document.createElementNS(SignatureConstants.DS_NS, "ds:CanonicalizationMethod");
        canonicalizationMethodElement.setAttribute("Algorithm", getAlgorithm());
        
        return canonicalizationMethodElement;
    }
}
