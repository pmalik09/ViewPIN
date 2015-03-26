package com.safenetinc.viewpin.cli;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.log4j.Logger;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.Hashtable;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.safenetinc.viewpin.cli.PinAuthorityConfigurationElements;
import com.safenetinc.viewpin.cli.ConfigurationConstants;
import com.safenetinc.viewpin.cli.ReadPinAuthorityConfiguration;
import com.safenetinc.Common;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import com.safenetinc.viewpin.cli.exception.ServerXmlException;


/**
*  class to view the PinAuthority Configuration elements 
*/

public class ViewPinAuthorityConfiguration {
	
	private static final String APPLICATION_NAME	=			"ViewPinAuthority";
	
	private static final String SSL = "SSL";
	
	private static final String     CONFIG_PATH_TO_SERVICE         = "Service(0)";

    private static final String     CONFIG_PATH_TO_ENGINE          = CONFIG_PATH_TO_SERVICE + ".Engine(0)";

    private static final String     CONFIG_PATH_TO_HOST_NAMES      = CONFIG_PATH_TO_ENGINE + ".Host[@name]";

    private static final String     CONFIG_PATH_TO_CONNECTOR_PORTS = CONFIG_PATH_TO_SERVICE + ".Connector[@port]";

    private static final String     CONFIG_PATH_TO_CONNECTOR       = CONFIG_PATH_TO_SERVICE + ".Connector(";

    private static final String     CONFIG_PATH_TO_HOST            = CONFIG_PATH_TO_ENGINE + ".Host(";

    private static final String     SERVER_XML_LOCATION            = "/usr/tomcat/conf/server.xml";

    private static final String     SERVER_XML_BACKUP_LOCATION     = "/usr/tomcat/conf/server.xml.orig";
	
	private static final int    HIGH_PORT_NUMBER_RANGE          		 = 65536;

    private static final int    LOW_PORT_NUMBER_RANGE           		 = 1024;
	
	PinAuthorityConfigurationElements pinAuthorityConfigurationElements = new PinAuthorityConfigurationElements();
	
	ConfigurationConstants configurationConstants = new ConfigurationConstants();
	
	ReadPinAuthorityConfiguration readPinAuthorityConfiguration = new ReadPinAuthorityConfiguration();
	
	private InputStreamReader isr 		= new InputStreamReader( System.in );
	
	private BufferedReader stdin		= new BufferedReader( isr );
	
	private static Logger logger 		= Logger.getLogger("viewpin.class");
	
	private static XMLConfiguration xmlConfig                      = null;

    static
    {
        // first make sure we backup the server.xml & remove the read only attribute
        try
        {
            removeServerXmlReadOnlyAttribute();
        }
        catch (ServerXmlException e)
        {
            System.err.println("Could not remove read only attribute from server.xml - this is a fatal error");
            e.printStackTrace();
        }

        // now load the server.xml file
        try
        {
            xmlConfig = new XMLConfiguration(SERVER_XML_LOCATION);
        }
        catch (ConfigurationException ce)
        {
            System.err.println("Could not load server.xml file - this is a fatal error");
            ce.printStackTrace();
        }
    }
	
	private ViewPinAuthorityConfiguration() {
    	super();
    }
    
	
	public ViewPinAuthorityConfiguration(String[] args) {
		readPinAuthorityConfiguration.readPinAuthorityConfigurationElements();
	}
    
	/**
	* Only PinAuthority commands will have main function with 
	* user supplied arguments,this command expects options 
	*/
	public static void main (final String args[])
    {
        Option key = new Option("PinAuthoritykey",false,"display the subject key Identifier");
	
		Option card = new Option("CardHolderElement",false,"display the CardHolder Information");
		
	//	Option ssl = new Option("SSL",false,"SSL Subject Key Identifier");
				
		final Options options = new Options();
        options.addOption(key);
		options.addOption(card);
   //   options.addOption(ssl);
		
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
		
		if(cmd.getArgList().size()!=0)
		{
			System.out.println("ViewPINAuthority.No option value is required to display the Attributes.");
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
		}
		
		if(args.length !=1)
		{
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
		}
		/* 
		* Initialise the value before using them 
		*/
		ViewPinAuthorityConfiguration viewpin = new ViewPinAuthorityConfiguration(args);
		
		if(cmd.hasOption("PinAuthoritykey")) {
			viewpin.listViewPINAuthorityConfiguration();
			return;
		}
		if(cmd.hasOption("CardHolderElement")){
			viewpin.ViewCardHolderInformation();
			return;
		}
		/*if(cmd.hasOption("SSL")){
			viewpin.ViewSSLSKI();
		}*/
		else {
		    final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
		}
	}
	
    public void listViewPINAuthorityConfiguration()
	{
		System.out.println(configurationConstants.PinAuthorityWrappingCertificateSKIKey + ":" + 
 			 pinAuthorityConfigurationElements.getPinAuthorityWrappingCertificateSKIKey());	 
		System.out.println(configurationConstants.PinAuthoritySigningSKI + ":" + 
 			 pinAuthorityConfigurationElements.getPinAuthoritySigningKeySKI());
		System.out.println(configurationConstants.MaximumReplayOpportunityWindow + ":" + 
 			 pinAuthorityConfigurationElements.getMaximumReplayOpportunityWindow());
	}
  
  /* Usage of CardHolder Elements */
	
