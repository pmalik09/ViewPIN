package com.safenetinc.viewpin.agent.sessionkey.exceptions;

/**
 * Exception class to represent an unsupported padding scheme exception
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class UnsupportedPaddingSchemeException extends Exception
{
    private static final long serialVersionUID = 42L;

    /**
     * Constructor
     */
    public UnsupportedPaddingSchemeException()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param message The message associated with this exception
     */
    public UnsupportedPaddingSchemeException(String message)
    {
        super(message);
    }
}