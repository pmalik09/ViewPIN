// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/InvalidPrimaryAccountNumberException.java 1.1 2008/09/15 11:03:52IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an invalid PAN is specified
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
     * 
     * @param message The message associated with this exception
     */
    public InvalidPrimaryAccountNumberException(String message)
    {
        super(message);
    }
}