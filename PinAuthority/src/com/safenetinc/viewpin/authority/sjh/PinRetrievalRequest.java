package com.safenetinc.viewpin.authority.sjh;

import com.safenetinc.viewpin.authority.ExpiryDate;
import com.safenetinc.viewpin.authority.PrimaryAccountNumber;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.CardVerificationValue;

public class PinRetrievalRequest 
{
	private CardVerificationValue cardVerificationValue = null;
	private PrimaryAccountNumber primaryAccountNumber = null;
	private ExpiryDate expiryDate = null;
	
	public PinRetrievalRequest(CardVerificationValue cardVerificationValue,
		PrimaryAccountNumber primaryAccountNumber, ExpiryDate expiryDate)
	{
		super();
		
		setCardVerificationValue(cardVerificationValue);
		setPrimaryAccountNumber(primaryAccountNumber);
		setExpiryDate(expiryDate);
	}
	
	private void setCardVerificationValue(CardVerificationValue cardVerificationValue) 
	{
		this.cardVerificationValue = cardVerificationValue;
	}
	
	public CardVerificationValue getCardVerificationValue() 
	{
		return this.cardVerificationValue;
	}

	private void setPrimaryAccountNumber(PrimaryAccountNumber primaryAccountNumber) 
	{
		this.primaryAccountNumber = primaryAccountNumber;
	}
	
	public PrimaryAccountNumber getPrimaryAccountNumber() 
	{
		return this.primaryAccountNumber;
	}

	private void setExpiryDate(ExpiryDate expiryDate) 
	{
		this.expiryDate = expiryDate;
	}
	
	public ExpiryDate getExpiryDate() 
	{
		return this.expiryDate;
	}
}
