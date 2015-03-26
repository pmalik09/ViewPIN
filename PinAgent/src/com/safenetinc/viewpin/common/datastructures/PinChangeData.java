
package com.safenetinc.viewpin.common.datastructures;

public class PinChangeData {

	private CardHolderVerificationValue cardHolderVerificationValue = null;
    private ExpiryDate expiryDate = null;
    private PrimaryAccountNumber primaryAccountNumber = null;
    private CardPin oldPin = null;
    private CardPin newPin = null;
    
    public PinChangeData(CardHolderVerificationValue cardHolderVerificationValue,
            ExpiryDate expiryDate, PrimaryAccountNumber primaryAccountNumber, CardPin oldPin,CardPin newPin)
        {
        	super();
        	
        	setCardHolderVerificationValue(cardHolderVerificationValue);
        	setExpiryDate(expiryDate);
        	setPrimaryAccountNumber(primaryAccountNumber);
        	setOldPin(oldPin);
        	setNewPin(newPin);        	
        }
        
    private void setCardHolderVerificationValue(CardHolderVerificationValue cardHolderVerificationValue)
    {
        this.cardHolderVerificationValue = cardHolderVerificationValue;
    }

    /**
     * @return The CVV
     */
    public CardHolderVerificationValue getCardHolderVerificationValue()
    {
        return this.cardHolderVerificationValue;
    }

    private void setExpiryDate(ExpiryDate expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    /**
     * @return The expiry date
     */
    public ExpiryDate getExpiryDate()
    {
        return this.expiryDate;
    }

    private void setPrimaryAccountNumber(PrimaryAccountNumber primaryAccountNumber)
    {
        this.primaryAccountNumber = primaryAccountNumber;
    }

    /**
     * @return The PAN
     */
    public PrimaryAccountNumber getPrimaryAccountNumber()
    {
        return this.primaryAccountNumber;
    }

    private void setOldPin(CardPin oldPin)
    {
        this.oldPin = oldPin;
    }

    /**
     * @return The old Card Pin
     */
    public CardPin getOldPin()
    {
        return this.oldPin;
    }

    private void setNewPin(CardPin newPin)
    {
        this.newPin = newPin;
    }

    /**
     * @return The new Card Pin
     */
    public CardPin getNewPin()
    {
        return this.newPin;
    }
}
