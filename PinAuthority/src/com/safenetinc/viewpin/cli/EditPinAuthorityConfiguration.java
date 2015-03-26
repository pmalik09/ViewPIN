package com.safenetinc.viewpin.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;

import java.lang.Number;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

import com.safenetinc.viewpin.cli.PinAuthorityConfigurationElements;
import com.safenetinc.viewpin.cli.ConfigurationConstants;
import com.safenetinc.viewpin.cli.ReadPinAuthorityConfiguration;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;
                                      
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
* class to edit the PinAuthorityConfiguration Elements
*/

public class EditPinAuthorityConfiguration {
	
	PinAuthorityConfigurationElements pinAuthorityConfigurationElements = new PinAuthorityConfigurationElements();
	
	ConfigurationConstants configurationConstants = new ConfigurationConstants();
	ReadPinAuthorityConfiguration readPinAuthorityConfiguration = new ReadPinAuthorityConfiguration();
	
	// Parameters to configur SSL element for PinAuthority.
	private static final String     CONFIG_PATH_TO_SERVICE         = "Service(0)";

    private static final String     CONFIG_PATH_TO_ENGINE          = CONFIG_PATH_TO_SERVICE + ".Engine(0)";

    private static final String     CONFIG_PATH_TO_HOST_NAMES      = CONFIG_PATH_TO_ENGINE + ".Host[@name]";

    private static final String     CONFIG_PATH_TO_CONNECTOR_PORTS = CONFIG_PATH_TO_SERVICE + ".Connector[@port]";

    private static final String     CONFIG_PATH_TO_CONNECTOR       = CONFIG_PATH_TO_SERVICE + ".Connector(";

    private static final String     CONFIG_PATH_TO_HOST            = CONFIG_PATH_TO_ENGINE + ".Host(";

    private static final String     SERVER_XML_LOCATION            = "/usr/tomcat/conf/server.xml";

    private static final String     SERVER_XML_BACKUP_LOCATION     = "/usr/tomcat/conf/server.xml.orig";

    private static final String     LOCALHOST                      = "localhost";
	
	private static final int    HIGH_PORT_NUMBER_RANGE          		 = 65536;

    private static final int    LOW_PORT_NUMBER_RANGE           		 = 1024;
		
	private static final String APPLICATION_NAME 		= "EditPinAuthority";
	
	private static final String PINAUTHORITY_ELEMENT	= "PinAuthoritykey";
	
	private static final String CARD_HOLDER_ELEMENT 	= "CardHolderElement";
	
	private static final String SSL_ELEMENT	 			= "SSL";
	
	private static int LOW_REPLAY_WINDOW = 1000;
	
	private static int HIGH_REPLAY_WINDOW = 600000;
	
	private InputStreamReader isr 		= new InputStreamReader( System.in );
	
	private BufferedReader stdin		= new BufferedReader( isr );
	
	private static Logger logger 		= Logger.getLogger("editpin.class");
	
	
	private static CommandLine cmd 		= null;
	private static Options options 		= null;
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
	
	public EditPinAuthorityConfiguration(String[] args) 
	{	
		if(updatePinAuthorityConfiguration(args)==false) 
		{
			System.out.println("Edit configuration failed.");
		}
		else
		{
			
			System.out.println("Edit configuration successful.");
		}
	}
	
	
	
