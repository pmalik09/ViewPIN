package com.safenetinc.viewpin.banksimulator;

public class InvalidPinException extends Exception 
{
	private static final long serialVersionUID = 42L;

	public InvalidPinException()
	{
		super();
	}
	
	public InvalidPinException(String message)
	{
		super(message);
	}
}
