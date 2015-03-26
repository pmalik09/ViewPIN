package com.safenetinc.viewpin.authority;

public class PinChangeData {


	private String cardHolderVerificationValue = null;
    
	private String expiryDate = null;
    
    private String primaryAccountNumber = null;
    
    private String oldPin = null;

    private String newPin = null;
    
    public static final String INDENT = "  ";
    
    
    public PinChangeData()
    {
    	this.cardHolderVerificationValue = null;
        
    	this.expiryDate = null;
        
        this.primaryAccountNumber = null;
        
        this.oldPin = null;

        this.newPin = null;
    }
    
    public PinChangeData(String cardHolderVerificationValue,
    		String expiryDate, String primaryAccountNumber, String oldPin,String newPin)
        {
        	super();
        	
        	setCardHolderVerificationValue(cardHolderVerificationValue);
        	setExpiryDate(expiryDate);
        	setPrimaryAccountNumber(primaryAccountNumber);
        	setOldPin(oldPin);
        	setNewPin(newPin);        	
        }
        
    public void setCardHolderVerificationValue(String cardHolderVerificationValue)
    {
        this.cardHolderVerificationValue = cardHolderVerificationValue;
    }

    /**
     * @return The CVV
     */
    public String getCardHolderVerificationValue()
    {
        return this.cardHolderVerificationValue;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    /**
     * @return The expiry date
     */
    public String getExpiryDate()
    {
        return this.expiryDate;
    }

    public void setPrimaryAccountNumber(String primaryAccountNumber)
    {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    /**
     * @return The PAN
     */
    public String getPrimaryAccountNumber()
    {
        return this.primaryAccountNumber;
    }

    public void setOldPin(String oldPin)
    {
        this.oldPin = oldPin;
    }

    /**
     * @return The old Card Pin
     */
    public String getOldPin()
    {
        return this.oldPin;
    }

    
    public void setNewPin(String newPin)
    {
        this.newPin = newPin;
    }

    /**
     * @return The new Card Pin
     */
    public String getNewPin()
    {
        return this.newPin;
    }
    
    public String toString(PinChangeData pinChangeData)
    {
    	   StringBuffer buffer = new StringBuffer();

    	   buffer.append(INDENT);
    	   buffer.append("CardHolderVerificationValue: ");
    	   buffer.append(pinChangeData.getCardHolderVerificationValue());
    	   
    	   buffer.append(INDENT);
    	   buffer.append("ExpiryDate: ");
    	   buffer.append(pinChangeData.getExpiryDate());
    	   
    	   buffer.append(INDENT);
    	   buffer.append("PrimaryAccountNumber: ");
    	   buffer.append(pinChangeData.getPrimaryAccountNumber());
    	   
    	   buffer.append(INDENT);
    	   buffer.append("OldPin: ");
    	   buffer.append(pinChangeData.getOldPin());
    	   
    	   buffer.append(INDENT);
    	   buffer.append("NewPin: ");
    	   buffer.append(pinChangeData.getNewPin());
    	   
    	return buffer.toString() ;
    }
	
}
