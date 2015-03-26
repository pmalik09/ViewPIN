package com.safenetinc.viewpin.banksimulator;

public class RetrievePinServiceRequest 
{
	private String encodedCompressedPinRetrievalRequestDocument = null;
	private String primaryAccountNumber = null;
	private String expiryDate = null;
	private String cardVerificationKeyPairName = null;
	private CardPin[] cardPins = null;
	private String serviceCode = null;
	private boolean enforceExpiryDateAuthentication = false;
	private boolean authenticateCardVerificationValueOnly = false;
	
	public RetrievePinServiceRequest()
	{
		super();
	}
	
	public String getEncodedCompressedPinRetrievalRequestDocument() 
	{
		return encodedCompressedPinRetrievalRequestDocument;
	}

	public void setEncodedCompressedPinRetrievalRequestDocument(String encodedCompressedPinRetrievalRequestDocument) 
	{
		this.encodedCompressedPinRetrievalRequestDocument = encodedCompressedPinRetrievalRequestDocument;
	}
	
	public void setPrimaryAccountNumber(String primaryAccountNumber) 
	{
		this.primaryAccountNumber = primaryAccountNumber;
	}
	
	public String getPrimaryAccountNumber() 
	{
		return this.primaryAccountNumber;
	}

	public void setExpiryDate(String expiryDate) 
	{
		this.expiryDate = expiryDate;
	}
	
	public String getExpiryDate() 
	{
		return this.expiryDate;
	}
	
	public void setCardVerificationKeyPairName(String cardVerificationKeyPairName) 
	{
		this.cardVerificationKeyPairName = cardVerificationKeyPairName;
	}
	
	public String getCardVerificationKeyPairName() 
	{
		return this.cardVerificationKeyPairName;
	}

	public void setCardPins(CardPin[] cardPins) 
	{
		this.cardPins = cardPins;
	}
	
	public CardPin[] getCardPins() 
	{
		return this.cardPins;
	}

	public void setServiceCode(String serviceCode) 
	{
		this.serviceCode = serviceCode;
	}
	
	public String getServiceCode()
	{
		return this.serviceCode;
	}
	
	public void setEnforceExpiryDateAuthentication(boolean enforceExpiryDateAuthentication)
	{
		this.enforceExpiryDateAuthentication = enforceExpiryDateAuthentication;
	}
	
	public boolean isEnforceExpiryDateAuthentication() 
	{
		return this.enforceExpiryDateAuthentication;
	}
	
	public void setAuthenticateCardVerificationValueOnly(boolean authenticateCardVerificationValueOnly)
	{
		this.authenticateCardVerificationValueOnly = authenticateCardVerificationValueOnly;
	}
	
	public boolean isAuthenticateCardVerificationValueOnly()
	{
		return this.authenticateCardVerificationValueOnly;
	}
}
