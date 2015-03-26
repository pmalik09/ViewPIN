package com.safenetinc.luna.sp.throttle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.ServletException;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.safenetinc.luna.sp.throttle.ThreadPools.ThreadPool;

public class ConnectorThrottle extends ValveBase implements Lifecycle
{
	private static Logger logger = Logger.getLogger(ConnectorThrottle.class);
	private static final String LOGGING_CONFIGURATION_FILE = "/usr-xfiles/connectorthrottle-log4j.xml";
	private static final String VERSION = "050620071232";
	private static final String CONFIGURATION_FILENAME = "/usr-xfiles/connectorthrottle-configuration.xml";
	private static final String DEFAULT_MAINTENANCE_PAGE_URL ="https://maintenance2.egg.com/websolutions/sorry.asp";
	private static final String THREAD_POOL_NAME_PREFIX = "http-";
	
	private Vector<LifecycleListener> listeners = null;
	private String threadPoolBusyRedirectUrl = null;
	private ThreadPools threadPools = null;
	private ThreadPoolThresholds threadPoolThresholds = null;
	
	public ConnectorThrottle() throws ThreadPoolException
	{
		super();
		
		setListeners(new Vector<LifecycleListener>());
	
		setThreadPoolBusyRedirectUrl(DEFAULT_MAINTENANCE_PAGE_URL);
		
		setThreadPools(new ThreadPools());
		
		setThreadPoolThresholds(new ThreadPoolThresholds());
		
        initLogging();
		
		initConfiguration();
		
	    getLogger().info("version = " + VERSION);
	    getLogger().info("started");
	}
	
	public void invoke(Request request, Response response) throws IOException, ServletException
    {
		try
		{
			// Throttle request if the current busy threads of this connectors thread pool has exceeded threshold
			if(isConnectorsBusyThreadThresholdExceeded(request) == true)
			{
                // Current busy threads of this connectors thread pool has exceeded threshold
		        response.sendRedirect(getThreadPoolBusyRedirectUrl());
		
				return;
			}
		}
		catch(Exception e)
		{
			getLogger().error("throttling thread pool", e);
		}
		
		// Request has not been throttled, pass down to next valve in chain
	    getNext().invoke(request, response);
    }
	
	private boolean isConnectorsBusyThreadThresholdExceeded(Request request) throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
	{
		boolean throttleRequest;
        String connectorThreadPoolName;
        ThreadPoolThreshold connectorThreadPoolThreshold;
        ThreadPool connectorThreadPool;
        
        throttleRequest = false;
        connectorThreadPoolName = null;
        connectorThreadPoolThreshold = null;
        connectorThreadPool = null;
        
        // Get name of connectors thread pool
        connectorThreadPoolName = getConnectorThreadPoolName(request);
        
        // Did we get name of connectors thread pool OK?
        if(connectorThreadPoolName == null)
        {
        	// Failed to get name of connectors thread pool
            throttleRequest = false;
            
            getLogger().error("getting name of connectors thread pool");
        	
        	return throttleRequest;
        }
        
        // Get connectors thread pool threshold
        connectorThreadPoolThreshold = getThreadPoolThresholds().getThreadPoolThreshold(connectorThreadPoolName);
        
        // Did we get connectors thread pool threshold OK?
        if(connectorThreadPoolThreshold == null)
        {
        	// Failed to get connectors thread pool threshold
        	throttleRequest = false;
        	
        	getLogger().debug("connector " + connectorThreadPoolName + " thread pool threshold not present");
        	
        	return throttleRequest;
        }
        
        // Get connectors thread pool
        connectorThreadPool = getThreadPools().getThreadPool(connectorThreadPoolName);
        
        // Did we get connectors thread pool OK?
        if(connectorThreadPool == null)
        {
        	// Failed to get connectors thread pool
         	throttleRequest = false;
         	
         	getLogger().error("getting connectors thread pool");
        	
        	return throttleRequest;
        }
        
        // Log connectors thread pool statistics
        getLogger().debug(buildThreadPoolStatisticsLoggingMessage(connectorThreadPool));
		
        // Should we throttle request?
        if(connectorThreadPool.getCurrentThreadsBusy() > connectorThreadPoolThreshold.getThreshold())
        {
        	// Request should be throttled
        	throttleRequest = true;
        
            getLogger().warn("current busy threads of connector " + connectorThreadPoolName +
                " has exceeded thread pool threshold of " + connectorThreadPoolThreshold.getThreshold() +
                " redirecting request to " + getThreadPoolBusyRedirectUrl());
        }
        
        return throttleRequest;
	}
	
