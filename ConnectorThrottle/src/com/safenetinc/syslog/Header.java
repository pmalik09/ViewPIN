package com.safenetinc.syslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Header 
{
	private TimeStamp timeStamp = null;
	private HostName hostName = null;

	public Header(TimeStamp timeStamp, HostName hostName) 
	{
		super();

		setTimeStamp(timeStamp);
		setHostName(hostName);
	}

	private void setTimeStamp(TimeStamp timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public TimeStamp getTimeStamp()
	{
		return this.timeStamp;
	}

	private void setHostName(HostName hostName)
	{
		this.hostName = hostName;
	}

	public HostName getHostName()
	{
		return this.hostName;
	}
	
	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream baos;

		baos = null;

		try
		{
			baos = new ByteArrayOutputStream();

			// Timestamp field
			baos.write(getTimeStamp().getEncodedTimeStamp().getBytes());

			// Place space character after timestamp field
			baos.write(' ');

			// Host name field
			baos.write(getHostName().getHostNameField().getBytes());

			// Place space character after hostname field
			baos.write(' ');
		}
		finally
		{
			if(baos != null)
			{
				baos.close();
			}
		}
		if(baos != null)
			return baos.toByteArray();
		else return null;
	}
}