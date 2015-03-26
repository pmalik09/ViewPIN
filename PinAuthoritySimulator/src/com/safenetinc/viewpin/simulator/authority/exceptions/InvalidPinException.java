// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/exceptions/InvalidPinException.java 1.1 2008/09/04 10:49:35IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

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
