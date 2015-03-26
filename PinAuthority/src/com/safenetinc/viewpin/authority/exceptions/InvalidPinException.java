// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/InvalidPinException.java 1.1 2008/09/15 11:03:49IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an invalid PIN is specified
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class InvalidPinException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public InvalidPinException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public InvalidPinException(String message)
    {
        super(message);
    }
}
