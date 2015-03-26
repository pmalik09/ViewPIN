package com.safenetinc.syslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Packet
{
	private Priority priority = null;
	private Header header = null;
	private Message message = null;
	
    public Packet(Priority priority, Header header, Message message)
    {
    	super();
    	
    	setPriority(priority);
    	setHeader(header);
    	setMessage(message);
    }

	public Header getHeader() 
	{
		return this.header;
	}

	private void setHeader(Header header) 
	{
		this.header = header;
	}

	public Message getMessage() 
	{
		return this.message;
	}

	private void setMessage(Message message)
	{
		this.message = message;
	}

	public Priority getPriority()
	{
		return this.priority;
	}

	private void setPriority(Priority priority)
	{
		this.priority = priority;
	}
	
	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream baos;
		
		baos = null;
		
		try
		{
		    baos = new ByteArrayOutputStream();
		
		    baos.write(getPriority().toByteArray());
            baos.write(getHeader().toByteArray());
		    baos.write(getMessage().toByteArray());
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