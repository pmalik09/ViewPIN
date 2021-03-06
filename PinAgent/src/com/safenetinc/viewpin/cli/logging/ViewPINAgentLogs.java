package com.safenetinc.viewpin.cli.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.cli.FileHandler;

import com.safenetinc.Common;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ViewPINAgentLogs {
	// TODO Auto-generated method stub
	Logger    logger                             			  = Logger.getLogger(ViewPINAgentLogs.class);
	
  
    static String configFile 								  = null;
    
    private static final String SERVER_NAME                   = "ViewPIN";
    
	private static final String HOSTNAME_COMMAND_LINE_ARG     = "hostname";
	
	private static final String APPLICATION_NAME              = "ViewLogFile";
	
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
		 
        if( false == FileHandler.checkFileStatus(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG)))
        {
        	System.out.println("Either the virtual host with this name does not exist or Configuration File is missing");
        	return;
        }
        
        boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
        new ViewPINAgentLogs(args, cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}
	/*
	 * Default Constructor
	 */
	public ViewPINAgentLogs(String args[], String virtualHostName)
	{
				
		if(false == viewLogFile(virtualHostName))
		{
        	System.out.println("View Log file failed. ");
        	
        }
		else 
		{
			System.out.println("View Log file successful.");
		}
		return;
	}
	
	/*
	 * This method displays the log file on console 
	 */
	private static boolean viewLogFile(String virtualHostName)
	{
		try
		{
			String configFile = Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+ Constants.LOG_FILE;
			FileInputStream input = new FileInputStream(configFile);
			FileChannel channel = input.getChannel();
			byte[] buffer = new byte[256 * 1024 * 1024];
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
	
			try {
			    for (int length = 0; (length = channel.read(byteBuffer)) != -1;) {
			        System.out.write(buffer, 0, length);
			        byteBuffer.clear();
			    }
			} catch (IOException e) {	
				System.out.println("Could not read file.");
				return false;
			} finally {
			    try {
					input.close();
				} 
			    catch (IOException e)
			    {
			    	return false;
				}
			}
		}
		catch(FileNotFoundException fne)
		{
			System.out.println("Log file not present.");
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Could not read Log File");
			return false;		
		}
		return true;
	}
	
	

	 
}
