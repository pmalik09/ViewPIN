// $Id: PinAgent/src/com/safenetinc/ds/X509Data.java 1.1 2008/09/04 10:45:33IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the X509Data element of an XML Digital Signature document
 * @author Stuart Horler
 *
 *
 */
public class X509Data 
{
	private ArrayList<X509KeyIdentifier> x509KeyIdentifiers = null;
	
    /**
     * Create a new instance of this class
     */
    public X509Data()
    {
    	super();
    
    	setX509KeyIdentifiers(new ArrayList<X509KeyIdentifier>());
    }
    
    /**
     * Add an {@link X509KeyIdentifier} to this element
     * @param x509KeyIdentifier The {@link X509KeyIdentifier} element to add
     */
    public void add(X509KeyIdentifier x509KeyIdentifier)
    {
        getX509KeyIdentifiers().add(x509KeyIdentifier);
    }
	
	private void setX509KeyIdentifiers(ArrayList<X509KeyIdentifier> keyIdentifiers) 
	{
		this.x509KeyIdentifiers = keyIdentifiers;
	}

	private ArrayList<X509KeyIdentifier> getX509KeyIdentifiers()
	{
		return this.x509KeyIdentifiers;
	}
	
	Element toDom(Document document)
	{
		Element x509DataElement;
		X509KeyIdentifier nextX509KeyIdentifier;
		Element nextX509KeyIdentifierElement;
		
		x509DataElement = null;
		nextX509KeyIdentifier = null;
		nextX509KeyIdentifierElement = null;
		
		x509DataElement = document.createElementNS(SignatureConstants.DS_NS, "ds:X509Data");
		
		for(int i = 0; i < getX509KeyIdentifiers().size(); i++)
		{
			nextX509KeyIdentifier = getX509KeyIdentifiers().get(i);
			nextX509KeyIdentifierElement = nextX509KeyIdentifier.toDom(document);
			x509DataElement.appendChild(nextX509KeyIdentifierElement);
		}
		
		return x509DataElement;
	}
}