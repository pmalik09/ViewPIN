package com.safenetinc.luna.sp.throttle;

import java.util.ArrayList;

public class ThreadPoolThresholds
{
	private ArrayList<ThreadPoolThreshold> threadPoolThresholds = null;
	
    public ThreadPoolThresholds()
    {
    	super();
    	
    	setThreadPoolThresholds(new ArrayList<ThreadPoolThreshold>());
    }
    
    public void addThreadPoolThreshold(ThreadPoolThreshold threadPoolThreshold)
    {
    	getThreadPoolThresholds().add(threadPoolThreshold);
    }
    
    public ThreadPoolThreshold getThreadPoolThreshold(String threadPoolName)
    {
    	ThreadPoolThreshold threadPoolThreshold;
    	ThreadPoolThreshold nextThreadPoolThreshold;
    	
    	threadPoolThreshold = null;
    	nextThreadPoolThreshold = null;
    	
    	// Work through all thread pool thresholds
    	for(int i = 0; i < getThreadPoolThresholds().size(); i++)
    	{
    		// Get next thread pool threshold
    		nextThreadPoolThreshold = (ThreadPoolThreshold)getThreadPoolThresholds().get(i);
    		
    		// Is this the thread pool threshold we were looking for?
    		if(nextThreadPoolThreshold.getThreadPoolName().equals(threadPoolName) == true)
    		{
    			// We found the thread pool threshold we were looking for
    			threadPoolThreshold = nextThreadPoolThreshold;
    			
    			break;
    		}
    	}
    	
    	return threadPoolThreshold;
    }

	private ArrayList<ThreadPoolThreshold> getThreadPoolThresholds() 
	{
		return this.threadPoolThresholds;
	}

	private void setThreadPoolThresholds(ArrayList<ThreadPoolThreshold> threadPoolThresholds)
	{
		this.threadPoolThresholds = threadPoolThresholds;
	}
}