// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinChangeRequest.java 1.1 2008/09/05 15:59:46IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent;

import org.w3c.dom.Document;


/**
 * Class to hold a PINChangeRequest document
 * 
 * @author Manmeet Singh
 *
 */
public class PinChangeRequest
{
    private Document pinChangeRequestDocument = null;
    private String encodedCompressedPinChangeRequestDocument = null;
    
    /**
     * Constructor
     * @param pinChangeRequestDocument The PINChange request document
     * @param agentWrappedSessionKey The session key associated with the document
     * @param encodedCompressedPinChangeRequestDocument The encoded, compressed version of the document
     */
    public PinChangeRequest(Document pinChangeRequestDocument,String encodedCompressedPinChangeRequestDocument)
    {
    	super();
    	
    	setPinChangeRequestDocument(pinChangeRequestDocument);
    	setEncodedCompressedPinChangeRequestDocument(encodedCompressedPinChangeRequestDocument);
    }

	private void setPinChangeRequestDocument(Document pinChangeRequestDocument)
	{
		this.pinChangeRequestDocument = pinChangeRequestDocument;
	}
    
	/**
	 * @return The PINChangeRequest document
	 */
	public Document getPinChangeRequestDocument() 
	{
		return this.pinChangeRequestDocument;
	}
	
	private void setEncodedCompressedPinChangeRequestDocument(String encodedCompressedPinChangeRequestDocument) 
	{
		this.encodedCompressedPinChangeRequestDocument = encodedCompressedPinChangeRequestDocument;
	}

	/**
	 * @return the PINChangeRequest document in encoded, compressed form
	 */
	public String getEncodedCompressedPinChangeRequestDocument() 
	{
		return this.encodedCompressedPinChangeRequestDocument;
	}
}