// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/exceptions/DuplicateCardAccountException.java 1.1 2008/09/04 10:38:42IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

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
