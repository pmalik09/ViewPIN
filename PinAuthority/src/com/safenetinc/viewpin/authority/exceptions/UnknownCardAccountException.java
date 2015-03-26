// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/exceptions/UnknownCardAccountException.java 1.1 2008/09/15 11:03:54IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an unknown card account is specified
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class UnknownCardAccountException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public UnknownCardAccountException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public UnknownCardAccountException(String message)
    {
        super(message);
    }
}