	public void ViewCardHolderInformation() 
	{
		System.out.println(configurationConstants.CardHolderPINEncryption + ": " + 
			pinAuthorityConfigurationElements.getCardHolderPINEncryption());
		System.out.println(configurationConstants.CardHolderPINKeyIdentifier + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPINKeyIdentifier());
		System.out.println(configurationConstants.CardHolderPINKeyType + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPINKeyType());
		System.out.println(configurationConstants.CardHolderPINTransformation + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPINEncryptionTransformation());
		System.out.println(configurationConstants.CardHolderPAN + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPAN());
		System.out.println(configurationConstants.CardHolderPANEncryption + ": " + 
			pinAuthorityConfigurationElements.getCardHolderPANEncryption());
		System.out.println(configurationConstants.CardHolderPANKeyIdentifier + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPANKeyIdentifier());
		System.out.println(configurationConstants.CardHolderPANKeyType + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPANKeyType());
		System.out.println(configurationConstants.CardHolderPANTransformation + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderPANEncryptionTransformation());
		System.out.println(configurationConstants.CardHolderCVV + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderCVV());
		System.out.println(configurationConstants.CardHolderCVVEncryption + ": " + 
			pinAuthorityConfigurationElements.getCardHolderCVVEncryption());
		System.out.println(configurationConstants.CardHolderCVVKeyIdentifier + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderCVVKeyIdentifier());
		System.out.println(configurationConstants.CardHolderCVVKeyType + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderCVVKeyType());
		System.out.println(configurationConstants.CardHolderCVVTransformation + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderCVVEncryptionTransformation());
		System.out.println(configurationConstants.CardHolderExpiryDate + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderExpiryDate());
		System.out.println(configurationConstants.CardHolderExpiryDateEncryption + ": " + 
			pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryption());
		System.out.println(configurationConstants.CardHolderExpiryDateKeyIdentifier + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyIdentifier());
		System.out.println(configurationConstants.CardHolderExpiryDateKeyType + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyType());
		System.out.println(configurationConstants.CardHolderExpiryDateTransformation + ": " + 
 			 pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryptionTransformation());
	}
	
	public boolean ViewSSLSKI()
	{
		System.out.print("Enter Port Number,for which SKI to be Viewed: ");
		String setValue = null;
		int port = 0;
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority Port cannot be set null");
				return false;
			}
			if(setValue.length()!=0)
			{
				try
				{	
					port = Integer.parseInt(setValue);
				}
				catch (final NumberFormatException nfe)
				{
					System.out.println("Invalid Port Number.Parse Error");
					return false;
				}
        
				if (port <= LOW_PORT_NUMBER_RANGE || port > HIGH_PORT_NUMBER_RANGE)
				{
					System.out.println("Invalid Port Number.Out of range");
					return false;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		try
        {
            if (!checkForConnectorOnPort(port))
            {
                System.out.println("No connector configured on this port, please choose a port with a connector configured (use command \"websvc server port addSecure\" to add a port)");
                return false;
            }
			Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
			String Ssl_SKI = null;
			boolean found = false;
            for (int i = 0; i < prop.size(); i++)
			{
				int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");
				if (connectorPort == port)
				{
					if(xmlConfig.getProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@web_service]")!=null)
					{
						String web_service = null;
						web_service = xmlConfig.getString(CONFIG_PATH_TO_CONNECTOR + i + ")[@web_service]");
						if(web_service != null && web_service.compareToIgnoreCase("PINAuth")==0)
						{
							Ssl_SKI = xmlConfig.getString(CONFIG_PATH_TO_CONNECTOR + i + ")[@keyAlias]");
							found = true;
							break;
						}
					}
				}
			}
			if(found)
				System.out.println("SSL Certificate: " + Ssl_SKI);
			else
				System.out.println("PINAuthority service is not configured on port: " + port);
	   }
       catch (ServerXmlException sxe)
       {
            System.err.println("Could not modify the connector " + sxe.getMessage());
            return false;
       }
	   return true;
	}
	
	/**
     * Looks for a connector on a given TCP port.
     * 
     * @param port The TCP port to look for a connector on
     * @return boolean denoting whether a connector exists for the specified port.
     * @throws ServerXmlException Thrown if reading or parsing the server.xml file failed
     */
    public static boolean checkForConnectorOnPort (int port) throws ServerXmlException
    {
        // Count the number of connectors
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        // Check that there isn't already a connector on our port
        for (int i = 0; i < prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");

            if (connectorPort == port)
                return true;
        }
        return false;
    }
	
		/**
     * Removes the read only attribute from server.xml. This attribute is set by default with a fresh Luna SP
     * jail installation. Java doesn't provide a way of changing the read only attribute (only setting it) so
     * this method renames the file before copying it back to its original name. Removing the read only
     * attribute is a side effect of this process.
     * 
     * @throws ServerXmlException If modifying the file was not possible.
 */

	public static void removeServerXmlReadOnlyAttribute () throws ServerXmlException
    {
        // First lets rename the original server.xml
        File serverxml = new File(SERVER_XML_LOCATION);
        File serverxmlBackup = new File(SERVER_XML_BACKUP_LOCATION);
        boolean success = serverxml.renameTo(serverxmlBackup);
        if (!success)
        {
            System.err.println("Unable to rename the server.xml file");
        }

        /*
         * Now copy the contents of the backup back to the original filename thus removing the read only
         * attribute
         */
        File inputFile = new File(SERVER_XML_BACKUP_LOCATION);
        File outputFile = new File(SERVER_XML_LOCATION);

        try
        {
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;

            while ((c = in.read()) != -1)
                out.write(c);

            in.close();
            out.close();
        }
        catch (FileNotFoundException fnfe)
        {
            throw new ServerXmlException(fnfe.toString());
        }
        catch (IOException ioe)
        {
            throw new ServerXmlException(ioe.toString());
        }

    }
}