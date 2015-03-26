// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/exceptions/InvalidExpiryDateException.java 1.1 2008/09/04 10:49:32IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

/**
 * Exception to be thrown when an invalid expiry date is specified
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class InvalidExpiryDateException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public InvalidExpiryDateException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public InvalidExpiryDateException(String message)
    {
        super(message);
    }
}