	private String getConnectorThreadPoolName(Request request)
	{
	    String threadPoolName;
	    ObjectName connectorObjectName;
	    String connectorPortNumber;
	    
	    threadPoolName = null;
	    connectorObjectName = null;
	    connectorPortNumber = null;
	    
	    // Get connector object name
		connectorObjectName = request.getConnector().getObjectName();
		
		// Get connector port number
		connectorPortNumber = connectorObjectName.getKeyProperty("port");
		
		// Build thread pool name
		threadPoolName = THREAD_POOL_NAME_PREFIX + connectorPortNumber;
	    
	    return threadPoolName;
	}
	
	private void initConfiguration()
	{
		File configurationFile;
		XMLConfiguration configuration;
		List<String> connectorNames;
		String nextConnectorName;
		boolean nextConnectorThrottle;
		int nextConnectorThreshold;
		String threadPoolBusyRedirectUrl;
		
		configurationFile = null;
		configuration = null;
		connectorNames = null;
		nextConnectorName = null;
		nextConnectorThrottle = false;
		nextConnectorThreshold = 0;
		threadPoolBusyRedirectUrl = null;
		
		// Instantiate File object to hold name of configuration file
		configurationFile = new File(CONFIGURATION_FILENAME);
		
		// Ensure configuration file exists
		if(configurationFile.exists() == false)
		{
			getLogger().error("configuration file " + configurationFile.getAbsolutePath() + " does not exist");
			
			return;
		}
		
		// Ensure configuration file is readable
		if(configurationFile.canRead() == false)
		{
			getLogger().error("reading configuration file " + configurationFile.getAbsolutePath());
			
			return;
		}

		try
		{
			// Parse configuration file
			configuration = new XMLConfiguration(configurationFile);
			
			// Get all connector names
			connectorNames = configuration.getList("Connectors.Connector.Name");
			
		    // Work through all connector names
			for(int i = 0; i < connectorNames.size(); i++)
			{
                // Get next connector name
				nextConnectorName = (String)connectorNames.get(i);
				
				// Get next connectors throttle status
				nextConnectorThrottle = configuration.getBoolean("Connectors.Connector(" + i + ").Throttle");
			   
				// Determine if next connector should be throttled
				if(nextConnectorThrottle == false)
				{
					getLogger().info("connector " + nextConnectorName + " is not to be throttled");
				
					continue;
				}
				
				// Get next connector throttling threshold
				nextConnectorThreshold = configuration.getInt("Connectors.Connector(" + i + ").Threshold");
				
				getLogger().info("connector " + nextConnectorName + " will be throttled if threshold of " +
				    nextConnectorThreshold + " is exceeded");
				
				// Add connector throttling threshold to thread pool thresholds collection
			    getThreadPoolThresholds().addThreadPoolThreshold(new ThreadPoolThreshold(nextConnectorName, nextConnectorThreshold));
			}
			
			// Get thread pool busy redirect url
			threadPoolBusyRedirectUrl = configuration.getString("ThreadPoolBusyRedirectUrl");
	
			// Did we get thread pool busy redirect url OK?
			if(threadPoolBusyRedirectUrl != null)
			{
				// Got thread pool busy redirect url OK
				setThreadPoolBusyRedirectUrl(threadPoolBusyRedirectUrl);
			}
			
			getLogger().info("thread pool busy redirect url = " + getThreadPoolBusyRedirectUrl());
		}
		catch(ConfigurationException ce)
		{
		    getLogger().error("parsing configuration file", ce);
		}
	}
	
