package com.safenetinc.luna.sp.throttle;

import java.util.ArrayList;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ThreadPools
{
	private static final String CATALINA_DOMAIN = "Catalina";
	private static final String ATTRIBUTE_NAME_CURRENT_THREADS_BUSY = "currentThreadsBusy";
	private static final String ATTRIBUTE_NAME_CURRENT_THREAD_COUNT = "currentThreadCount";
	private static final String ATTRIBUTE_NAME_MIN_SPARE_THREADS = "minSpareThreads";
	private static final String ATTRIBUTE_NAME_MAX_SPARE_THREADS = "maxSpareThreads";
	private static final String ATTRIBUTE_NAME_MAX_THREADS = "maxThreads";
	private static final String ATTRIBUTE_NAME_THREAD_STATUS = "threadStatus";
	
	private MBeanServer managementBeanServer = null;
	
    public ThreadPools() throws ThreadPoolException
    {
    	super();
    	
    	initManagementBeanServer();
    }
    	
	private void initManagementBeanServer() throws ThreadPoolException
    {
		ArrayList<MBeanServer> managementBeanServers;
		
		managementBeanServers = null;
		
		// Get management bean server containing management beans registered under Catalina domain
		managementBeanServers = getManagementBeanServers(CATALINA_DOMAIN);
		
		// Did we find management bean server containing management beans registered under Catalina domain?
		if(managementBeanServers.size() != 1)
		{
			// We did not find management bean server containing management beans registered under Catalina domain
			throw new ThreadPoolException("locating management bean server containing management beans registered under " +
			    CATALINA_DOMAIN + " domain");
		}
			
		// We did find management bean server containing management beans registered under Catalina domain
		setManagementBeanServer((MBeanServer)managementBeanServers.get(0));
	}
	
	public ThreadPool getThreadPool(String threadPoolName)
	{
		ThreadPool threadPool;
		
		threadPool = null;
		
		threadPool = new ThreadPool(threadPoolName);
		
		return threadPool;
	}
	
	private ArrayList<MBeanServer> getManagementBeanServers(String domain)
	{
		ArrayList<MBeanServer> locatedServers;
		ArrayList<MBeanServer> allServers;
		MBeanServer nextServer;
		
		locatedServers = null;
		allServers = null;
		nextServer = null;

		// Instantiate collection to hold management bean servers containing management beans registered under domain
		locatedServers = new ArrayList<MBeanServer>();
		
		// Get all management bean servers
        allServers = MBeanServerFactory.findMBeanServer(null);
       
        // Work through all management bean servers
        for(int i = 0; i < allServers.size(); i++)
        {
        	// Get next management bean server
        	nextServer = (MBeanServer)allServers.get(i);
        	
        	// Does management bean server contain management beans registered under domain?   
        	if(containsManagementBeansRegisteredUnder(nextServer, domain) == true)
        	{
        		// This management bean server does contain management beans registered under domain
        		locatedServers.add(nextServer);
        	}
        }
        
		return locatedServers;
	}
	
	private boolean containsManagementBeansRegisteredUnder(MBeanServer server, String domain)
	{
		boolean contains;
		String[] domains;
		
		contains = false;
		domains = null;
		
		// Get all domains that have been used to register management beans under management bean server
		domains = server.getDomains();
		
		// Did we find any domains that have been used to register management beans under management bean server?
		if(domains == null)
		{
			// We did not find any domains that have been used to register management beans under management bean server
			return false;
		}
		
		// Work through all domains found
		for(int i = 0; i < domains.length; i++)
		{
			// Is this the domain we are looking for?
			if(domains[i].equals(domain) == true)
			{
				// This is the domain we are looking for
				contains = true;
				
				break;
			}
		}
		
		return contains;
	}
	
	private MBeanServer getManagementBeanServer()
	{
		return this.managementBeanServer;
	}

	private void setManagementBeanServer(MBeanServer managementBeanServer)
	{
		this.managementBeanServer = managementBeanServer;
	}

    public class ThreadPool
    {
    	private String name = null;
    	
    	private ThreadPool()
    	{
    		super();
    	}
    	
    	private ThreadPool(String name)
    	{
    		super();
    		
    		setName(name);
    	}
    	
    	public int getCurrentThreadsBusy() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		Integer currentThreadsBusy;
    		
    		currentThreadsBusy = null;
    		
    		currentThreadsBusy = (Integer)getAttribute(ATTRIBUTE_NAME_CURRENT_THREADS_BUSY);
    		
    		return currentThreadsBusy.intValue();
    	}
    	
    	public int getCurrentThreadCount() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		Integer currentThreadCount;
    		
    		currentThreadCount = null;
    		
    		currentThreadCount = (Integer)getAttribute(ATTRIBUTE_NAME_CURRENT_THREAD_COUNT);
    		
    		return currentThreadCount.intValue();
    	}
    	
    	public int getMinSpareThreads() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		Integer minSpareThreads;
    		
    		minSpareThreads = null;
    		
    		minSpareThreads = (Integer)getAttribute(ATTRIBUTE_NAME_MIN_SPARE_THREADS);
    		
    		return minSpareThreads.intValue();
    	}
    	
    	public int getMaxSpareThreads() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		Integer maxSpareThreads;
    		
    		maxSpareThreads = null;
    		
    		maxSpareThreads = (Integer)getAttribute(ATTRIBUTE_NAME_MAX_SPARE_THREADS);
    		
    		return maxSpareThreads.intValue();
    	}
    	
    	public int getMaxThreads() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		Integer maxThreads;
    		
    		maxThreads = null;
    		
    		maxThreads = (Integer)getAttribute(ATTRIBUTE_NAME_MAX_THREADS);
    		
    		return maxThreads.intValue();
    	}
    	
    	public String[] getThreadStatus() throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
    		String[] threadStatus;
    		
    		threadStatus = null;
    		
    		threadStatus = (String[])getAttribute(ATTRIBUTE_NAME_THREAD_STATUS);
    	
    		return threadStatus;
    	}
    	
    	private Object getAttribute(String attributeName) throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
    	{
       		Object attribute;
       		ObjectName threadPoolObjectName;
    		
       		attribute = null;
    		threadPoolObjectName = null;
    		
    		threadPoolObjectName = createThreadPoolObjectName();
    		
    		attribute = getManagementBeanServer().getAttribute(threadPoolObjectName, attributeName);
		
    		return attribute;
    	}
    	
    	private ObjectName createThreadPoolObjectName() throws MalformedObjectNameException
    	{
    		ObjectName threadPoolObjectName;
    		
       		threadPoolObjectName = null;
    		
			threadPoolObjectName = new ObjectName("Catalina:type=ThreadPool,name=" + getName());
    	
			return threadPoolObjectName;
    	}

		public String getName() 
		{
			return this.name;
		}

		private void setName(String name)
		{
			this.name = name;
		}
    }
}