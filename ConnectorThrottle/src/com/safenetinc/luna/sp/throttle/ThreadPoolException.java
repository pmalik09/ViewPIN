package com.safenetinc.luna.sp.throttle;

public class ThreadPoolException extends Exception
{
	private static final long serialVersionUID = 42L;

	public ThreadPoolException()
    {
    	super();
    }
    
    public ThreadPoolException(String message)
    {
    	super(message);
    }
}
