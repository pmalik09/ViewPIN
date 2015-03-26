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
import com.safenetinc.viewpin.cli.FileHandler;

/**
 * This class implements methods for view the logging elements
 * @author Pmalik
 *
 */
public class ViewLoggingConfiguration {
	
	LoggingConfigurationElements loggingConfigurationElements 				  = new LoggingConfigurationElements();
	
	private static Logger    logger                             			  = Logger.getLogger(ViewLoggingConfiguration.class);
	
    private static final String APPLICATION_NAME              				  = "ViewLoggingConfiguration";
	
    private static final String SERVER_NAME                    				  = "ViewPIN";
    
	private static final String HOSTNAME_COMMAND_LINE_ARG       			  = "hostname";
	
	/*
	 * Default Constructor
	 */
	public ViewLoggingConfiguration(String HostName)
	{
		 if (false == listViewPINAgentConfiguration())
		{
			System.out.println("ViewLoggingConfiguration failed");
			return;
		}
		return;
	}
	
	/*
	 * Main  function to view the logging configuration of PIN Agent
	 * @param args
	 * ViewLoggingConfiguration -name "name of the pinagent" for eg www.mybank.com
	 */
	
	public static void main(String[] args)
	{
		// parse the command line arguments
        final Options options = new Options();

        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host added, for example xyz.com");
        final Option hostname = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);
        
        options.addOption(hostname);
		
		       
        final CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        
        try
        {
            cmd = parser.parse(options, args);
                    
        }
        catch (final ParseException e)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }
       
	    if(args.length != 2)
		{
		   final HelpFormatter formatter = new HelpFormatter();
           formatter.printHelp(APPLICATION_NAME, options);
           return;
		}
		 
        if( false == ReadLoggingConfiguration.checkFileStatus(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG)))
        {
        	System.out.println("Either the virtual host with this name does not exist or Configuration File is missing");
        	return;
        }
		new ViewLoggingConfiguration(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
		
		
		System.out.println("View Configuration Successful");
		return;
	}
	
	/*
	 * ListViewPINAgentLoggingConfiguration
	 * shows the logging configuration elements of PINAgent
	 * shows the following
	 * ConsoleAppender Threshold value
	 * SyslogAppender Threshold value
	 * SyslogAppender Sysloghost
	 * ViewPINThreshold value
	 * return false if it cant read any of the element
	 * reads from the file one by one
	 */
	public boolean listViewPINAgentConfiguration()
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
		//Read threshold of file output
		elementValue = Constants.FILE_APPENDER_NAME;
		if(false == ReadLoggingConfiguration.readLoggingConfiguration(element, elementValue,Constants.THRESHOLD_VALUE))
		{
        	
			logger.error("Could not read File Appender Threshold value");
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
		
			/*
			//print Console Appender Threshold
			System.out.println(Constants.CONSOLE_APPENDER_THRESHOLD + ":" + 
					loggingConfigurationElements.getConsoleAppenderThreshold());*/
			
			//print File Appender Threshold
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
