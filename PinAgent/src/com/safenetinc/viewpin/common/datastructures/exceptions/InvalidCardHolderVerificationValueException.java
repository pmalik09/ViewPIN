// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/exceptions/InvalidCardHolderVerificationValueException.java 1.1 2008/09/04 10:46:54IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures.exceptions;

/**
 * Exception class to represent an invalid CVV exception
 * 
 * @author Stuart Horler
 *
 *
 */
public class InvalidCardHolderVerificationValueException extends Exception
{
	private static final long serialVersionUID = 42L;

	/**
	 * Constructor
	 */
	public InvalidCardHolderVerificationValueException()
	{
		super();
	}

	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
	public InvalidCardHolderVerificationValueException(String message)
	{
		super(message);
	}
}