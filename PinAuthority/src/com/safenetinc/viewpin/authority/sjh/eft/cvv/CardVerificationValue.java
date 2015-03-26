// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/sjh/eft/cvv/CardVerificationValue.java 1.2 2013/04/12 17:18:37IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.sjh.eft.cvv;

import org.apache.log4j.Logger;

/**
 * Class to manage CVVs
 * 
 * @author Stuart Horler
 *
 *
 */
public class CardVerificationValue
{
	private static final String CVV_REGEX = "^[0-9]{3}$";
	
	private String cardVerificationValue = null;

	
	/**
	 * Constructor
	 * @param cardVerificationValue String containing the CVV this class is to store
	 * @throws InvalidCardVerificationValueException Thrown if an invalid CVV String is specified
	 */
	public CardVerificationValue(String cardVerificationValue) throws InvalidCardVerificationValueException 
	{
		super();

		// Ensure card verification value is valid
		// validate(cardVerificationValue);

		setCardVerificationValue(cardVerificationValue);
	}

	private void validate(String cardVerificationValue) throws InvalidCardVerificationValueException
	{
		
		// Ensure card verification value is not null
		if(cardVerificationValue == null)
		{
			// Card verification value is null
			throw new InvalidCardVerificationValueException("is null");
		}

		// Ensure card verification value is in the correct format
		if(cardVerificationValue.matches(CVV_REGEX) == false)
		{
			throw new InvalidCardVerificationValueException("invalid format");
		}
	}

	private void setCardVerificationValue(String cardVerificationValue)
	{
		this.cardVerificationValue = cardVerificationValue;
	}

	/**
	 * @return The CVV held by this class
	 */
	public String getCardVerificationValue()
	{
		return this.cardVerificationValue;
	}
	
	
}