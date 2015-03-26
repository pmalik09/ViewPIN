// $Id: PinAgent/src/com/safenetinc/ds/XmlSignature.java 1.1 2008/09/04 10:45:38IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to create XML Digital Signatures
 * @author Stuart Horler
 *
 *
 */
public class XmlSignature
{
    private SignedInfo signedInfo = null;
    private KeyInfo keyInfo = null;
    private SignatureValue signatureValue = null;
    
    /**
     * @param signedInfo The {@link SignedInfo} element to use
     */
    public XmlSignature(SignedInfo signedInfo)
    {
        super();
        
        setSignedInfo(signedInfo);
    }

    /**
     * Method to sign an XML document
     * @param document The document to sign
     * @param kp The KeyPair to use for the signature
     * 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public void sign(Document document, KeyPair kp) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException
    {
    	Element signatureElement;
    	Element signedInfoElement;
    	Signature signatureEngine;
    	byte[] canonicalizedSignedInfoElement;
    	byte[] signedCanonicalizedSignedInfoElement;
    	SignatureValue valueOfSignature;
    	
    	signatureElement = null;
    	signedInfoElement = null;
    	signatureEngine = null;
    	canonicalizedSignedInfoElement = null;
    	signedCanonicalizedSignedInfoElement = null;
    	valueOfSignature = null;
    	
    	signatureElement = toDom(document);
    	
    	signedInfoElement = (Element)signatureElement.getElementsByTagNameNS(com.safenetinc.ds.SignatureConstants.DS_NS, "SignedInfo").item(0);
    	
    	canonicalizedSignedInfoElement = SignatureUtils.exclusiveCanonicalizeWithoutComments(signedInfoElement);
        
        // Sign SignedInfo element
        signatureEngine = Signature.getInstance("SHA1withRSA");
        signatureEngine.initSign(kp.getPrivate());
        signatureEngine.update(canonicalizedSignedInfoElement);
        signedCanonicalizedSignedInfoElement = signatureEngine.sign();
        
        valueOfSignature = new SignatureValue(new String(Base64.encodeBase64(signedCanonicalizedSignedInfoElement, false)));
        setSignatureValue(valueOfSignature);
    }
        
    /**
     * @param reference The Reference element to add
     */
    public void addReference(Reference reference)
    {
        getSignedInfo().addReference(reference);
    }
    
    /**
     * @param keyInfoToAdd The KeyInfo element to add
     */
    public void addKeyInfo(KeyInfo keyInfoToAdd)
    {
        setKeyInfo(keyInfoToAdd);
    }
    
    private void setKeyInfo(KeyInfo keyInfo)
    {
        this.keyInfo = keyInfo;
    }
    
    private KeyInfo getKeyInfo()
    {
        return this.keyInfo;
    }
        
    /**
     * Adds a signature element to the supplied document
     * @param document The document to add the signature element to
     * @return an Element containing the supplied document and added signature element
     */
    public Element toDom(Document document)
    {
        Element signatureElement;
                
        signatureElement = null;
        
        signatureElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Signature");
        signatureElement.setAttributeNS(SignatureConstants.XMLNS_NS, "xmlns:ds", SignatureConstants.DS_NS);
        
        signatureElement.appendChild(getSignedInfo().toDom(document));
        
        if(getSignatureValue() != null)
        {
        	signatureElement.appendChild(getSignatureValue().toDom(document));	
        }
        
        if(getKeyInfo() != null)
        {
            signatureElement.appendChild(getKeyInfo().toDom(document));
        }
        
        return signatureElement;
    }
    
    private void setSignedInfo(SignedInfo signedInfo)
    {
        this.signedInfo = signedInfo;
    }
    
    private SignedInfo getSignedInfo()
    {
        return this.signedInfo;
    }
    
    /**
     * @param signatureValue The signature value to use
     */
    public void setSignatureValue(SignatureValue signatureValue)
    {
    	this.signatureValue = signatureValue;
    }
    
    /**
     * @return The signature value held by this class
     */
    public SignatureValue getSignatureValue()
    {
    	return this.signatureValue;
    }
}
