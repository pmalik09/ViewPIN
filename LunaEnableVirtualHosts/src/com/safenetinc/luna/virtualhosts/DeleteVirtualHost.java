/*
 * Created on 26 Jul 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.safenetinc.luna.fileutils.DirectoryHandler;
import com.safenetinc.luna.virtualhosts.exception.ServerXmlException;
import com.safenetinc.Common;

/**
 * LunaSP command line application for deleting a virtual host from a LunaSP's Tomcat instance. This class
 * removes the Tomcat connector for the port that this virtual host is associated with (this is the purpose of
 * the <code>correspondingConnectorPort</code> attribute added to the host element in Tomcat's server.xml)
 * 
 * @author Paul Hampton
 */
public class DeleteVirtualHost
{

    private static final int    TOMCAT_DEFAULT_PORT       = 8443;

    private static final String HOSTNAME_COMMAND_LINE_ARG = "hostname";

    /**
     * Default constructor
     */
    public DeleteVirtualHost()
    {
        super();
    }

    /**
     * Deletes a virtual host from the LunaSP
     * 
     * @param name
     */
    public void deleteVirtualHost (String name)
    {
    	
        if(name == null)
            return ;
        
        // Ensure the virtual host name is lowercase
        String lowerCaseName = name.toLowerCase();

        try
        {
         
            if (false == ServerXmlHandler.deleteVirtualHost(lowerCaseName))
            {
				System.out.println("DeleteVirtualHost failed");
				return ;
            }
        }
        catch (final ServerXmlException e)
        {
            System.err.println("Could not delete virtual host from server.xml " );
			System.out.println("DeleteVirtualHost failed");
			 return ;
        }
        try
        {
            DirectoryHandler.deleteDirectories(lowerCaseName);
        }
        catch (final IOException e)
        {
            System.err.println("Could not delete virtual host directories");
			System.out.println("DeleteVirtualHost failed");
			return ;
        }
		 System.out.println("Deleted Virtual host " + lowerCaseName);
		 return ;
    }

    /**
     * Main method for this application. Responsible for informing the user of the correct command-line
     * arguments to use when invoking the application and in turn validating those parameters before calling
     * the {@link #deleteVirtualHost(String)} method.
     * 
     * @param args Standard command line arguments
     */
    public static void main (String[] args)
    {
        // parse the command line arguments
        Options options = new Options();

        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host to delete");
        Option hostname = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);

        options.addOption(hostname);
		
		CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
		
		if(args.length !=2)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("DeleteVirtualHost", options);
			return;
		}
		
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
			try
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("DeleteVirtualHost", options);
				return;
			}
			catch (IllegalArgumentException ie)
			{
				System.out.println("Illegal arguemnts passed");
				System.out.println("DeleteVirtualHost failed");
				return;
			}
          
        }
		
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
        DeleteVirtualHost deleter = new DeleteVirtualHost();
		try
		{
			deleter.deleteVirtualHost(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG));
		}
		catch(NegativeArraySizeException e)
		{
			System.out.println("Arguments invalid");
			System.out.println("DeleteVirtualHost failed");
			return;
		}
		catch(ClassCastException e)
		{
			System.out.println("Could not execute DeleteVirtualHost");
			System.out.println("DeleteVirtualHost failed");
			return;
		}
		catch(NullPointerException e)
		{
			System.out.println("Passed values null not accepted");
			System.out.println("DeleteVirtualHost failed");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Check the arguments passed");
			System.out.println("DeleteVirtualHost failed");
			return;
		}
        
       

    }

}
