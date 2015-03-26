// $Id: PinAuthority/src/com/safenetinc/viewpin/authority/sjh/ViewPinRequestException.java 1.1 2012/07/19 11:23:46IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.authority.sjh;

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
        super(message);
    }
}
