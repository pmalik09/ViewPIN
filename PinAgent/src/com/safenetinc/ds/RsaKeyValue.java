// $Id: PinAgent/src/com/safenetinc/ds/RsaKeyValue.java 1.1 2008/09/04 10:45:16IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.security.interfaces.RSAPublicKey;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.commons.codec.binary.Base64;

/**
 * Class to represent the RSA Key Value element of an XML Digital Signature document
 * 
 * @author Stuart Horler
 *
 */
public class RsaKeyValue extends KeyValue
{
    private RSAPublicKey publicKey = null;
    
    /**
     * Constructor
     * @param publicKey The {@link RSAPublicKey} object to include in this document element
     */
    public RsaKeyValue(RSAPublicKey publicKey)
    {
        super();
        
        setPublicKey(publicKey);
    }
    
    @Override
    Element toDom(Document document)
    {
        Element keyValue;
        Element rsaKeyValueElement;
        Element modulusElement;
        Element exponentElement;
        String encodedModulus;
        String encodedExponent;
        
        keyValue = null;
        rsaKeyValueElement = null;
        modulusElement = null;
        exponentElement = null;
        encodedModulus = null;
        encodedExponent = null;
        
        keyValue = document.createElementNS(SignatureConstants.DS_NS, "ds:KeyValue");
        rsaKeyValueElement = document.createElementNS(SignatureConstants.DS_NS, "ds:RSAKeyValue");
        keyValue.appendChild(rsaKeyValueElement);
        
        modulusElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Modulus");
        rsaKeyValueElement.appendChild(modulusElement);
        
        exponentElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Exponent");
        rsaKeyValueElement.appendChild(exponentElement);
        
        encodedModulus = new String(Base64.encodeBase64((getPublicKey().getModulus().toByteArray()), false));
        encodedExponent = new String(Base64.encodeBase64((getPublicKey().getPublicExponent().toByteArray()), false));
        
        modulusElement.appendChild(document.createTextNode(encodedModulus));
        exponentElement.appendChild(document.createTextNode(encodedExponent));
        
        return keyValue;
    }
    
    private void setPublicKey(RSAPublicKey publicKey)
    {
        this.publicKey = publicKey;   
    }
    
    /**
     * @return The {@link RSAPublicKey} element used by this class
     */
    public RSAPublicKey getPublicKey()
    {
        return this.publicKey;
    }
}
