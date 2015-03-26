// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/authority/exceptions/ViewPinRequestException.java 1.1 2008/09/15 13:45:09IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.exceptions;

/**
 * Exception to be thrown when an error associated with a PINRetrievalRequest occurs
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class ViewPinRequestException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public ViewPinRequestException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public ViewPinRequestException(String message)
    {
        super();
    }
}
