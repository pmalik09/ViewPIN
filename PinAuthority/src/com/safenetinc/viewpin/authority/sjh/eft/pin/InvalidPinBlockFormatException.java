package com.safenetinc.viewpin.authority.sjh.eft.pin;
public class InvalidPinBlockFormatException extends Exception 
{
	private static final long serialVersionUID = 42L;
	
	public InvalidPinBlockFormatException()
	{
		super();
	}
	
	public InvalidPinBlockFormatException(String message)
	{
		super(message);
	}
}
