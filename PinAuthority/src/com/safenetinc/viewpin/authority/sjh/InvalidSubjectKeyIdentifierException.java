// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/sjh/InvalidSubjectKeyIdentifierException.java 1.1 2012/07/19 11:23:29IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.sjh;

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