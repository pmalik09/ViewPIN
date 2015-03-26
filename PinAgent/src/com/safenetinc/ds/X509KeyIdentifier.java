// $Id: PinAgent/src/com/safenetinc/ds/X509KeyIdentifier.java 1.1 2008/09/04 10:45:34IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstract class. Represents a key identifier
 * @author Stuart Horler
 *
 *
 */
public abstract class X509KeyIdentifier 
{
    /**
     * Constructor
     *
     */
	public X509KeyIdentifier()
	{
		super();
	}
	
	abstract Element toDom(Document document);
}