	/**
	* main function to interact with user
	*/
	public static void main(String[] args)
	{
		
		boolean isLoggedIn = false;
		
		final CommandLineParser parser = new GnuParser();
		OptionBuilder.withArgName(PINAUTHORITY_ELEMENT);
		OptionBuilder.withDescription("PIN Authority key element");
		final Option keyelement = OptionBuilder.create(PINAUTHORITY_ELEMENT);
		
		OptionBuilder.withArgName(CARD_HOLDER_ELEMENT);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("card holder data element");
		final Option cardholder = OptionBuilder.create(CARD_HOLDER_ELEMENT);
		
		/*OptionBuilder.withArgName(SSL_ELEMENT);
		OptionBuilder.withDescription("PIN Authority SSL element");
		final Option sslelement = OptionBuilder.create(SSL_ELEMENT);*/
		
		options = new Options();
		
		options.addOption(keyelement);
		options.addOption(cardholder);
		try
		{
			
			cmd = parser.parse(options, args);
			
		}
		catch (final MissingOptionException e)
		{
			logger.error(e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			System.exit(0) ;
		}
		catch(final MissingArgumentException e)
		{
				logger.error(e.getMessage());
				System.out.println("Usage: EditPinAuthority -CardHolderElement [pin | pan | expirydate | cvv]");
				System.exit(0) ;
				
		}
		catch(final UnrecognizedOptionException e)
		{
			logger.error(e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			System.exit(0) ;
		}
		catch(final AlreadySelectedException e)
		{
			logger.error(e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			System.exit(0) ;
		}
		catch(final ParseException e)
		{
			logger.error(e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			System.exit(0) ;
		}
		if ( (cmd.hasOption(PINAUTHORITY_ELEMENT) == false) &&
				(cmd.hasOption(CARD_HOLDER_ELEMENT) == false ))
		{
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            System.exit(0) ;
		}
		if(cmd.hasOption(PINAUTHORITY_ELEMENT) && cmd.getArgList().size()!=0)
		{
			System.out.println("EditPINAuthorityConfiguration.No option value is required to set the PinAuthorityKey Attributes.");
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
           System.exit(0) ;
		}
		isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  System.exit(0) ;
		}
		
		
		new EditPinAuthorityConfiguration(args);
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
		
	}
	
	/**
	@param args takes the user input arguments
	*/
	public boolean updatePinAuthorityConfiguration(String[] args) 
	{

		if(false == readPinAuthorityConfiguration.readPinAuthorityConfigurationElements())
		{
			logger.error("Edit PINAuthority Configuration failed,Could not read PIN Authority Configuration elements");
			return false;
		}
		
		/*if(cmd.hasOption(SSL_ELEMENT) && cmd.getArgList().size()!=0)
		{
			System.out.println("EditPINAuthorityConfiguration.No option value is required to set the SSL Attributes.");
			final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return false;
		}*/
		
		
		
		if(cmd.hasOption(PINAUTHORITY_ELEMENT)&& cmd.hasOption(CARD_HOLDER_ELEMENT)==false) 
		{
			return updatePinAuthorityKeyElement();
		}
		/*else if(cmd.hasOption(SSL_ELEMENT)&& (cmd.hasOption(PINAUTHORITY_ELEMENT)==false)&&
				(cmd.hasOption(CARD_HOLDER_ELEMENT)==false))
		{
			return updatePinAuthoritySSLElement();
		}*/
		else if(cmd.hasOption(CARD_HOLDER_ELEMENT)&&cmd.hasOption(PINAUTHORITY_ELEMENT)==false /*&& cmd.hasOption(SSL_ELEMENT)==false*/) 
		{
			if(cmd.getOptionValue(CARD_HOLDER_ELEMENT).equalsIgnoreCase("pan"))
			{
				return updatePinAuthorityPANElement();
			}
			if(cmd.getOptionValue(CARD_HOLDER_ELEMENT).equalsIgnoreCase("cvv"))
			{
				return updatePinAuthorityCVVElement();
			}
			if(cmd.getOptionValue(CARD_HOLDER_ELEMENT).equalsIgnoreCase("expirydate"))
			{
				return updatePinAuthorityExpiryDateElement();
			}
			if(cmd.getOptionValue(CARD_HOLDER_ELEMENT).equalsIgnoreCase("pin"))
			{
				return updatePinAuthorityPINElement();
			}
			else
			{
				System.out.println("Incorrect argument passed with cardholder option");
				return false;
			}
		}
		else if(cmd.hasOption(PINAUTHORITY_ELEMENT)==false && cmd.hasOption(CARD_HOLDER_ELEMENT)==false /*&& cmd.hasOption(SSL_ELEMENT)==false*/) 
		{
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			return false;
		}
		else 
		{
			System.out.println("you can set either Key element or CardHolder element");
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(APPLICATION_NAME, options);
			return false;
		}
	}
	/**
	* Interface to update PinAuthorityConfiguration Signing,Wrapping and ReplayWindowOpportunity element
	*/
	public boolean updatePinAuthorityKeyElement() 
	{
		String setValue = null;
		
		System.out.println(configurationConstants.PinAuthoritySigningSKI + "[" +
			pinAuthorityConfigurationElements.getPinAuthoritySigningKeySKI()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority Signing Certificate SKI cannot be set null.");
				return false;
			}
			// put code for checking aplha-numeric character
			if(setValue.length() != 0) {
				if(false == isAlphaNumerickey(setValue))
				{
					System.out.println("Only alphanumeric value for PIN Authority signing certificate SKI are permitted");
					return false;
				}
				pinAuthorityConfigurationElements.setPinAuthoritySigningKeySKI(setValue.trim());
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		setValue = null;
		System.out.println(configurationConstants.PinAuthorityWrappingCertificateSKIKey + "[" +
			pinAuthorityConfigurationElements.getPinAuthorityWrappingCertificateSKIKey()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority Signing Certificate SKI cannot be set null");
				return false;
			}
			// put code for checking aplha-numeric character
			if(setValue.length() != 0){
				if(false == isAlphaNumerickey(setValue))
				{
					System.out.println("Only alphanumeric value for PIN Authority wrapping certificate SKI are permitted");
					return false;
				}
				pinAuthorityConfigurationElements.setPinAuthorityWrappingCertificateSKIKey(setValue.trim());
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		setValue = null;
		System.out.println(configurationConstants.MaximumReplayOpportunityWindow + "[" +
			pinAuthorityConfigurationElements.getMaximumReplayOpportunityWindow()+ "][Min = 1000 Max = 600000]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority Maximum Replay Oppurtunities window cannot be set null.");
				return false;
			}
			if(setValue.length() != 0){
				int m;
				try {
					m = Integer.parseInt(setValue);
				}catch (NumberFormatException e) {
					System.out.println("Parse Integer error.");
					return false;
				}
				if((m >= LOW_REPLAY_WINDOW) && (m <= HIGH_REPLAY_WINDOW)) {
					pinAuthorityConfigurationElements.setMaximumReplayOpportunityWindow(setValue.trim());
				}else {
					System.out.println("Invalid Maximum Replay Oppurtunities window,Usage:Integer with valid value are allowed");
					return false;
				}
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		if(updatePinAuthorityConfigurationFile())
			return true;
		else
			return false;
	}
	
	/** 
	* Interface to update the SSL file for PINAuthority
	*/
	public boolean updatePinAuthoritySSLElement()
	{
		System.out.print("Port to be configured: ");
		String setValue = null;
		int portValue = 0;
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
					portValue = Integer.parseInt(setValue);
				}
				catch (final NumberFormatException nfe)
				{
					System.out.println("Invalid Port Number.Parse Error");
					return false;
				}
        
				if (portValue <= LOW_PORT_NUMBER_RANGE || portValue > HIGH_PORT_NUMBER_RANGE)
				{
					System.out.println("Invalid Port Number.Out of range");
					return false;
				}
			}
			else
			{
				System.out.println("Please provide the valid Port Number");
				return false;
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		String ssl_certificate = null;
		setValue = null;	
		System.out.print("Certificate to be added: ");
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority SSL Certificate cannot be set null");
				return false;
			}
			if(setValue.length() != 0 )
			{
				ssl_certificate = setValue;
				if(isAlphaNumerickey(ssl_certificate.trim())==false) 
				{
					logger.error("Value can't be set to " + ssl_certificate);
					logger.error("Permitted value are Alpha Numeric Character.");
					return false;
				}
			}
			else
			{
				System.out.println("Please provide Valid SSL certificate.");
				return false;
			}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		boolean success = false;
		// Interface will add a new tag in Connector for PinAuthority Identification,will help in looking for PinAuthority Port Search.
		try 
		{
			success = editPinAuthoritySsl(portValue, ssl_certificate);
		}
		catch(ServerXmlException e) 
		{
			System.err.println("Could not modify the connector " + e.getMessage());
			return false;
		}
		if(success)
		{
			System.out.println("Server XML file updated successfully.");
			return true;
		}
		else 
		{
			return false;
		}
		
	}
	/**
	* Interface to update PinAuthorityConfiguration PAN Element
	*/
	public boolean updatePinAuthorityPANElement(){
		String setValue = null;
		System.out.println(configurationConstants.CardHolderPAN + "[" +
			pinAuthorityConfigurationElements.getCardHolderPAN()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority PAN cannot be set null");
				return false;
			}
			if(setValue.length() != 0 )
				if(isAlphaNumerickey(setValue.trim())==false) 
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are Alpha Numeric Character.");
					return false;
				}
				else
					pinAuthorityConfigurationElements.setCardHolderPAN(setValue.trim());
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		setValue = null;
		System.out.println(configurationConstants.Encrypted + "[" +
			pinAuthorityConfigurationElements.getCardHolderPANEncryption()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority PAN Encryption Scheme cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if (setValue.compareToIgnoreCase("true")==0 || setValue.compareToIgnoreCase("false")==0) 
				{
					pinAuthorityConfigurationElements.setCardHolderPANEncryption(setValue.trim());
				}
				else
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are true/false.");
					return false;
				}
			}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		if(pinAuthorityConfigurationElements.getCardHolderPANEncryption().compareToIgnoreCase("true")==0) {
			setValue = null;
			System.out.println(configurationConstants.KeyIdentifier + "[" +
				pinAuthorityConfigurationElements.getCardHolderPANKeyIdentifier()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PAN Key Identifier cannot be set null");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPANKeyIdentifier(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;	
			}
		
			setValue = null;
			System.out.println(configurationConstants.KeyType + "[" +
				pinAuthorityConfigurationElements.getCardHolderPANKeyType()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PAN Key Type cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPANKeyType(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}	
		
			setValue = null;
			System.out.println(configurationConstants.Transformation + "[" +
				pinAuthorityConfigurationElements.getCardHolderPANEncryptionTransformation()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PAN Encryption Transformation Algorithm cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPANEncryptionTransformation(setValue.trim());
			
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		
		if(updatePinAuthorityConfigurationFile())
			return true;
		else
			return false;
	}
	/**
	* Interface to update the PinAuthorityConfiguration CVV Element
	*/
	public boolean updatePinAuthorityCVVElement(){
		String setValue = null;
		System.out.println(configurationConstants.CardHolderCVV + "[" +
			pinAuthorityConfigurationElements.getCardHolderCVV()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority CVV cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isAlphaNumerickey(setValue.trim())==false)
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are Alpha Numeric Character.");
					return false;
				}
				else
					pinAuthorityConfigurationElements.setCardHolderCVV(setValue.trim());
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		setValue = null;
		System.out.println(configurationConstants.Encrypted + "[" +
			pinAuthorityConfigurationElements.getCardHolderCVVEncryption()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority CVV Encryption Scheme cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if (setValue.compareToIgnoreCase("true")==0 || setValue.compareToIgnoreCase("false")==0)
				{
						pinAuthorityConfigurationElements.setCardHolderCVVEncryption(setValue.trim());
				}
				else
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are true/false.");
					return false;
				}
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		if(pinAuthorityConfigurationElements.getCardHolderCVVEncryption().compareToIgnoreCase("true")==0) {
			setValue = null;
			System.out.println(configurationConstants.KeyIdentifier + "[" +
				pinAuthorityConfigurationElements.getCardHolderCVVKeyIdentifier()+ "]" + ":" );
				
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority CVV Key Identifier cannot be set null");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderCVVKeyIdentifier(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.KeyType + "[" +
				pinAuthorityConfigurationElements.getCardHolderCVVKeyType()+ "]" + ":" );
				
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority CVV Key Type cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderCVVKeyType(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.Transformation + "[" +
				pinAuthorityConfigurationElements.getCardHolderCVVEncryptionTransformation()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority CVV Encryption Transformation Algorithm cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderCVVEncryptionTransformation(setValue.trim());
			}
		
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		
		if(updatePinAuthorityConfigurationFile())
			return true;
		else
			return false;
	}
	/** 
	* Interface to update the PinAuthorityConfiguration ExpiryDate Element
	*/
	public boolean updatePinAuthorityExpiryDateElement(){
		String setValue = null;
		System.out.println(configurationConstants.CardHolderExpiryDate + "[" +
			pinAuthorityConfigurationElements.getCardHolderExpiryDate()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority ExpiryDate cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isAlphaNumerickey(setValue.trim())==false)
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are Alpha Numeric Character.");
					return false;
				}
				else
					pinAuthorityConfigurationElements.setCardHolderExpiryDate(setValue.trim());
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		setValue = null;
		System.out.println(configurationConstants.Encrypted + "[" +
			pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryption()+ "]" + ":" );
				
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Authority Expiry Date Encryption Scheme cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(setValue.compareToIgnoreCase("true")==0 || setValue.compareToIgnoreCase("false")==0)
				{
					pinAuthorityConfigurationElements.setCardHolderExpiryDateEncryption(setValue.trim());
				}
				else
				{
					logger.error("Value can't be set to " + setValue);
					logger.error("Permitted value are true/false.");
					return false;
				}
			}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		   	return false;
		}
		
		if(pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryption().compareToIgnoreCase("true")==0) 		  {
			setValue = null;
			System.out.println(configurationConstants.KeyIdentifier + "[" +
				pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyIdentifier()+ "]" + ":" );
				
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority ExpiryDate Key Identifier cannot be set null");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderExpiryDateKeyIdentifier(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.KeyType + "[" +
				pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyType()+ "]" + ":" );
				
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority Expiry Date Key Type cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderExpiryDateKeyType(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.Transformation + "[" +
				pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryptionTransformation()+ "]" + ":" );
				
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority ExpiryDate Encryption Transformation Algorithm cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderExpiryDateEncryptionTransformation(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		if(updatePinAuthorityConfigurationFile())
			return true;
		else
			return false;
		
	}
	/** 
	* Interface to update the PinAuthorityConfiguration PIN Element
	*/
	public boolean updatePinAuthorityPINElement(){
		String setValue = null;
		if(pinAuthorityConfigurationElements.getCardHolderPINEncryption().compareToIgnoreCase("true")==0) {
			setValue = null;
			System.out.println(configurationConstants.KeyIdentifier + "[" +
				pinAuthorityConfigurationElements.getCardHolderPINKeyIdentifier()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PIN Key Identifier cannot be set null");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPINKeyIdentifier(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.KeyType + "[" +
				pinAuthorityConfigurationElements.getCardHolderPINKeyType()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PIN Key Type cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPINKeyType(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		
			setValue = null;
			System.out.println(configurationConstants.Transformation + "[" +
				pinAuthorityConfigurationElements.getCardHolderPINEncryptionTransformation()+ "]" + ":" );
			try
			{
				setValue = stdin.readLine();
				if(setValue == null)
				{
					System.out.println("PIN Authority PIN Encryption Transformation Algorithm cannot be set null.");
					return false;
				}
				if(setValue.length() != 0)
					if(isAlphaNumeric(setValue)==false)
						return false;
					else
						pinAuthorityConfigurationElements.setCardHolderPINEncryptionTransformation(setValue.trim());
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				return false;
			}
		}
		
		if(updatePinAuthorityConfigurationFile())
			return true;
		else
			return false;
	}
	/**
	* Interface to update the PinAuthorityConfiguration file and write on disk
	*/
	public boolean updatePinAuthorityConfigurationFile()
	{
		
		Element rootElement = null;
		Element pinAuthorityNameElement = null;
		
		Element pinAuthorityMaxFailedAttemptsElement = null;
		Element pinAuthoritySigningKeySKIElement = null;
		Element PinAuthorityWrappingCertificateSubjectKeyIdentifierElement = null;
		Element MaximumReplayOpportunityWindowElement = null;
		Element CardHolderDataElements = null;
		Element PANElement = null;
		Element CVVElement = null;
		Element ExpiryDateElement = null;
		Element PINElement = null;
		
		Element NameElement = null;
		Element EncryptedElement = null;
		Element EncryptionPropertiesElement = null;
		Element KeyIdentifierElement = null;
		Element KeyTypeElement = null;
		Element TransformationElement = null;
		
		
		 // Instantiate document builder factory
		DocumentBuilderFactory dbf= null;
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false);
		
		DocumentBuilder db = null;
		try
		{
			 // Instantiate document builder
			db = dbf.newDocumentBuilder();
		}
		catch (Exception e)
		{	
			System.out.println("exception");
			e.printStackTrace();
		}

		// instantiate the document
		Document pinAuthConfDocument = null;
		pinAuthConfDocument = db.newDocument();
        
		//create the root element
		rootElement = pinAuthConfDocument.createElement("PinAuthorityConfiguration");
		//append the rootELement to the Document
		pinAuthConfDocument.appendChild(rootElement);
    	
    			
		pinAuthoritySigningKeySKIElement = pinAuthConfDocument.createElement( configurationConstants.PinAuthoritySigningKeySKI);
		pinAuthoritySigningKeySKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getPinAuthoritySigningKeySKI()));
		rootElement.appendChild(pinAuthoritySigningKeySKIElement);
		
		PinAuthorityWrappingCertificateSubjectKeyIdentifierElement = pinAuthConfDocument.createElement(configurationConstants.PinAuthorityWrappingCertificateSubjectKeyIdentifier);
					PinAuthorityWrappingCertificateSubjectKeyIdentifierElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getPinAuthorityWrappingCertificateSKIKey()));
		rootElement.appendChild(PinAuthorityWrappingCertificateSubjectKeyIdentifierElement);
		
		pinAuthorityMaxFailedAttemptsElement = pinAuthConfDocument.createElement(configurationConstants.MaximumFailedAuthenticationAttempts);
		pinAuthorityMaxFailedAttemptsElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getMaxFailedAttempts()));
		rootElement.appendChild(pinAuthorityMaxFailedAttemptsElement);
		
