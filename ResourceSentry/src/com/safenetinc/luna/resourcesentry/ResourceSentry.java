// $Id: ResourceSentry/src/com/safenetinc/luna/resourcesentry/ResourceSentry.java 1.5 2013/09/25 09:46:41IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.luna.resourcesentry;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * The Resource Sentry is design to govern access to URLs running within Apache Tomcat. This class acts as an
 * extremely primitive application firewall; checking the resource being accessed and the method used for
 * access.
 * 
 * @author Paul Hampton, Stuart Horler
 * 
 */
public class ResourceSentry extends ValveBase implements Lifecycle
{

    /** Constant representing the POST HTTP method */
    private static final String         POST                     = "POST";

    /** Constant representing the GET HTTP method */
    private static final String         GET                      = "GET";

    /** Constant representing the loop back address */
    private static final String         LOOP_BACK_ADDRESS        = "127.0.0.1";

    private static final Vector<String> NON_ROUTABLE_ADDRESSES   = new Vector<String>();

    /**
     * Location of the Log4j configuration file for the valve (configuration is expected in xml form)
     */
    private static final String         LOG4J_CONFIGURATION_FILE = "/usr-xfiles/resourcesentry-log4j.xml";

    /** Location of the configuration file for the valve */
    private static final String         CONFIGURATION_FILE       = "/usr-xfiles/resourcesentry.properties";

    /** The Log4J Logger the valve will use to log */
    private static Logger               logger                   = Logger.getLogger(ResourceSentry.class);

    /** The configuration object used to read configuration for the valve */
    private static XMLConfiguration     configuration            = null;

    /** Listeners on this valve */
    private Vector<LifecycleListener>   listeners                = null;

    /** List of resources this valve will allow access to */
    private Vector<AccessibleResource>  accessibleResources      = null;

    static
    {
        // Load our Log4J configuration
        try
        {
            DOMConfigurator.configure(LOG4J_CONFIGURATION_FILE);
        }
        catch (FactoryConfigurationError fce)
        {
            System.err.println("ResourceSentryError - Could not load Log4j configuration; Resource Sentry cannot log");
        }

        // Create a configuration instance for loading our valve configuration
        try
        {
            configuration = new XMLConfiguration(CONFIGURATION_FILE);
        }
        catch (ConfigurationException ce)
        {
            System.out.println("Error - Could not open configuration file no resources will be allowed! " + ce);
            logger.error("Error - Could not open configuration file no resources will be allowed! " + ce);
        }
        catch (Exception e)
        {
            System.out.println("Error parsing configuration; no resources will be allowed! " + e);
            logger.error("Error parsing configuration; no resources will be allowed! " + e);
        }

        // Configure our Vector containing non routable IP Addresses
        NON_ROUTABLE_ADDRESSES.add("10");
        NON_ROUTABLE_ADDRESSES.add("172.16");
        NON_ROUTABLE_ADDRESSES.add("172.17");
        NON_ROUTABLE_ADDRESSES.add("172.18");
        NON_ROUTABLE_ADDRESSES.add("172.19");
        NON_ROUTABLE_ADDRESSES.add("172.20");
        NON_ROUTABLE_ADDRESSES.add("172.21");
        NON_ROUTABLE_ADDRESSES.add("172.22");
        NON_ROUTABLE_ADDRESSES.add("172.24");
        NON_ROUTABLE_ADDRESSES.add("172.25");
        NON_ROUTABLE_ADDRESSES.add("172.26");
        NON_ROUTABLE_ADDRESSES.add("172.27");
        NON_ROUTABLE_ADDRESSES.add("172.28");
        NON_ROUTABLE_ADDRESSES.add("172.29");
        NON_ROUTABLE_ADDRESSES.add("172.30");
        NON_ROUTABLE_ADDRESSES.add("172.31");
        NON_ROUTABLE_ADDRESSES.add("192.168");

    }

    /**
     * Construct the Resource Sentry valve.
     * 
     */
    public ResourceSentry()
    {
        super();
        // Print a start message to the console
        System.out.println("Resource Sentry Starting Up");

        // Create a new Vector for holding listeners
        setListeners(new Vector<LifecycleListener>());
        // Obtain a list of resources that we will allow
        try
        {
            initAccesibleResources();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to load configuration for Resource Sentry. Tomcat will start in an unpredictable state!");
            System.out.println("Unable to load configuration for Resource Sentry. Tomcat will start in an unpredictable state! " + e);
        }
        // Print a friendly started message
        System.out.println("Resource Sentry Started Ok");
        logger.info("Resource Sentry Started Ok");
    }

