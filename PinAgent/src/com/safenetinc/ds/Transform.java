// $Id: PinAgent/src/com/safenetinc/ds/Transform.java 1.1 2008/09/04 10:45:29IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the Transform element of an XML Digital Signature
 * @author Stuart Horler
 *
 */
public class Transform
{
    /**
     * The Enveloped signature transform URI/Algorithm
     */
    public static final String ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    /**
     * The Exclusive Without Comments URI/Algorithm
     */
    public static final String EXCLUSIVE_WITHOUT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
    
    private String algorithm = null;
    
    /**
     * @param algorithm The algorithm used with the transform
     */
    public Transform(String algorithm)
    {
        super();
        
        setAlgorithm(algorithm);
    }
    
    private void setAlgorithm(String algorithm)
    {
        this.algorithm = algorithm;
    }
    
    private String getAlgorithm()
    {
        return this.algorithm;
    }
    
    Element toDom(Document document)
    {
        Element transformElement;
        
        transformElement = null;
        
        transformElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Transform");
        transformElement.setAttribute("Algorithm", getAlgorithm());
        
        return transformElement;
    }
}
