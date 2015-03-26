// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/sjh/eft/cvv/InvalidCardVerificationValueException.java 1.1 2012/07/19 11:23:57IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.sjh.eft.cvv;

/**
 * Exception class to represent an invalid CVV exception
 * 
 * @author Stuart Horler
 *
 *
 */
public class InvalidCardVerificationValueException extends Exception
{
	private static final long serialVersionUID = 42L;

	/**
	 * Constructor
	 */
	public InvalidCardVerificationValueException()
	{
		super();
	}

	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
	public InvalidCardVerificationValueException(String message)
	{
		super(message);
	}
}