    /**
     * The invoke method is the main function method for a valve. Here we examine an incoming request to
     * determine whether we will let it pass or instead, return a 403 to the end user.
     * 
     * @param request The HTTP request to examine
     * @param response The associated HTTP response
     * @throws IOException Thrown if accessing the request or response was not possible
     * @throws ServletException Thrown if an error occurs processing this request
     */
    @Override
    public void invoke (Request request, Response response) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;
        boolean getRequest = false;
        boolean postRequest = false;
        boolean letRequestThrough = false;
        AccessibleResource nextAccessibleResource = null;
        boolean getAllowed = false;
        boolean postAllowed = false;
        boolean requestInitiatedLocally = false;

        // Get HTTP request and response objects
        httpServletRequest = request;
        httpServletResponse = response;

        // Determine if request was initiated locally
        if (httpServletRequest.getLocalAddr().compareTo(LOOP_BACK_ADDRESS) == 0)
        {
            // Indicate that request was initiated locally
            requestInitiatedLocally = true;
            getLogger().info(httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + " issued locally");
        }

        // Determine HTTP verb
        if (httpServletRequest.getMethod().compareTo(GET) == 0)
        {
            // This is a GET request
            getRequest = true;
        }
        else if (httpServletRequest.getMethod().compareTo(POST) == 0)
        {

            // This is a POST REQUEST
            postRequest = true;
        }
        else
        {
            getLogger().error("Unsupported HTTP verb " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + " from " + httpServletRequest.getRemoteAddr());
            return;
        }

        // Now work through each accessible resource
        for (int i = 0; i < getAccessibleResources().size(); i++)
        {
            nextAccessibleResource = getAccessibleResources().elementAt(i);

            getLogger().info("Next accessible resource name = " + nextAccessibleResource.getName());

            // Does the requested resource match the next accessible resource?
            if (nextAccessibleResource.getName().compareTo(httpServletRequest.getRequestURI()) != 0)
            {
                // Requested resource does not match next accessible resource
                getLogger().info("Next accessible resource does not match requested resource " + httpServletRequest.getRequestURI());

                continue;// skip to next resource
            }

            // Is this resource only accessible locally?
            if (nextAccessibleResource.isLocalOnly() == true)
            {
                // This resource is only available locally, ensure request was made locally
                if (requestInitiatedLocally == false)
                {
                    // An attempt has been made to access a local only resource remotely
                    getLogger()
                            .error(
                                    "Attempt to access local only resource " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + " remotely by "
                                            + httpServletRequest.getRemoteAddr());

                    return;
                }
            }
            else if (nextAccessibleResource.isRestrictByIPAddress())// Does this resource have IP
            // restrictions?
            {
                boolean passIPRestrictions = false;
                // Lets see if we should allow non routable addresses
                if (nextAccessibleResource.isAllowNonRoutableAddresses())
                {
                    if (isAddressNonRoutable(request.getLocalAddr()))
                        passIPRestrictions = true;
                }

                // Now see if there are other specific addresses we should allow
                if (isAddressAllowed(request.getLocalAddr(), nextAccessibleResource))
                {
                    passIPRestrictions = true;
                }

                if (!passIPRestrictions)// if it doesn't pass the restrictions
                // then stop here
                {
                    getLogger().warn("Address " + request.getLocalAddr() + " tried to access resource " + nextAccessibleResource.getName() + " but is not allowed by the IP address restrictions");
                    return;
                }
            }

            // This resource is accessible, but using which HTTP method?
            if (getRequest == true)
            {
                // Is this resource accessible through the GET method?
                if (nextAccessibleResource.getAllowedHttpVerbs().isGetAllowed() == true)
                {
                    // This resource is accessible through the GET method
                    getAllowed = true;
                }
            }
            else
            {
                // Is this a POST request?
                if (postRequest == true)
                {
                    // Is this resource is accessible through the POST method?
                    if (nextAccessibleResource.getAllowedHttpVerbs().isPostAllowed() == true)
                    {
                        // This resource is accessible through the POST method
                        postAllowed = true;
                    }
                }
            }

            /*
             * Break here as we have found a matching resource. The if statement is used to convince compilers
             * and audit tools that we really do want to break here. It is otherwise redundant due to the
             * continue above
             */
            if (nextAccessibleResource.getName().compareTo(httpServletRequest.getRequestURI()) == 0)
                break;// Break here as we found a matching resource
        }

