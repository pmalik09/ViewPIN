package com.safenetinc.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public class SimpleErrorHandler implements ErrorHandler
{
	public void error(String message) 
	{
		System.out.println(message);
	}

	public void error(String message, Exception exception, int errorCode) 
	{
		System.out.println(message);
		
		exception.printStackTrace(System.out);
	}

	public void error(String message, Exception exception, int errorCode, LoggingEvent loggingEvent) 
	{
		if(loggingEvent != null)
		{
			System.out.println("ERROR: " + loggingEvent.getLoggerName() + "-" + message); 
		}
		else
		{
			System.out.println(message);
		}
		
		if(exception != null)
		{
			exception.printStackTrace(System.out);
		}
	}
	
	public void error(String message, LoggingEvent loggingEvent)
	{
	}
	
	public void setAppender(Appender appender) 
	{
	}
	
	public void setBackupAppender(Appender backupAppender) 
	{
	}
	
	public void setLogger(Logger arg0)
	{
	}

	public void activateOptions() 
	{	
	}
}