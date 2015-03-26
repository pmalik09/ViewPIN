// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/DuplicateCardAccountException.java 1.1 2008/09/15 11:03:41IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an attempt to create a duplicate card account is detected
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class DuplicateCardAccountException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public DuplicateCardAccountException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public DuplicateCardAccountException(String message)
    {
        super(message);
    }
}
