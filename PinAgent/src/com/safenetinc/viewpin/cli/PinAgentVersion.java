package com.safenetinc.viewpin.cli;

import java.lang.Number;

import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.cli.Constants;
import com.safenetinc.viewpin.cli.ReadPinAgentVersion;
import com.safenetinc.viewpin.cli.FileHandler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
                                               

/**
* class to View PinAgent Version
*/

public class PinAgentVersion {
	
	Constants Constants = new Constants();
	ReadPinAgentVersion readPinAgentVersion = new ReadPinAgentVersion();
	
	private static final String HOSTNAME_COMMAND_LINE_ARG       = "hostname";
	private static final String APPLICATION_NAME                = "GetPINAgentVersion";
	
	/**
	* main function to interact with user
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
		
		String virtualhostname = cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG);
        if( false == ReadPinAgentVersion.checkVersionFileStatus(virtualhostname))
        {
		  System.out.println("Either the virtual host with this name does not exist or Version File is missing");
    	  System.out.println("View PINAgent Version failed");
    	  return;
        }
			
		new PinAgentVersion(virtualhostname);
	}
	
	
	public PinAgentVersion(String virtualhostname) 
	{	
		getVersionInfo(virtualhostname); 
		System.out.println("View PINAgent Version successful.");
	}
	
	public boolean getVersionInfo(String virtualhostname) 
	{
		System.out.println(Constants.PinAgentVersion + " : " + 
		readPinAgentVersion.readPinAgentVersion(virtualhostname));
		return true;
	}
	
}