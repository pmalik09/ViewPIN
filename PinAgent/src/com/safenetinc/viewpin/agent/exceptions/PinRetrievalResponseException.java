// $Id: PinAgent/src/com/safenetinc/viewpin/agent/exceptions/PinRetrievalResponseException.java 1.1 2008/09/04 10:46:13IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.exceptions;

/**
 * Exception to be thrown in event of a problem with parsing the PINRetrievalResponse
 * 
 * @author Stuart Horler
 *
 *
 */
public class PinRetrievalResponseException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public PinRetrievalResponseException()
    {
        super();
    }
    
    /**
     * Constructor
     * @param message The message associated with this exception
     */
    public PinRetrievalResponseException(String message)
    {
        super(message);
    }
}
