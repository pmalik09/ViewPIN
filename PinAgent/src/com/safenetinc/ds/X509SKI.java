// $Id: PinAgent/src/com/safenetinc/ds/X509SKI.java 1.1 2008/09/04 10:45:36IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the X509SKI element of an XML Digital Signature
 * @author Stuart Horler
 *
 *
 */
public class X509SKI extends X509KeyIdentifier 
{
	private byte[] subjectKeyIdentifier = null;
	
    /**
     * Constructor
     * @param subjectKeyIdentifier The subject key identifier to include with this element
     */
    public X509SKI(byte[] subjectKeyIdentifier)
    {
    	super();
    	
    	setSubjectKeyIdentifier(subjectKeyIdentifier);
    }

	@Override
    Element toDom(Document document)
	{
		Element x509SkiElement;
		String encodedSubjectKeyIdentifier;
		
		x509SkiElement = null;
		encodedSubjectKeyIdentifier = null;
		
		x509SkiElement = document.createElementNS(SignatureConstants.DS_NS, "ds:X509SKI");
		
		encodedSubjectKeyIdentifier = new String(Base64.encodeBase64(getSubjectKeyIdentifier()));
		
		x509SkiElement.appendChild(document.createTextNode(encodedSubjectKeyIdentifier));
		
		return x509SkiElement;
	}
	
	private void setSubjectKeyIdentifier(byte[] subjectKeyIdentifier) 
	{
		this.subjectKeyIdentifier = subjectKeyIdentifier;
	}
	
	/**
	 * @return The subject key identifier used by this element
	 */
	public byte[] getSubjectKeyIdentifier() 
	{
		return this.subjectKeyIdentifier.clone();
	}
	
}
