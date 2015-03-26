package com.safenetinc.syslog;

public class InvalidContentException extends Exception 
{
	private static final long serialVersionUID = 42L;

	public InvalidContentException() 
	{
		super();
	}

	public InvalidContentException(String message) 
	{
		super(message);
	}
}