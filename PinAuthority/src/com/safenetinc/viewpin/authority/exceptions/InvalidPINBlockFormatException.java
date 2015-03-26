// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/InvalidPINBlockFormatException.java 1.1 2008/10/20 10:25:00IST Achaudhary Exp  $
package com.safenetinc.viewpin.authority.exceptions;
/**
 * Exception to be thrown in event if requested PIN Block format is invalid/un-supported
 * 
 * @author Anurag Chaudhary
 *
 *
 */
public class InvalidPINBlockFormatException extends Exception
{
   private static final long serialVersionUID = 42L;

   /**
    * Constructor
    */
	public InvalidPINBlockFormatException()
    {
    	super();
    }
    
	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
    public InvalidPINBlockFormatException(String message)
    {
    	super(message);
    }
}