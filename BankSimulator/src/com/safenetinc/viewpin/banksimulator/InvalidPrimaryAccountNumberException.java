// $Id: BankSimulator/src/com/safenetinc/viewpin/banksimulator/InvalidPrimaryAccountNumberException.java 1.1 2012/07/19 11:28:41IST Malik, Pratibha (Pmalik) Exp  $
/**
 * Exception to be thrown when an invalid PAN is specified
 * 
 * @author Stuart Horler
 * 
 * 
 */
package com.safenetinc.viewpin.banksimulator;
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