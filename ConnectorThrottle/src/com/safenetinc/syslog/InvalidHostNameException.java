package com.safenetinc.syslog;

public class InvalidHostNameException extends Exception 
{
	private static final long serialVersionUID = 42L;

	public InvalidHostNameException()
	{
		super();
	}

	public InvalidHostNameException(String message)
	{
		super(message);
	}
}