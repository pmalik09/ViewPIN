// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/exceptions/CardAccountLockedException.java 1.1 2008/09/04 10:38:40IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

/**
 * Exception to be thrown when a customer's account is locked.
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class CardAccountLockedException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public CardAccountLockedException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public CardAccountLockedException(String message)
    {
        super(message);
    }
}
