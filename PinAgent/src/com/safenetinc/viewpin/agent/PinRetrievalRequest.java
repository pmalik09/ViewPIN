// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinRetrievalRequest.java 1.1 2008/09/04 10:45:51IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent;

import org.w3c.dom.Document;

import com.safenetinc.viewpin.agent.sessionkey.WrappedSessionKey;

/**
 * Class to hold a PINRetrievalRequest document
 * 
 * @author Stuart Horler
 *
 */
public class PinRetrievalRequest
{
    private Document pinRetrievalRequestDocument = null;
    private WrappedSessionKey agentWrappedSessionKey = null;
    private String encodedCompressedPinRetrievalRequestDocument = null;
    
    /**
     * Constructor
     * @param pinRetrievalRequestDocument The PINRetrieval request document
     * @param agentWrappedSessionKey The session key associated with the document
     * @param encodedCompressedPinRetrievalRequestDocument The encoded, compressed version of the document
     */
    public PinRetrievalRequest(Document pinRetrievalRequestDocument, WrappedSessionKey agentWrappedSessionKey,
        String encodedCompressedPinRetrievalRequestDocument)
    {
    	super();
    	
    	setPinRetrievalRequestDocument(pinRetrievalRequestDocument);
    	setAgentWrappedSessionKey(agentWrappedSessionKey);
    	setEncodedCompressedPinRetrievalRequestDocument(encodedCompressedPinRetrievalRequestDocument);
    }

	private void setPinRetrievalRequestDocument(Document pinRetrievalRequestDocument)
	{
		this.pinRetrievalRequestDocument = pinRetrievalRequestDocument;
	}
    
	/**
	 * @return The PINRetrievalRequest document
	 */
	public Document getPinRetrievalRequestDocument() 
	{
		return this.pinRetrievalRequestDocument;
	}
	
	private void setAgentWrappedSessionKey(WrappedSessionKey agentWrappedSessionKey)
	{
		this.agentWrappedSessionKey = agentWrappedSessionKey;
	}

	/**
	 * @return The wrapped session key
	 */
	public WrappedSessionKey getAgentWrappedSessionKey() 
	{
		return this.agentWrappedSessionKey;
	}
	
	private void setEncodedCompressedPinRetrievalRequestDocument(String encodedCompressedPinRetrievalRequestDocument) 
	{
		this.encodedCompressedPinRetrievalRequestDocument = encodedCompressedPinRetrievalRequestDocument;
	}

	/**
	 * @return the PINRetrievalRequest document in encoded, compressed form
	 */
	public String getEncodedCompressedPinRetrievalRequestDocument() 
	{
		return this.encodedCompressedPinRetrievalRequestDocument;
	}
}