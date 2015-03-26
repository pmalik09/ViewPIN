package com.safenetinc.viewpin.cli.logging;

import java.io.*;

import org.apache.log4j.Logger;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;


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

public class DeleteLoggingFile {

	Logger    logger                             			  = Logger.getLogger(DeleteLoggingFile.class);
	
	private static final String SERVER_NAME                   = "ViewPIN";
    
	private static final String HOSTNAME_COMMAND_LINE_ARG     = "hostname";
	
	private static final String APPLICATION_NAME              = "DeleteLogFile";
	
	/**
	 * @param args
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
		
        new DeleteLoggingFile(args,cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		return;
	}

	/*
	 * Default Constructor
	 */
	public DeleteLoggingFile(String args[], String virtualHostName)
	{
		
		if(false == deleteLogFile(virtualHostName))
		{
        	System.out.println("Log file deletion failed.");
        	
        }
		else 
		{
			System.out.println("Log File Deleted");
		}
		return;
	}
	/*
	 * This method deletes the log file generated 
	 */
	private static boolean deleteLogFile(String virtualHostName)
	{		 	 	  
		boolean success = false;
		String configFile = Constants.Webapp_Directory + virtualHostName +Constants.Config_Directory+ Constants.LOG_FILE;
		File fileToDelete = new File(configFile);
		if (!fileToDelete.exists()) {
			System.out.println("Required File is not present to begin with!");
			return false;
		}
		try {
			success = fileToDelete.delete();
			if (!success){
				System.out.println("Deletion failed.");
			}
		}catch(SecurityException e) {
			System.out.println("File Deletion Failed error Message: "+ e.getMessage());
			return false;
		}
		return success;
		  
	}
}
