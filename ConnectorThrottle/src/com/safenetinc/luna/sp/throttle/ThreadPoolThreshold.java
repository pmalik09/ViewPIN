package com.safenetinc.luna.sp.throttle;

public class ThreadPoolThreshold
{
	private String threadPoolName = null;
	private int threshold = 0;
	
    public ThreadPoolThreshold(String threadPoolName, int threshold)
    {
    	super();
    	
    	setThreadPoolName(threadPoolName);
    	setThreshold(threshold);
    }

	public String getThreadPoolName() 
	{
		return this.threadPoolName;
	}

	private void setThreadPoolName(String threadPoolName)
	{
		this.threadPoolName = threadPoolName;
	}

	public int getThreshold() 
	{
		return this.threshold;
	}

	private void setThreshold(int threshold) 
	{
		this.threshold = threshold;
	}
}