		MaximumReplayOpportunityWindowElement = pinAuthConfDocument.createElement(configurationConstants.MaximumReplayOpportunityWindow);
		MaximumReplayOpportunityWindowElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getMaximumReplayOpportunityWindow()));
		rootElement.appendChild(MaximumReplayOpportunityWindowElement);
		
		CardHolderDataElements = pinAuthConfDocument.createElement( configurationConstants.CardHolderDataElements);
		rootElement.appendChild(CardHolderDataElements);
		
		
		PANElement = pinAuthConfDocument.createElement(configurationConstants.PANElement);
		CardHolderDataElements.appendChild(PANElement);
		
		NameElement = pinAuthConfDocument.createElement(configurationConstants.Name);
		NameElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPAN()));
		PANElement.appendChild(NameElement);
				
		EncryptedElement = pinAuthConfDocument.createElement(configurationConstants.Encrypted);
		EncryptedElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPANEncryption()));
		PANElement.appendChild(EncryptedElement);
				
		EncryptionPropertiesElement = pinAuthConfDocument.createElement(configurationConstants.EncryptionProperties);
		PANElement.appendChild(EncryptionPropertiesElement);
		
		KeyIdentifierElement = pinAuthConfDocument.createElement(configurationConstants.KeyIdentifier);
		KeyIdentifierElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPANKeyIdentifier()));
		EncryptionPropertiesElement.appendChild(KeyIdentifierElement);
			
		KeyTypeElement = pinAuthConfDocument.createElement(configurationConstants.KeyType);
		KeyTypeElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPANKeyType()));
		EncryptionPropertiesElement.appendChild(KeyTypeElement);
				
		TransformationElement = pinAuthConfDocument.createElement(configurationConstants.Transformation);
		TransformationElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPANEncryptionTransformation()));
		EncryptionPropertiesElement.appendChild(TransformationElement);
		
		
		
		CVVElement = pinAuthConfDocument.createElement(configurationConstants.CVVElement);
		CardHolderDataElements.appendChild(CVVElement);
		
		NameElement = pinAuthConfDocument.createElement(configurationConstants.Name);
		NameElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderCVV()));
		CVVElement.appendChild(NameElement);
				
		EncryptedElement = pinAuthConfDocument.createElement(configurationConstants.Encrypted);
		EncryptedElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderCVVEncryption()));
		CVVElement.appendChild(EncryptedElement);
				
		EncryptionPropertiesElement = pinAuthConfDocument.createElement(configurationConstants.EncryptionProperties);
		CVVElement.appendChild(EncryptionPropertiesElement);
		
		KeyIdentifierElement = pinAuthConfDocument.createElement(configurationConstants.KeyIdentifier);
		KeyIdentifierElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderCVVKeyIdentifier()));
		EncryptionPropertiesElement.appendChild(KeyIdentifierElement);
			
		KeyTypeElement = pinAuthConfDocument.createElement(configurationConstants.KeyType);
		KeyTypeElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderCVVKeyType()));
		EncryptionPropertiesElement.appendChild(KeyTypeElement);
				
		TransformationElement = pinAuthConfDocument.createElement(configurationConstants.Transformation);
		TransformationElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderCVVEncryptionTransformation()));
		EncryptionPropertiesElement.appendChild(TransformationElement);
				

		

		ExpiryDateElement = pinAuthConfDocument.createElement(configurationConstants.ExpiryDateElement);
		CardHolderDataElements.appendChild(ExpiryDateElement);
		
		NameElement = pinAuthConfDocument.createElement(configurationConstants.Name);
		NameElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderExpiryDate()));
		ExpiryDateElement.appendChild(NameElement);
		EncryptedElement = pinAuthConfDocument.createElement(configurationConstants.Encrypted);
		EncryptedElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryption()));
		ExpiryDateElement.appendChild(EncryptedElement);
		EncryptionPropertiesElement = pinAuthConfDocument.createElement(configurationConstants.EncryptionProperties);
		ExpiryDateElement.appendChild(EncryptionPropertiesElement);
		KeyIdentifierElement = pinAuthConfDocument.createElement(configurationConstants.KeyIdentifier);
		KeyIdentifierElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyIdentifier()));
		EncryptionPropertiesElement.appendChild(KeyIdentifierElement);
		KeyTypeElement = pinAuthConfDocument.createElement(configurationConstants.KeyType);
		KeyTypeElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderExpiryDateKeyType()));
		EncryptionPropertiesElement.appendChild(KeyTypeElement);
		TransformationElement = pinAuthConfDocument.createElement(configurationConstants.Transformation);
		TransformationElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderExpiryDateEncryptionTransformation()));
		EncryptionPropertiesElement.appendChild(TransformationElement);
    	

		PINElement = pinAuthConfDocument.createElement(configurationConstants.PINElement);
		CardHolderDataElements.appendChild(PINElement);
		
		NameElement = pinAuthConfDocument.createElement(configurationConstants.Name);
		NameElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPIN()));
		PINElement.appendChild(NameElement);
		EncryptedElement = pinAuthConfDocument.createElement(configurationConstants.Encrypted);
		EncryptedElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPINEncryption()));
		PINElement.appendChild(EncryptedElement);
		EncryptionPropertiesElement = pinAuthConfDocument.createElement(configurationConstants.EncryptionProperties);
		PINElement.appendChild(EncryptionPropertiesElement);
		KeyIdentifierElement = pinAuthConfDocument.createElement(configurationConstants.KeyIdentifier);
		KeyIdentifierElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPINKeyIdentifier()));
		EncryptionPropertiesElement.appendChild(KeyIdentifierElement);
		KeyTypeElement = pinAuthConfDocument.createElement(configurationConstants.KeyType);
		KeyTypeElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPINKeyType()));
		EncryptionPropertiesElement.appendChild(KeyTypeElement);
		TransformationElement = pinAuthConfDocument.createElement(configurationConstants.Transformation);
		TransformationElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthorityConfigurationElements.getCardHolderPINEncryptionTransformation()));
		EncryptionPropertiesElement.appendChild(TransformationElement);
		try
		{	
			OutputFormat format = new OutputFormat(pinAuthConfDocument);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
	
			new FileOutputStream(new File(configurationConstants.WebApp_Directory +configurationConstants.PinAuthority_Directory+configurationConstants.New_Authority_Config_File)), format);
			serializer.serialize(pinAuthConfDocument);
			try
			{
				FileHandler.copyFile(new File(configurationConstants.WebApp_Directory +configurationConstants.PinAuthority_Directory+ configurationConstants.New_Authority_Config_File),
							new File(configurationConstants.WebApp_Directory +configurationConstants.PinAuthority_Directory+ configurationConstants.Authority_Config_File));
			
			}
			catch(IOException e)
			{
				System.out.println("Could not Update Configuration File");
				return false;
			}
			
			
			if (false == FileHandler.deleteFile(configurationConstants.WebApp_Directory +configurationConstants.PinAuthority_Directory+ configurationConstants.New_Authority_Config_File))
			{
				System.out.println("Could not Update Configuration File");
				return false;
			}
		
		}
		catch (Exception e)
		{
			logger.error("Could not Update PINAuthority Configuration File");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static Logger getLogger ()
	{
	    return logger;
	}
	
	private boolean isAlphaNumeric(String s) {
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) continue; 
		   if ((c >= 'A') && (c <= 'Z')) continue; 
		   if ((c >= '0') && (c <= '9')) continue; 
		   if (c == '/') continue;
		   return false;
		 }  
		 return true;
	}
	
	private boolean isAlphaNumerickey(String s) {
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) continue; 
		   if ((c >= 'A') && (c <= 'Z')) continue; 
		   if ((c >= '0') && (c <= '9')) continue; 
		   return false;
		 }  
		 return true;
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

    /**
     * Adds a SSL entry to server.xml. 
     * @param name The name of the virtual host
     * @param port The port that users will access this virtual host from
     * @throws ServerXmlException
     */
    public static boolean editPinAuthoritySsl(int port, String ssl_ski) throws ServerXmlException
    {
		  
	   try
       {
            if (!checkForConnectorOnPort(port))
            {
                System.out.println("No connector configured on this port, please choose a port with a connector configured (use command \"websvc server port addSecure\" to add a port)");
                return false;
            }
			// add a new tag for PinAuthority.
			modifyConnector(port);
            // Modify the connector
            changeCertificateForConnector(port, ssl_ski);
       }
       catch (ServerXmlException sxe)
       {
            System.err.println("Could not modify the connector " + sxe.getMessage());
            return false;
       }
	   return true;

    }

