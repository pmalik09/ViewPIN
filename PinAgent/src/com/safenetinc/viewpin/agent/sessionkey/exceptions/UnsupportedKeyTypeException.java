package com.safenetinc.viewpin.agent.sessionkey.exceptions;

/**
 * Exception class to represent an unsupported  key type exception
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class UnsupportedKeyTypeException extends Exception
{
	private static final long serialVersionUID = 42L;

	/**
	 * Constructor
	 */
    public UnsupportedKeyTypeException()
    {
        super();
    }

    /**
     * Constructor
     * @param message The message associated with this exception
     */
    public UnsupportedKeyTypeException(String message)
    {
        super(message);
    }
}