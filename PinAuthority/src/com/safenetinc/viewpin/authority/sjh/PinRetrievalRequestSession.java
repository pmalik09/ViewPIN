package com.safenetinc.viewpin.authority.sjh;

import javax.crypto.SecretKey;

import org.w3c.dom.Document;

public class PinRetrievalRequestSession
{
	private Document pinRetrievalRequestDocument = null;
	private PinRetrievalRequest pinRetrievalRequest = null;
	private SecretKey sessionKey = null;
	private String sessionEncrytionMethodAlgorithm = null;
	
	public PinRetrievalRequestSession(Document pinRetrievalRequestDocument, PinRetrievalRequest pinRetrievalRequest, SecretKey sessionKey, String sessionEncrytionMethodAlgorithm)
	{
		super();
		
		setPinRetrievalRequestDocument(pinRetrievalRequestDocument);
		setPinRetrievalRequest(pinRetrievalRequest);
		setSessionKey(sessionKey);
		setSessionEncrytionMethodAlgorithm(sessionEncrytionMethodAlgorithm);
	}

	public void setSessionKey(SecretKey sessionKey) 
	{
		this.sessionKey = sessionKey;
	}
	
	public SecretKey getSessionKey() 
	{
		return this.sessionKey;
	}
	
	private void setSessionEncrytionMethodAlgorithm(String sessionEncrytionMethodAlgorithm) 
	{
		this.sessionEncrytionMethodAlgorithm = sessionEncrytionMethodAlgorithm;
	}
	
	public String getSessionEncrytionMethodAlgorithm() 
	{
		return this.sessionEncrytionMethodAlgorithm;
	}
	
	private void setPinRetrievalRequestDocument(Document pinRetrievalRequestDocument) 
	{
		this.pinRetrievalRequestDocument = pinRetrievalRequestDocument;
	}
	
	public Document getPinRetrievalRequestDocument() 
	{
		return this.pinRetrievalRequestDocument;
	}
	
	private void setPinRetrievalRequest(PinRetrievalRequest pinRetrievalRequest) 
	{
		this.pinRetrievalRequest = pinRetrievalRequest;
	}
	
	public PinRetrievalRequest getPinRetrievalRequest()
	{
		return this.pinRetrievalRequest;
	}
}
