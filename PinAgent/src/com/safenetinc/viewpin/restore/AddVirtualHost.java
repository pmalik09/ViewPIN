/*
 * Created on Jul 17, 2007
 * 
 * 
 */
package com.safenetinc.viewpin.restore;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import java.io.FileNotFoundException;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import com.safenetinc.viewpin.backup.ServerXmlHandler;
import com.safenetinc.viewpin.backup.exception.ServerXmlException;


/**
 * LunaSP command line application used to setup a LunaSP for virtual hosting.
 * This application does the following things to tomcat's server.xml:<br>
 * - Removes the default connectors on ports 8080 and 8443<br>
 * - Sets a predetermined 'secure' set of SSL ciphers<br>
 * - Adds the host settings for the virtual host<br>
 * - Creates the directories necessary for virtual hosting<br>
 * See <a href="http://tomcat.apache.org/tomcat-5.5-doc/virtual-hosting-howto.html">The Tomcat Documentation</a> for more information
 * @author Paul Hampton
 */
public class AddVirtualHost
{

	private static final String PINAGENT_WAR_FILE						 = "/PinAgent.war";
 
    private static final int    LUNA_DEFAULT_HTTPS_PORT         		 = 8443;
 
    private static final int    LUNA_DEFAULT_HTTP_PORT         			 = 8080;

    private static final int    HIGH_PORT_NUMBER_RANGE          		 = 65536;

    private static final int    LOW_PORT_NUMBER_RANGE           		 = 1024;

    private static final String APPLICATION_NAME                		 = "VirtualHostsConfiguration";

    private static final String CERTIFICATESKI_COMMAND_LINE_ARG 		 = "certificateski";

    private static final String HOSTNAME_COMMAND_LINE_ARG       		 = "hostname";

    private static final String PORT_COMMAND_LINE_ARG           		 = "port";

    private static final String SERVER_NAME                     		 = "ViewPIN";

    private static final String SOURCE_FILE_PATH                 		 = "/usr-files/";

    private static final String AGENT_CONFIGURATION_FILE_NAME        	 = "agentconfiguration.xml";

    private static final String CONFIGURATION_DESTINATION_PATH_START 	 = "/usr/tomcat/webapps/";

    private static final String CONFIGURATION_DESTINATION_DIR        	 = "/configuration/";
    
    private FileCopy fileCopy				                     		 = new FileCopy();
    
    private static final String AGENT_LOG_CONFIGURATION_FILE_NAME        = "pinagent-log4j.xml";
    
    
    
    


    /**
     * Default constructor
     */
    public AddVirtualHost()
    {
        super();
    }

    /**
     * Creates a virtual host on a LunaSP. This method expects several things to be in place before it is run:<br>
     * <br>
     * -The LunaSP command <code>webs server port addSecure</code> to have been run already in order to 
     * create a tomcat connector on the port the user wishes to access their new virtual host from.<br>
     * <br>
     * -A SSL certificate to be in place within the HSM of the Luna. The Subject Key Identifier (SKI) of this
     * certificate is passed to this method which will set this SKI as the SSL certificate for the port indicated
     * by the port parameter.
     * 
     * @param hostname The hostname of the virtual host to create for example viewpin.safenet-inc.com
     * @param port The port to use for this virtual host
     * @param keyAlias The Subject Key Identifier of the SSL certificate to be used on the port
     */
    public boolean createVirtualHost (String hostname, int port, String keyAlias)
    {
    	
    	
    	try
		{
			fileCopy.copy(PINAGENT_WAR_FILE,"/usr-files/");			
		}
		catch (IOException e)
		{
			   System.out.println("Could not copy PINAgent" + e.getMessage());
			  return false;
		}
        // Now ensure the hostname is in lowercase
        String hostnameLowerCase = hostname.toLowerCase();

        // Check that the port is configured
        try
        {
            if (!ServerXmlHandler.checkForConnectorOnPort(port))
            {
            	System.out.println("No connector configured on port :"+ port +", please choose a port with a connector configured (use command \"websvc server port addSecure\" to add a port)");
                return false;
            }
            // Now check that the virtual host name doesn't already exist
            if (ServerXmlHandler.virtualHostExists(hostnameLowerCase))
            {
            	System.out.println("A virtual host with that name already exists.");
                return false;
            }
            // Modify the connector
            ServerXmlHandler.changeCertificateForConnector(port, keyAlias);
        }
        catch (ServerXmlException sxe)
        {
            System.err.println("Could not modify the connector ");
        }

        // Now add the virtual host directory configuration
        try
        {
            DirectoryHandler.createVirtualHostRootDirectories(hostnameLowerCase);
        }
        catch (IOException ioe)
        {
           
        	 System.out.println("Could not create directories " );
            return false;
        }
        try
        {

            

            // Now add our new host
            ServerXmlHandler.addVirtualHost(hostnameLowerCase, port);

            // Configure the SSL ciphers
            ServerXmlHandler.setSecureCiphersForConnectors();

            // Tune the connector
            ServerXmlHandler.setConnectionParameters(SERVER_NAME);
        }
        catch (ServerXmlException e)
        {
        	System.out.println("Could not add entries to server.xml  " );
            return false;
        }
	
		//deploy to Virtual Host

		  // Lowercase the hostname
        String lowerCaseHostname = hostname.toLowerCase();

        // Now copy the file
        File inputFile = new File(SOURCE_FILE_PATH + "/" + PINAGENT_WAR_FILE );
        File outputFile = new File(CONFIGURATION_DESTINATION_PATH_START + "/" + lowerCaseHostname + "/" + PINAGENT_WAR_FILE);
		try
		{
			   FileCopy.copyFile(inputFile, outputFile);
		}
		catch (IOException e)
		{
			 
			 System.out.println("Could not copy PINAgent" );
			 return false;
		}
     
        // Delete the original file
        FileDelete.deleteFile(inputFile);
        System.out.println("Added host "+ hostnameLowerCase);
       return true;

    }
    

   

    
    
}
