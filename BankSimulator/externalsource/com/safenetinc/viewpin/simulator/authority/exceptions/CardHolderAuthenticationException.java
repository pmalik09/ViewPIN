// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/exceptions/CardHolderAuthenticationException.java 1.1 2008/09/04 10:38:41IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority.exceptions;

/**
 * Exception to be thrown when a problem has occurred authenticating a customer
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class CardHolderAuthenticationException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public CardHolderAuthenticationException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public CardHolderAuthenticationException(String message)
    {
        super(message);
    }
}
