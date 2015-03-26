// $Id: PinAgent/src/com/safenetinc/viewpin/common/datastructures/exceptions/InvalidExpiryDateException.java 1.1 2008/09/04 10:46:56IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.datastructures.exceptions;

/**
 * Exception class to represent an invalid expiry date exception
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