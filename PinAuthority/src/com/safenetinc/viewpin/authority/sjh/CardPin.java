package com.safenetinc.viewpin.authority.sjh;

public class CardPin 
{
	private String primaryAccountNumber = null;
	private String encodedEncryptedPinBlock = null;
	private String zonePinKeyName = null;
	
	public CardPin()
	{
		super();
	}
	
	public CardPin(String primaryAccountNumber, String encodedEncryptedPinBlock, String zonePinKeyName)
	{
		super();
		
		setPrimaryAccountNumber(primaryAccountNumber);
		setEncodedEncryptedPinBlock(encodedEncryptedPinBlock);
		setZonePinKeyName(zonePinKeyName);
	}

	public void setPrimaryAccountNumber(String primaryAccountNumber) 
	{
		this.primaryAccountNumber = primaryAccountNumber;
	}
	
	public String getPrimaryAccountNumber() 
	{
		return this.primaryAccountNumber;
	}
	
	public void setEncodedEncryptedPinBlock(String encodedEncryptedPinBlock)
	{
		this.encodedEncryptedPinBlock = encodedEncryptedPinBlock;
	}
	
	public String getEncodedEncryptedPinBlock() 
	{
		return this.encodedEncryptedPinBlock;
	}
	
	public void setZonePinKeyName(String zonePinKeyName) 
	{
		this.zonePinKeyName = zonePinKeyName;
	}
	
	public String getZonePinKeyName() 
	{
		return this.zonePinKeyName;
	}
}
