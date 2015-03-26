package com.safenetinc.viewpin.banksimulator;
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
