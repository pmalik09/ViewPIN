// $Id: PinAgent/src/com/safenetinc/viewpin/agent/exceptions/ViewPinRequestException.java 1.1 2008/09/05 16:07:24IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.exceptions;

/**
 * Exception to be thrown in event of a problem with generating the PINRetrievalRequest
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
     * @param message The message associated with this exception
     */
    public ViewPinRequestException(String message)
    {
    	super(message);
    }
}