        // If this is a GET request and we allow GET requests on this resource then let it through
        if ((getRequest == true) && (getAllowed == true))
        {
            letRequestThrough = true;
        }
        else
        {
            // If this is a POST request and we allow POST requests on this resource then let it through
            if ((postRequest == true) && (postAllowed == true))
            {
                letRequestThrough = true;
            }
        }

        // Should we allow this request through?
        if (letRequestThrough == true)
        {
            // This request is be allowed through
            getLogger().info("Allowing request " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + " from " + httpServletRequest.getRemoteAddr() + " through");

            // Pass control onto next valve in chain
            getNext().invoke(request, response);
        }
        else
        {
            // This request is not to be allowed through, terminate request
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

            getLogger().error("Blocking request " + httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI() + " from " + httpServletRequest.getRemoteAddr());
        }
    }

    /**
     * Method to start the valve
     */
	 @Override
    public void start() throws LifecycleException
    {
		 //super.startInternal();
        fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_START_EVENT, null));
        fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.START_EVENT,null));
        fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_START_EVENT,null));
    }

    /**
     * Method to stop the valve
     */
	 @Override
    public void stop() throws LifecycleException
    {
		 //super.stop();
		 fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.BEFORE_STOP_EVENT,null));
        fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.STOP_EVENT,null));
        fireLifecycleEvent(new LifecycleEvent(this, Lifecycle.AFTER_STOP_EVENT,null));
    }

    /**
     * Returns an array containing the LifeCycleListeners that are listening on this valve.
     * 
     * @return An array of {@link LifecycleListener} objects that are listening on this valve
     */
    public LifecycleListener[] findLifecycleListeners ()
    {
        LifecycleListener[] allListeners;

        allListeners = null;

        synchronized (getListeners())
        {
            if (getListeners().size() < 1)
            {
                allListeners = new LifecycleListener[0];
            }
            else
            {
                allListeners = new LifecycleListener[getListeners().size()];

                for (int i = 0; i < getListeners().size(); i++)
                {
                    allListeners[i] = getListeners().elementAt(i);
                }
            }
        }

        return allListeners;
    }

    /**
     * Method to allow a listener to be added to the valve
     * 
     * @param listener The {@link LifecycleListener} to add
     */
    public void addLifecycleListener (LifecycleListener listener)
    {
        synchronized (getListeners())
        {
            getListeners().addElement(listener);
        }
    }

    /**
     * Method to remove a listener from this valve
     * 
     * @param listener The {@link LifecycleListener} to remove
     */
    public void removeLifecycleListener (LifecycleListener listener)
    {
        synchronized (getListeners())
        {
            for (int i = 0; i < getListeners().size(); i++)
            {
                if (getListeners().elementAt(i) == listener)
                {
                    getListeners().removeElementAt(i);
                }
            }
        }
    }

    /**
     * Sets the listeners Vector used to store LifeCycleListeners
     * 
     * @param listeners
     */
    private void setListeners (Vector<LifecycleListener> listeners)
    {
        this.listeners = listeners;
    }

    /**
     * Get this valve's LifeCycleListeners
     * 
     * @return A Vector containing the LifeCycleListeners of this valve
     */
    private Vector<LifecycleListener> getListeners ()
    {
        return this.listeners;
    }

    /**
     * Fire a LifeCycleEvent to all listeners of this valve
     * 
     * @param lifecycleEvent The LifeCycleEvent to fire
     */
    private void fireLifecycleEvent (LifecycleEvent lifecycleEvent)
    {
        LifecycleListener nextLifecycleListener;

        nextLifecycleListener = null;

        synchronized (getListeners())
        {
            for (int i = 0; i < getListeners().size(); i++)
            {
                nextLifecycleListener = getListeners().elementAt(i);
                nextLifecycleListener.lifecycleEvent(lifecycleEvent);
            }
        }
    }

    /**
     * Get the current Log4J logger
     * 
     * @return The Log4J Logger
     */
    private static Logger getLogger ()
    {
        return logger;
    }

    /**
     * Set the accessible resource list for this valve
     * 
     * @param accessibleResources The Vector holding the resources this valve allows access to
     */
    private void setAccessibleResources (Vector<AccessibleResource> accessibleResources)
    {
        this.accessibleResources = accessibleResources;
    }

    /**
     * Get accessible resources
     * 
     * @return The Vector containing the list of accessible resources
     */
    private Vector<AccessibleResource> getAccessibleResources ()
    {
        return this.accessibleResources;
    }

    /**
     * Method to load the configuration for the valve from an Apache Commons Configuration object. This method
     * is used to load the accessible resources for the valve
     */
    private void initAccesibleResources () throws NoSuchElementException
    {
        setAccessibleResources(new Vector<AccessibleResource>());

        // Loop through the configuration elements (Use of MAX_VALUE is fairly nasty but necessary)
        for (int i = 0; i < Integer.MAX_VALUE; i++)
        {

            String resource = configuration.getString("resource(" + i + ").url");
            if (resource == null)// no more resources to configure
                break;

            boolean allowGet = configuration.getBoolean("resource(" + i + ").allowedGET");
            boolean allowPost = configuration.getBoolean("resource(" + i + ").allowedPOST");
            boolean localAccessOnly = configuration.getBoolean("resource(" + i + ").localAccessOnly");
            // boolean to denote whether this resource will have IP based
            // restrictions
            boolean restrictedAccess = false;

            boolean allowNonRoutableAddressesOnly = false;

            try
            {
                allowNonRoutableAddressesOnly = configuration.getBoolean("resource(" + i + ").allowedAddresses.nonRoutable");
            }
            catch (NoSuchElementException nsee)
            {
                // if this happens then the non routable element is not present
				 logger.info("There are no non routable element is not present");
            }
            if (allowNonRoutableAddressesOnly)
                restrictedAccess = true;

            // Now look to see if there are other addresses we should add (sets this resource as restricted to
            // only these addresses)
            Vector<String> otherAllowedAddresses = new Vector<String>();
            for (int j = 0; j < Integer.MAX_VALUE; j++)
            {
                String address = null;
                try
                {
                    address = configuration.getString("resource(" + i + ").allowedAddresses.address(" + j + ")");
                }
                catch (NoSuchElementException nsee)
                {
                    // If this happens then no addresses have been specified
                    break; // stop the addressing loop
                }

                if (address == null)
                {
                    if (otherAllowedAddresses.size() == 0)
                        logger.info("There are no specific IP addresses to allow access to resource " + resource);
                    if (otherAllowedAddresses.size() > 0)
                        logger.info("Done adding specific IP addresses for resource " + resource);
                    break;
                }

                otherAllowedAddresses.add(address);
                logger.info("adding accessible address " + address + " for resource " + resource);
                restrictedAccess = true;
            }

            getAccessibleResources().add(
                    new AccessibleResource(resource, new AllowedHttpVerbs(allowGet, allowPost), localAccessOnly, restrictedAccess, allowNonRoutableAddressesOnly, otherAllowedAddresses));
            logger.info("Adding accessible resource " + resource);
            System.out.println("Adding accessible resource " + resource);
            if (allowNonRoutableAddressesOnly)
            {
                logger.info(resource + " is accessible from non-routable IP addresses");
                System.out.println(resource + " is accessible from non-routable IP addresses");
            }
            if (otherAllowedAddresses.size() > 0)
            {
                StringBuffer addresses = new StringBuffer();
                for (int k = 0; k < otherAllowedAddresses.size(); k++)
                    addresses.append(otherAllowedAddresses.get(k) + " ");

                logger.info(resource + " is accessible from IP addresses " + addresses.toString());
                System.out.println(resource + " is accessible from IP addresses " + addresses.toString());
            }

        }
        logger.info("Done loading configuration");
    }

    private boolean isAddressNonRoutable (String address)
    {
        if (address == null)
            return false;

        for (int i = 0; i < NON_ROUTABLE_ADDRESSES.size(); i++)
        {
            if (address.startsWith(NON_ROUTABLE_ADDRESSES.get(i)))
                return true;
        }

        return false;
    }

    private boolean isAddressAllowed (String address, AccessibleResource resource)
    {
        if (address == null)
            return false;
        if (resource == null)
            return false;

        for (int i = 0; i < resource.getAllowedIPAddresses().size(); i++)
        {
            if (address.equals(resource.getAllowedIPAddresses().get(i)))
                return true;
        }
        return false;
    }

}