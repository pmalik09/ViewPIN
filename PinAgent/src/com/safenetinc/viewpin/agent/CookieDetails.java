// $Id: PinAgent/src/com/safenetinc/viewpin/agent/CookieDetails.java 1.1 2008/09/04 10:45:41IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent;

import java.io.Serializable;

/**
 * Class to represent the parameters of a cookie that the PINAgent needs to set
 * 
 * @author Stuart Horler
 *
 * $Id: PinAgent/src/com/safenetinc/viewpin/agent/CookieDetails.java 1.1 2008/09/04 10:45:41IST Mkhurana Exp  $
 *
 */
public class CookieDetails implements Serializable
{
	private static final long serialVersionUID = 42L;
    private String name = null;
	private String domain = null;
	private String path = null;
	
    /**
     * Constructor
     * 
     * @param name The name of the cookie
     * @param domain The domain for the cookie
     * @param path The path for the cookie
     */
    public CookieDetails(String name, String domain, String path)
    {
    	super();
    
        setName(name);
        setDomain(domain);
        setPath(path);
    }

	private void setName(String name)
	{
		this.name = name;
	}
    
	/**
	 * @return The name of the cookie
	 */
	public String getName() 
	{
		return this.name;
	}
	
	private void setDomain(String domain) 
	{
		this.domain = domain;
	}
	
	/**
	 * @return The domain for the cookie
	 */
	public String getDomain() 
	{
		return this.domain;
	}

	private void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return The path for the cookie
	 */
	public String getPath()
	{
		return this.path;
	}
    
    /**
     * @return The name of the cookie
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }
}