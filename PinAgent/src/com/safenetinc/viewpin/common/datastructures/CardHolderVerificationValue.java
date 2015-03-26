// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/CardHolderVerificationValue.java 1.1 2008/09/04 10:46:46IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures;

import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidCardHolderVerificationValueException;

/**
 * Class to manage CVVs
 * 
 * @author Stuart Horler
 *
 *
 */
public class CardHolderVerificationValue
{
	private static final String CVV_REGEX = "^[0-9]{3}$";
	
	private String cardHolderVerificationValue = null;

	/**
	 * Constructor
	 * @param cardHolderVerificationValue String containing the CVV this class is to store
	 * @throws InvalidCardHolderVerificationValueException Thrown if an invalid CVV String is specified
	 */
	public CardHolderVerificationValue(String cardHolderVerificationValue) throws InvalidCardHolderVerificationValueException 
	{
		super();

		// Ensure card holder verification value is valid
		validate(cardHolderVerificationValue);

		setCardHolderVerificationValue(cardHolderVerificationValue);
	}

	private void validate(@SuppressWarnings("hiding") String cardHolderVerificationValue) throws InvalidCardHolderVerificationValueException
	{
		// Ensure card holder verification value is not null
		if(cardHolderVerificationValue == null)
		{
			// Card holder verification value is null
			throw new InvalidCardHolderVerificationValueException("is null");
		}

		// Ensure card holder verification value is in the correct format
		if(cardHolderVerificationValue.matches(CVV_REGEX) == false)
		{
			throw new InvalidCardHolderVerificationValueException("invalid format");
		}
	}

	private void setCardHolderVerificationValue(String cardHolderVerificationValue)
	{
		this.cardHolderVerificationValue = cardHolderVerificationValue;
	}

	/**
	 * @return The CVV held by this class
	 */
	public String getCardHolderVerificationValue()
	{
		return this.cardHolderVerificationValue;
	}
}