	private String buildThreadPoolStatisticsLoggingMessage(ThreadPool threadPool) throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
	{
		StringBuffer loggingMessage;
	    String[] threadStatus;
		
		loggingMessage = null;
		threadStatus = null;
		
		loggingMessage = new StringBuffer();
		
		loggingMessage.append(threadPool.getName() + "," + "currentThreadsBusy=" + threadPool.getCurrentThreadsBusy() +
			    "," + "currentThreadCount=" + threadPool.getCurrentThreadCount() +
			    "," + "minSpareThreads=" + threadPool.getMinSpareThreads() +
			    "," + "maxSpareThreads=" + threadPool.getMaxSpareThreads() +
			    "," + "maxThreads=" + threadPool.getMaxThreads());
			
		threadStatus = threadPool.getThreadStatus();
		
		for(int i = 0; i < threadStatus.length; i++)
		{
			loggingMessage.append("," + i + "=" + threadStatus[i]);
		}
		
		return loggingMessage.toString();
	}
	
	private void initLogging()
	{
		try
        {
            DOMConfigurator.configure(LOGGING_CONFIGURATION_FILE);
        }
        catch(FactoryConfigurationError fce)
        {
            System.err.println("ConnectorThrottle - could not load logging configuration");
        }
	}
	
    private String getThreadPoolBusyRedirectUrl() 
	{
		return this.threadPoolBusyRedirectUrl;
	}

	private void setThreadPoolBusyRedirectUrl(String threadPoolBusyRedirectUrl)
	{
		this.threadPoolBusyRedirectUrl = threadPoolBusyRedirectUrl;
	}

	private ThreadPools getThreadPools() 
	{
		return this.threadPools;
	}

	private void setThreadPools(ThreadPools threadPools)
	{
		this.threadPools = threadPools;
	}
	
	private static Logger getLogger()
	{
		return logger;
	}

	private ThreadPoolThresholds getThreadPoolThresholds() 
	{
		return this.threadPoolThresholds;
	}

	private void setThreadPoolThresholds(ThreadPoolThresholds threadPoolThresholds)
	{
		this.threadPoolThresholds = threadPoolThresholds;
	}
	
	public void start()
    {
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.START_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_START_EVENT));
    }
    
    public void stop()
    {
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_STOP_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.STOP_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_STOP_EVENT));
    }

    public LifecycleListener[] findLifecycleListeners()
    {
        LifecycleListener[] allListeners;

        allListeners = null;

        synchronized(getListeners())
        {
            if(getListeners().size() < 1)
            {
                allListeners = new LifecycleListener[0];
            }
            else
            {
                allListeners = new LifecycleListener[getListeners().size()];

                for(int i = 0; i < getListeners().size(); i++)
                {
                    allListeners[i] = (LifecycleListener)getListeners().elementAt(i);
                }
            }
        }

        return allListeners;
    }

    public void addLifecycleListener(LifecycleListener listener)
    {
        synchronized(getListeners())
        {
            getListeners().addElement(listener);
        }
    }

    public void removeLifecycleListener(LifecycleListener listener)
    {
        synchronized(getListeners())
        {
            for(int i = 0; i < getListeners().size(); i++)
            {
                if(getListeners().elementAt(i) == listener)
                {
                    getListeners().removeElementAt(i);
                }
            }
        }
    }

    private void setListeners(Vector<LifecycleListener> listeners)
    {
        this.listeners = listeners;
    }

    private Vector<LifecycleListener> getListeners()
    {
        return this.listeners;
    }

    private void fireLifecycleEvent(LifecycleEvent lifecycleEvent)
    {
        LifecycleListener nextLifecycleListener;

        nextLifecycleListener = null;

        synchronized(getListeners())
        {
            for(int i = 0; i < getListeners().size(); i++)
            {
                nextLifecycleListener = (LifecycleListener)getListeners().elementAt(i);
                nextLifecycleListener.lifecycleEvent(lifecycleEvent);
            }
        }
    }
}