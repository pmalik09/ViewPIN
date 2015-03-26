package com.safenetinc.viewpin.agent.sessionkey.exceptions;

/**
 * Exception class to represent an invalid session key length exception
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class InvalidSessionKeyLengthException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
	public InvalidSessionKeyLengthException()
    {
    	super();
    }
	
	/**
     * Constructor
     * @param message The message associated with this exception
     */
	public InvalidSessionKeyLengthException(String message)
    {
    	super(message);
    }
}
