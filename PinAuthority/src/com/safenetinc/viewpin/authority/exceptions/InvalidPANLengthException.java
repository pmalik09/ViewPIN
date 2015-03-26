// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/InvalidPANLengthException.java 1.1 2008/10/20 10:24:57IST Achaudhary Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown in event of a PAN too long ot too short
 * 
 * @author Anurag Chaudhary
 *
 *
 */
public class InvalidPANLengthException extends Exception
{
   private static final long serialVersionUID = 42L;

   /**
    * Constructor
    */
	public InvalidPANLengthException()
    {
    	super();
    }
    
	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
    public InvalidPANLengthException(String message)
    {
    	super(message);
    }
}