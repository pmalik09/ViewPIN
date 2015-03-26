package com.safenetinc.viewpin.authority.sjh.eft.pin;

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
