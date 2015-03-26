package com.safenetinc.log4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.safenetinc.syslog.Facility;
import com.safenetinc.syslog.Header;
import com.safenetinc.syslog.HostName;
import com.safenetinc.syslog.Message;
import com.safenetinc.syslog.Packet;
import com.safenetinc.syslog.Priority;
import com.safenetinc.syslog.Sender;
import com.safenetinc.syslog.Severity;
import com.safenetinc.syslog.Tag;
import com.safenetinc.syslog.TimeStamp;

public class TcpSyslogAppender extends AppenderSkeleton
{
	private static final int TIMEOUT_INFINITE = 0;
	
	private String remoteHost = null;
	private int portNumber = 0;
	private int connectTimeout = TIMEOUT_INFINITE;
	private String tag = "tagnotdefined	";
	
	public TcpSyslogAppender()
	{
		super();
	}
	
	protected void append(LoggingEvent loggingEvent) 
	{
		Socket remoteHostSocket;
		InetSocketAddress remoteHostAddress;
		
		remoteHostSocket = null;
		remoteHostAddress = null;
		
		if(getRemoteHost() == null)
	    {
	    	getErrorHandler().error("remote host not set", null, 0, loggingEvent);	
	    	
	    	return;
	    }
	    
	    if(getPortNumber() == 0)
	    {
	    	getErrorHandler().error("port number not set", null, 0, loggingEvent);
	    	
	    	return;
	    }
	    
	    try
	    {
	    	remoteHostSocket = new Socket();
	    	
	    	remoteHostAddress = new InetSocketAddress(getRemoteHost(), getPortNumber());
	    	remoteHostSocket.connect(remoteHostAddress, getConnectTimeout());
	    	
	    	send(remoteHostSocket, loggingEvent);
	    } 
	    catch(IOException ioe) 
	    {
	    	getErrorHandler().error("sending message", ioe, ErrorCode.WRITE_FAILURE, loggingEvent);
		}
	    finally
	    {
	    	if(remoteHostSocket != null)
	    	{
	    		try 
	    		{
	    			remoteHostSocket.close();
				}
	    		catch(IOException ioe) 
	    		{
	    			getErrorHandler().error("closing socket", ioe, ErrorCode.WRITE_FAILURE, loggingEvent);
				}
	    	}
	    }
	}
	
	private void send(Socket remoteHostSocket, LoggingEvent loggingEvent)
	{
		int severity;
		Priority priority;
		TimeStamp timestamp;
		HostName hostName;
		Header header;
		Message message;
		Packet packet;
		Sender sender;
		
		severity = 0;
		priority = null;
		timestamp = null;
		hostName = null;
		header = null;
		message = null;
		packet = null;
		sender = null;
		
		try
		{
			// Build priority
			severity = levelToSeverity(loggingEvent.getLevel());
			priority = Priority.getInstance(Facility.USER_LEVEL_MESSAGES, severity);
		}
		catch(Exception e)
		{
			getErrorHandler().error("building priority", e, ErrorCode.GENERIC_FAILURE, loggingEvent);
			
			return;
		}
		
		try
		{
	        // Build header
			timestamp = new TimeStamp(new Date(loggingEvent.timeStamp));
			hostName = HostName.getInstance(getRemoteHost());
			header = new Header(timestamp, hostName);
		}
		catch(Exception e)
		{
			getErrorHandler().error("building header", e, ErrorCode.GENERIC_FAILURE, loggingEvent);
			
			return;
		}
		
		try
		{
	        // Build message
			message = Message.getInstance(Tag.getInstance(getTag()), ":" + loggingEvent.getRenderedMessage());
		}
		catch(Exception e)
		{
            getErrorHandler().error("building message", e, ErrorCode.GENERIC_FAILURE, loggingEvent);
			
			return;
		}
		
        // Build packet
		packet = new Packet(priority, header, message);
	
		try
		{
			// Send packet
			sender = new Sender();
			sender.send(packet, remoteHostSocket);
		}
		catch(Exception e)
		{
            getErrorHandler().error("sending packet", e, ErrorCode.WRITE_FAILURE, loggingEvent);
			
			return;
		}
	}
	
	private int levelToSeverity(Level level)
	{
		int severity;

		severity = 0;
		
		switch(level.toInt())
		{
		    case Level.DEBUG_INT :
		    
		    	severity = Severity.DEBUG;
		    	
		    	break;
		
		    case Level.INFO_INT :
			    
		    	severity = Severity.INFORMATIONAL;
		    	
		    	break;
			
		    case Level.WARN_INT :
		    	
		    	severity = Severity.WARNING;
		    	
			    break;
			    
		    case Level.ERROR_INT :
			    
		    	severity = Severity.ERROR;
		    	
		    	break;
		    
		    case Level.FATAL_INT :
			    
		    	severity = Severity.CRITICAL;
		    	
		    	break;
			    
			default :
	
		    	severity = Severity.INFORMATIONAL;
			
				break;
		}
	
		return severity;
	}
	
	public void close()
	{
	}

	public boolean requiresLayout() 
	{
		return false;
	}
	
	public String getRemoteHost() 
	{
		return this.remoteHost;
	}

	public void setRemoteHost(String remoteHost) 
	{
		this.remoteHost = remoteHost;
	}
	
	public int getPortNumber() 
	{
		return this.portNumber;
	}

	public void setPortNumber(int portNumber) 
	{
		this.portNumber = portNumber;
	}

	public int getConnectTimeout()
	{
		return this.connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout)
	{
		this.connectTimeout = connectTimeout;
	}

	public String getTag() 
	{
		return this.tag;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}
}