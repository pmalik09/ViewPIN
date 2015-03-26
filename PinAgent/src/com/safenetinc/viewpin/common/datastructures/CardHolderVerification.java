// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/CardHolderVerification.java 1.1 2008/09/04 10:46:45IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures;


/**
 * Class to managed verification of a card holder
 * @author Stuart Horler
 *
 *
 */
public class CardHolderVerification 
{
	private CardHolderVerificationValue cardHolderVerificationValue = null;
    private ExpiryDate expiryDate = null;
    private PrimaryAccountNumber primaryAccountNumber = null;
    
    /**
     * Constructor
     * @param cardHolderVerificationValue The CVV value
     * @param expiryDate The expiry date
     * @param primaryAccountNumber The PAN
     */
    public CardHolderVerification(CardHolderVerificationValue cardHolderVerificationValue,
        ExpiryDate expiryDate, PrimaryAccountNumber primaryAccountNumber)
    {
    	super();
    	
    	setCardHolderVerificationValue(cardHolderVerificationValue);
    	setExpiryDate(expiryDate);
    	setPrimaryAccountNumber(primaryAccountNumber);
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
}