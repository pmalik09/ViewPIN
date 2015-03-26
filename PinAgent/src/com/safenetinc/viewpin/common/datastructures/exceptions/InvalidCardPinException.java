package com.safenetinc.viewpin.common.datastructures.exceptions;

public class InvalidCardPinException extends Exception {
	    private static final long serialVersionUID = 42L;

	    /**
	     * Constructor
	     */
	    public InvalidCardPinException()
	    {
	        super();
	    }

	    /**
	     * Constructor
	     * 
	     * @param message The message associated with this exception
	     */
	    public InvalidCardPinException(String message)
	    {
	        super(message);
	    }
	}

