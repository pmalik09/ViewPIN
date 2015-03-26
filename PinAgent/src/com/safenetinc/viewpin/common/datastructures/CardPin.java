// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/CardPin.java 1.5 2009/01/21 08:29:15IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.common.datastructures;

import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidCardPinException;


/**
 * Class to store the PIN associated with a card
 * @author Stuart Horler
 *
 *
 */
public class CardPin
{
    private String pin = null;
    private String primaryAccountNumber = null;
    
    private static final String PRIMARY_ACCOUNT_NUMBER_REGEX = "^[0-9]{16}$";
	private static final String PIN_REGEX = "^[0-9]{4,12}$";
	
    /**
     * Constructor
     * @param pin The PIN to store
     * @param primaryAccountNumber the PAN the PIN is associated with
     */
    public CardPin(String pin)throws InvalidCardPinException
    {
    	
        super();
        if (pin == null)
        {
            throw new InvalidCardPinException("Pin is null");
        }
/*
        if (primaryAccountNumber == null)
        {
            throw new InvalidCardPinException("PAN is null");
        }
  */      
        if (pin.matches(PIN_REGEX) == false)
        {
            throw new InvalidCardPinException("PIN format invalid");
        }
        /*
        if (primaryAccountNumber.matches(PRIMARY_ACCOUNT_NUMBER_REGEX) == false)
        {
            throw new InvalidCardPinException("PAN format invalid");
        }
          */      
        setPin(pin);
      //  setPrimaryAccountNumber(primaryAccountNumber);
        
    }

    private void setPin(String pin)
    {
        this.pin = pin;
    }

    /**
     * @return The PIN held by this class
     */
    public String getPin()
    {
        return this.pin;
    }
/*
    private void setPrimaryAccountNumber(String primaryAccountNumber)
    {
        this.primaryAccountNumber = primaryAccountNumber;
    }
*/
    /**
     * @return The PAN held by this class
     */
	 /*
    public String getPrimaryAccountNumber()
    {
        return this.primaryAccountNumber;
    }*/
    
    /**
     * @return 1 if both objects are equal else 0 
     */
    public boolean equals(CardPin cardPin) throws InvalidCardPinException
    {   	
    	//Check if valid Object is passed
    	if(cardPin == null || (cardPin instanceof CardPin) == false )
    	{
    		throw new InvalidCardPinException("Null or Invalid object passed");
    	}
    	
    	if(this.getPin().equals(cardPin.getPin())) 
    	{
    		return true;   	   
    	}
    	else
    	{
    		return false;
    	}
    }
}