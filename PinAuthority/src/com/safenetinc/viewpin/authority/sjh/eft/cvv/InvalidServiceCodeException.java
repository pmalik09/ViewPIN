package com.safenetinc.viewpin.authority.sjh.eft.cvv;

public class InvalidServiceCodeException extends Exception
{
	private static final long serialVersionUID = 42L;

	public InvalidServiceCodeException()
	{
		super();
	}
	
	public InvalidServiceCodeException(String message)
	{
		super(message);
	}
}
