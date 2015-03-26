// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/exceptions/InvalidPrimaryAccountNumberException.java 1.1 2008/09/04 10:49:37IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

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