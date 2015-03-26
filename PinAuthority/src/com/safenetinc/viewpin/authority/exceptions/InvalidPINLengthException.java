// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/InvalidPINLengthException.java 1.1 2008/10/20 10:25:02IST Achaudhary Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown in event of a PIN too long ot too short
 * 
 * @author Anurag Chaudhary
 *
 *
 */
public class InvalidPINLengthException extends Exception
{
   private static final long serialVersionUID = 42L;

   /**
    * Constructor
    */
	public InvalidPINLengthException()
    {
    	super();
    }
    
	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
    public InvalidPINLengthException(String message)
    {
    	super(message);
    }
}