/**
     * Modifies a connector to specify a different SSL certificate. Certificates are located by their
     * associated Subject Key Identifiers
     * 
     * @param port The TCP port for the connector to be edited
     * @param certificateAlias The Subject Key Identifier for the SSL certificate the connector will use
     * @throws ServerXmlException Thrown if reading, parsing or writing to the server.xml failed for some
     *         reason
     */
    public static boolean changeCertificateForConnector (int port, String certificateAlias) throws ServerXmlException
    {

        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        boolean changed = false;
        for (int i = 0; i < prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");
            if (connectorPort == port)
            {
                xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@keyAlias]", certificateAlias);
                changed = true;
            }
        }

        if (!changed)
            throw new ServerXmlException("No connector on that port");
        try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
		
		return changed;
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
     * Looks for a connector on a given TCP port.
     * 
     * @param port The TCP port to look for a connector on
     * @throws ServerXmlException Thrown if reading or parsing the server.xml file failed
     */
	public static void modifyConnector (int port) throws ServerXmlException
	{
		// Count the number of connectors
        Collection<?> prop = xmlConfig.getList(CONFIG_PATH_TO_CONNECTOR_PORTS);
        // Check that there isn't already a connector on our port
        for (int i = 0; i < prop.size(); i++)
        {
            int connectorPort = xmlConfig.getInt(CONFIG_PATH_TO_CONNECTOR + i + ")[@port]");

            if (connectorPort == port) 
			{
					xmlConfig.setProperty(CONFIG_PATH_TO_CONNECTOR + i + ")[@web_service]", "PINAuth");
					break;
			}
		}
		
		try
        {
            xmlConfig.save();
        }
        catch (ConfigurationException ce)
        {
            throw new ServerXmlException(ce.toString());
        }
	}
}