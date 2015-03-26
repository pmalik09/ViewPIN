package com.safenetinc.viewpin.cli.logging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import com.safenetinc.Common;

public class ViewPinAuthorityLoggingConfiguration {
	
	LoggingConfigurationElements loggingConfigurationElements 				  = new LoggingConfigurationElements();
	
	private static Logger    logger                             			  = Logger.getLogger(ViewPinAuthorityLoggingConfiguration.class);
	
    private static final String APPLICATION_NAME              				  = "ViewPinAuthorityLogging";
	
       
	/*
	 * Default Constructor
	 */
	public ViewPinAuthorityLoggingConfiguration()
	{
		if (false == listViewPINAuthorityConfiguration())
		{
			System.out.println("View PinAuthority Logging Configuration failed.");
		}
		else
		{
			System.out.println("View PinAuthority Logging Configuration Successful.");
		}
		return;
	}
	
	/*
	 * Main  function to view the logging configuration of PIN Authority
	 * @param args
	 */
	
	public static void main(String[] args)
	{
		if(args.length != 0)
		{
			System.out.println("ViewPinAuthorityLogging,No option is required to display the Attributes");
			return;
		}
        if( false == ReadLoggingConfiguration.checkFileStatus())
        {
        	System.out.println("Configuration File is missing");
        	return;
        }
		
		new ViewPinAuthorityLoggingConfiguration();
		return;
	}
	
	/*
	 * ListViewPINAuthorityLoggingConfiguration
	 * shows the logging configuration elements of PINAgent
	 * shows the following
	 * ConsoleAppender Threshold value
	 * SyslogAppender Threshold value
	 * SyslogAppender Sysloghost
	 * ViewPINThreshold value
	 * return false if it cant read any of the element
	 * reads from the file one by one
	 */
	 
	public boolean listViewPINAuthorityConfiguration()
	{
		String elementValue;
		String element;
		element = Constants.ELEMENT_APPENDER;
		
		//Read threshold of console output
		
		/*
		 elementValue = Constants.CONSOLE_APPENDER_NAME;
		 
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	
			logger.error("Could not read Console Appender Threshold value");
        	return false;
        }
		*/
		

		//Read threshold of FILE output
		
		 elementValue = Constants.FILE_APPENDER_NAME;
		 
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	
			logger.error("Could not read Console Appender Threshold value");
        	return false;
        }
		
		//Read threshold  of syslog output
		elementValue =  Constants.SYSLOG_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
			logger.error("Could not read Syslog Appender Threshold value");
        	return false;
        }
		
		//Read ip of syslog output
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue, Constants.SYSLOGHOST_VALUE))
		{
        	logger.error("Could not read Syslog Appender Syshost");
        	return false;
        }
		
		//Read value of viewPIN priority
		element = Constants.ELEMENT_CATEGORY;
		elementValue = Constants.VIEWPIN_CATEGORY_NAME;
		
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue, "test"))
		{
        	logger.error("Could not read ViewPIN Threshold Value");
        	return false;
        }
		
		try
		{
		
			//print Console Appender Threshold
			System.out.println(Constants.FILE_APPENDER_THRESHOLD + ":" + 
					loggingConfigurationElements.getFileAppenderThreshold());
			
			//print Syslog Appender Threshold
			System.out.println(Constants.SYSLOG_APPENDER_THRESHOLD + ":" + 
					loggingConfigurationElements.getSyslogAppenderThreshold());
			
			//print Syslog Appender IP
			System.out.println(Constants.SYSLOG_APPENDER_IP + ":" + 
					loggingConfigurationElements.getsyslogAppenderIP());
			
			//Print ViewPIN Threshold
			System.out.println(Constants.VIEWPIN_APPENDER_THRESHOLD + ":" + 
					loggingConfigurationElements.getViewPINThreshold());
		}
		catch(Exception e)
		{
		//	logger.error(e.getMessage());
			return false;
		}
		
		return true;
	}		
	/*private static Logger getLogger ()
	{
	    return logger;
	}*/

}
