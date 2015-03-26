// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/exceptions/InvalidSubjectKeyIdentifierException.java 1.1 2008/09/04 10:47:00IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures.exceptions;

/**
 * Exception class to represent an invalid SKI exception
 * 
 * @author Stuart Horler
 *
 *
 */
public class InvalidSubjectKeyIdentifierException extends Exception
{
	private static final long serialVersionUID = 42L;

	/**
	 * Constructor
	 */
	public InvalidSubjectKeyIdentifierException() 
	{
		super();
	}

	/**
     * Constructor
     * @param message The message associated with this exception
     */
	public InvalidSubjectKeyIdentifierException(String message) 
	{
		super(message);
	}
}