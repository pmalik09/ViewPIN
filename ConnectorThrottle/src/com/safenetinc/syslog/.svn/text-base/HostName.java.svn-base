package com.safenetinc.syslog;

import java.net.InetAddress;

public class HostName 
{
	private static final int MAXIMUM_HOSTNAME_LENGTH = 63;

	private String hostName = null;
	private InetAddress ipAddress = null;

	private HostName() 
	{
		super();
	}

	private HostName(String hostName)
	{
		super();

		setHostName(hostName);
	}

	private HostName(InetAddress ipAddress)
	{
		super();

		setIpAddress(ipAddress);
	}

	public static HostName getInstance(String hostName) throws InvalidHostNameException
	{
		HostName hn;

		hn = null;
		
		// Remove domain name from host name if present
		hostName = removeDomainName(hostName);
		
		// Ensure host name is valid
		validateHostName(hostName);

		hn = new HostName(hostName);
		
		return hn;
	}
	
	private static String removeDomainName(String hostName)
	{
		int firstPeriod;
		
		firstPeriod = -1;

		// Attempt to get the position of the first period of the the fully qualified domain name
		firstPeriod = hostName.indexOf('.');
		
		// Did we find the first period of the of the fully qualified domain name?
		if(firstPeriod == -1)
		{
			// This is not a fully qualified domain name, return the original hostname untouched
			return hostName;
		}
		
		// Return the hostname without the domain name
		return hostName.substring(0, firstPeriod);
	}

	public static HostName getInstance(InetAddress ipAddress)
	{
        HostName hn;

		hn = null;

		hn = new HostName(ipAddress);

		return hn;
	}

	private static void validateHostName(String hostName) throws InvalidHostNameException
	{
		int firstCharacter;
		int lastCharacter;

		firstCharacter = 0;
		lastCharacter = 0;

		// Ensure host name is not null
		if(hostName == null)
		{
			throw new InvalidHostNameException("is null");
		}

		// Ensire host name is not too small
		if(hostName.length() == 0)
		{
			throw new InvalidHostNameException("length too small");
		}

		// Ensure host name does not exceed maximum length
		if(hostName.length() > MAXIMUM_HOSTNAME_LENGTH)
		{
			throw new InvalidHostNameException("exceeds maximum length");
		}
		
		// Ensure host name contains only valid characters
		for(int i = 0; i < hostName.length(); i++)
		{
			if(isValidCharacter(hostName.charAt(i)) == false)
			{
				throw new InvalidHostNameException("Invalid character");
			}
		}

		// Get first character of host name
		firstCharacter = hostName.charAt(0);

		// Ensure first character of host name is a letter
		if(Utils.isLetterCharacter(firstCharacter) == false)
		{
			throw new InvalidHostNameException("host name must start with a letter");
		}

		// Get last character of host name
		lastCharacter = hostName.charAt(hostName.length() - 1);

		// Ensure last chracter of host name is a letter or a digit
		if(Utils.isLetterOrDigitCharacter(lastCharacter) == false)
		{
			throw new InvalidHostNameException("host name must end with a letter or digit");
		}
	}

	private void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	private String getHostName()
	{
		return this.hostName;
	}

	private static boolean isValidCharacter(int c )
	{
		boolean rc;
		
		rc = false;

		if( (Utils.isLetterCharacter(c) == true) || (Utils.isDigitCharacter(c) == true) || (c == '-') )
		{
			rc = true;
		}

		return rc;
	}

	private void setIpAddress(InetAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	private InetAddress getIpAddress()
	{
		return this.ipAddress;
	}

	public String getHostNameField()
	{
		String hnf;

		hnf = null;

		if(getHostName() != null)
		{
			hnf = getHostName();
		}
		else
		{
			hnf = getIpAddress().getHostAddress();
		}

		return hnf;
	}
}