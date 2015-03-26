// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/authority/exceptions/ViewPinResponseException.java 1.1 2008/09/15 13:45:15IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an error associated with the PINRetrievalResponse occurs
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class ViewPinResponseException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public ViewPinResponseException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public ViewPinResponseException(String message)
    {
        super();
    }
}
