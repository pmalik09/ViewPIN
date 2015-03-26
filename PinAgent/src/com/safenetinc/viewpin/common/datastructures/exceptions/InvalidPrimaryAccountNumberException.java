// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/exceptions/InvalidPrimaryAccountNumberException.java 1.1 2008/09/04 10:46:58IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures.exceptions;

/**
 * Exception class to represent an invalid PAN exception
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class InvalidPrimaryAccountNumberException extends Exception 
{
	private static final long serialVersionUID = 42L;

	/**
	 * Constructor
	 */
	public InvalidPrimaryAccountNumberException() 
	{
		super();
	}

	/**
     * Constructor
     * @param message The message associated with this exception
     */
	public InvalidPrimaryAccountNumberException(String message) 
	{
		super(message);
	}
}