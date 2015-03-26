// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/authority/exceptions/CardHolderAuthenticationException.java 1.1 2008/09/15 13:44:53IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

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
