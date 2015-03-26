package com.safenetinc.luna.sp;

import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Vector;

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
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Valve to change the default JCE provider for applications running within Tomcat on a Luna SP
 * @author Stuart Horler
 */
public class ProviderAlgorithmSelector extends ValveBase implements Lifecycle
{
	private static Logger logger = Logger.getLogger(ProviderAlgorithmSelector.class);
	private static final String LOGGING_CONFIGURATION_FILE = "/usr-xfiles/provideralgorithmselector-log4j.xml";
	private static final String VERSION = "040620071624";
	private static final String CONFIGURATION_FILENAME = "/usr-xfiles/provideralgorithmselector-configuration.xml";
	
	private Vector<LifecycleListener> listeners = null;

    /**
     * Initialises the valve's logging and the algorithms and associated providers
     */
    public ProviderAlgorithmSelector()
    {
    	super();

        initLogging();
        
        getLogger().info("starting, version = " + VERSION);
        
    	setListeners(new Vector<LifecycleListener>());
    	
        selectProviderAlgorithms();
    	
        getLogger().info("started, version = " + VERSION);
    }
    
    /**
     * The invoke method is the main function method for a valve
     * @param request The HTTP request to examine
     * @param response The associated HTTP response
     * @throws IOException Thrown if accessing the request or response was not possible
     * @throws ServletException Thrown if an error occurs processing this request
     */
    @Override
	public void invoke(Request request, Response response) throws IOException, ServletException
    {
        getNext().invoke(request, response);
    }
    
    @SuppressWarnings("unchecked")
    private void selectProviderAlgorithms()
	{
		File configurationFile;
		XMLConfiguration configuration;
	    List<String> providerNames;
		String nextProviderName;
		Provider nextProvider;
		List<String> nextAlgorithmNames;
		String nextAlgorithmName;
	    String nextAlgorithmValue;
		
		configurationFile = null;
		configuration = null;
		providerNames = null;
		nextProviderName = null;
		nextProvider = null;
		nextAlgorithmNames = null;
		nextAlgorithmName = null;
		nextAlgorithmValue = null;
		
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
			
			// Get all provider names
			providerNames = configuration.getList("Providers.Provider.Name"); 
		
			// Work through all provider names
			for(int i = 0; i < providerNames.size(); i++)
			{
				// Get next provider name
				nextProviderName = providerNames.get(i);
				
				// Get next provider
				nextProvider = Security.getProvider(nextProviderName);
				
				// Did we get next provider OK?
				if(nextProvider == null)
				{
					// Failed to get next provider
					getLogger().error("provider not present in JCA/JCE stack");
					
					continue;
				}
				
				// Remove all existing algorithms from provider
				nextProvider.clear();
				
				// Get all algorithms for this provider
				nextAlgorithmNames = configuration.getList("Providers.Provider(" + i + ").Algorithms.Algorithm.Name");
			
			    // Work through all algorithms for this provider
				for(int j = 0; j < nextAlgorithmNames.size(); j++)
				{
					// Get next algorithm name
					nextAlgorithmName = nextAlgorithmNames.get(j);
					
		            // Get next algorithm value
					nextAlgorithmValue = configuration.getString("Providers.Provider(" + i + ").Algorithms.Algorithm(" + j + ").Value");
					
					// Place next algorithm name and value into provider
					nextProvider.put(nextAlgorithmName, nextAlgorithmValue);
					
					getLogger().debug("provider=" + nextProviderName + ",algorithm name=" + nextAlgorithmName + ",algorithm value=" + nextAlgorithmValue);
				}
			}
		}
		catch(ConfigurationException ce)
		{
			getLogger().error("parsing configuration file", ce);
		}
	}
    
    private void initLogging()
	{
        initMinimalErrorLogger();
		
        try
        {
            DOMConfigurator.configure(LOGGING_CONFIGURATION_FILE);
        }
        catch(FactoryConfigurationError fce)
        {
            getLogger().error("could not load logging configuration " + fce.getMessage());
        }
	}
    
    private static Logger getLogger()
	{
		return logger;
	}
    
    /**
     * Method to start the valve
     */
    public void start()
    {
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.START_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_START_EVENT));
    }
    
    /**
     * Method to stop the valve
     */
    public void stop()
    {
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_STOP_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.STOP_EVENT));
         fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_STOP_EVENT));
    }

    /**
     * Returns an array containing the LifeCycleListeners that are listening on this valve.
     * @return An array of {@link LifecycleListener} objects that are listening on this valve
     */
    public LifecycleListener[] findLifecycleListeners()
    {
        LifecycleListener[] allListeners;

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
                    allListeners[i] = getListeners().elementAt(i);
                }
            }
        }

        return allListeners;
    }

    /**
     * Method to allow a listener to be added to the valve
     * @param listener The {@link LifecycleListener} to add
     */
    public void addLifecycleListener(LifecycleListener listener)
    {
        synchronized(getListeners())
        {
            getListeners().addElement(listener);
        }
    }

    /**
     * Method to remove a listener from this valve
     * @param listener The {@link LifecycleListener} to remove
     */
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
                nextLifecycleListener = getListeners().elementAt(i);
                nextLifecycleListener.lifecycleEvent(lifecycleEvent);
            }
        }
    }
    
    private void initMinimalErrorLogger()
    {
        Logger minimalErrorLogger;
        ConsoleAppender consoleAppender;

        minimalErrorLogger = null;
        consoleAppender = null;

        minimalErrorLogger = Logger.getLogger("com.safenetinc");
        minimalErrorLogger.setLevel(Level.ERROR);
        minimalErrorLogger.setAdditivity(false);
        
        consoleAppender = new ConsoleAppender();
        consoleAppender.setTarget(ConsoleAppender.SYSTEM_OUT);
        consoleAppender.setThreshold(Level.ERROR);

        consoleAppender.setLayout(new PatternLayout("%d{ISO8601} %p %c - %m%n"));

        minimalErrorLogger.addAppender(consoleAppender);
    }
}