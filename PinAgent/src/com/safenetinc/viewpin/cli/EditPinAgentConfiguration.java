package com.safenetinc.viewpin.cli; 



import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import java.io.*;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.Common;

/**
 * Class to edit the PINAgent Configuration file
 * @author Pratibha Malik
 */

public class EditPinAgentConfiguration {
	
	PinAgentConfigurationElements pinAgentConfigurationElements = new PinAgentConfigurationElements();
	
	Constants configurationConstants 							= new Constants();
	
	private static final String HOSTNAME_COMMAND_LINE_ARG       = "hostname";
	
	private static final String APPLICATION_NAME                = "EditHostConfiguration";
	
	private static final String ADD_PINAUTHORITY				= "addPINAuthority";

    private static final String EDIT_PINAUTHORITY		        = "editPINAuthority";

    private static final String DELETE_PINAUTHORITY		        = "delPINAuthority";

    private static final String SERVER_NAME                     = "ViewPIN";

 	static XPathReader reader 									= null;
    
 	private static LinkedList<PinAuthority> pinAuthorities 		= new LinkedList<PinAuthority>();
    
    private InputStreamReader isr 								= new InputStreamReader( System.in );
	
    private BufferedReader stdin								= new BufferedReader( isr );
    
    private static final int    HIGH_PORT_NUMBER_RANGE          = 65536;

    private static final int    LOW_PORT_NUMBER_RANGE           = 1024;
	
	private static int LOW_REPLAY_WINDOW = 1000;
	private static int HIGH_REPLAY_WINDOW = 600000;
    
    
 	
	private EditPinAgentConfiguration(String virtualHostName, boolean addPINAuthority, 
									String editPINAuthority, String delPINAuthority) 
    {
		
		
		//read PinAgent configuration file and get elements
		
		if(false == ReadPinAgentConfiguration.readPinAgentConfigurationElements())
		{
			System.out.println("Could not read PIN Agent Configuration");
        	System.out.println("EditPinAgentConfiguration failed");
        
        	return;
        }
		
		//read PinAgent configuration file and get elements
		
		if(false == ReadPinAgentConfiguration.readPinAuthorityConfigurationElements())
		{
			System.out.println("Could not read PIN Authority Configuration elements");
        	System.out.println("EditPinAgentConfiguration failed");
        	return;
        }
		
		/*
		 * if editPINAuthority is set
		 * update PIN Authority configuration elements
		 * else
		 * update PIN Agent configuration elements
		 */
		if(editPINAuthority != null)
		{
			if (false == updatePINAuthorityConfigurationElements(editPINAuthority))
			{
				System.out.println("updatePINAuthorityConfigurationElements failed");
				System.out.println("EditConfiguration Failed");
				return;
			}
		}
		else if(addPINAuthority == true)
		{
			if (false == addMultiplePINAuthority())
			{
				System.out.println("addMultiplePINAuthority failed");
				System.out.println("EditConfiguration Failed");
				return;
			}
		}
		else if(delPINAuthority != null)
		{
			if (false == deletePINAuthority(delPINAuthority))
			{
				System.out.println("deletePINAuthority failed");
				System.out.println("EditConfiguration Failed");
				return;
			}
		}
		else
		{
			if (false == updatePINAgentConfigurationElements())
			{
				System.out.println("EditConfiguration Failed");
				return;
			}
		}
				
			
		/*
		 * update PIN Agent configuration File
		 */
		if (false == updatePINAgentConfigurationFile(virtualHostName))
		{
			System.out.println("EditConfiguration Failed");
			return;
		}
		System.out.println("Edit Configuration Successful");
		return;
    }
	public static void main(String[] args)
	{
		// parse the command line arguments
        final Options options = new Options();

        OptionBuilder.withArgName(HOSTNAME_COMMAND_LINE_ARG);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("The name of the virtual host added, for example xyz.com");
        final Option hostname = OptionBuilder.create(HOSTNAME_COMMAND_LINE_ARG);
        
        OptionBuilder.withArgName(ADD_PINAUTHORITY);
        OptionBuilder.withDescription("If a new PIN Authority is to be added");
        final Option addPINAuthority = OptionBuilder.create(ADD_PINAUTHORITY);
        
        OptionBuilder.withArgName(EDIT_PINAUTHORITY);
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("To edit the configuration elements of the specified PINAuthority for the specified PINAgent");
        final Option editPINAuthority = OptionBuilder.create(EDIT_PINAUTHORITY);
        
        OptionBuilder.withArgName(DELETE_PINAUTHORITY);
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("To delete the specified PINAuthority for the specified PINAgent");
        final Option delPINAuthority = OptionBuilder.create(DELETE_PINAUTHORITY);
        
        options.addOption(hostname);
        options.addOption(addPINAuthority);
        options.addOption(editPINAuthority);
        options.addOption(delPINAuthority);
		
		
		final CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        
        try
        {
            cmd = parser.parse(options, args);
            
            //Validate that editPINAuthority, addPINAuthority or delPINAuthority are not set at same time
            if((cmd.hasOption(ADD_PINAUTHORITY) && cmd.hasOption(EDIT_PINAUTHORITY))|| 
            	(cmd.hasOption(DELETE_PINAUTHORITY)&& cmd.hasOption(EDIT_PINAUTHORITY))||
            	 (cmd.hasOption(ADD_PINAUTHORITY) && cmd.hasOption(DELETE_PINAUTHORITY)))
            {
            	System.out.println("You can either add or edit or delete PINAuthority");
            	return;
            }
        }
        catch (final ParseException e)
        {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            return;
        }
        if( false == ReadPinAgentConfiguration.checkFileStatus(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG)))
        {
        	System.out.println("Either the virtual host with this name does not exist or Configuration File is missing");
        	System.out.println("EditPinAgentConfiguration failed");
        	return;
        }
		
		boolean isLoggedIn = Common.isPartitionLoggedIn();
		if(Common.partitionAndMofnAuthentication(isLoggedIn)!=0)
		{
		  System.out.println("Authentication Failed");
		  return;
		}
		
		new EditPinAgentConfiguration(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG),cmd.hasOption(ADD_PINAUTHORITY),
										cmd.getOptionValue(EDIT_PINAUTHORITY),cmd.getOptionValue(DELETE_PINAUTHORITY));
		
		if(isLoggedIn==false)
		{
			Common.partition_logout();
		}
	
		return;
		
	}
	
	/*
	 * Update The configuration elements
	 * of PINAgent
	 */
	
	private boolean updatePINAgentConfigurationElements()
	{
		
		String setValue = null;
			
		// get value of PinAgentSigning SKI
		setValue = null;
		System.out.println(configurationConstants.PIN_Agent_Signing_Certificate_SKI + "[" +
							pinAgentConfigurationElements.getPINAgentSigningCertSKI()+ "]" + ":" );
		
		
		// if any of the input parameter is null exit
		// if the length of the input parameter is 0, then don't change
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				System.out.println("PIN Agent signing certificate SKI cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
			{	if(false == isAlphaNumeric(setValue))
				{
					System.out.println("Only alphanumeric value for PIN Agent signing certificate SKI are permitted");
					return false;
				}
				pinAgentConfigurationElements.setPINAgentSigningCertSKI(setValue.trim());
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
		
        	return false;
		}
		
		
		// get value of PinAgentSSL SKI
		setValue = null;
		System.out.println(configurationConstants.PIN_Agent_SSL_Certificate_SKI + "[" +
							pinAgentConfigurationElements.getPINAgentSSLCertSKI()+ "]" + ":" );
		
		
		// if any of the input parameter is null exit
		// if the length of the input parameter is 0, then don't change
		try
		{
			setValue = stdin.readLine();
			
			if(setValue == null)
			{
				System.out.println("PIN Agent SSL certificate SKI cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
			{	if(false == isAlphaNumeric(setValue))
				{
					System.out.println("Only alphanumeric value for PIN Agent SSL certificate SKI are permitted");
					return false;
				}
				pinAgentConfigurationElements.setPINAgentSSLCertSKI(setValue.trim());
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
			
			
        	return false;
		}
		
		// get value of PinAgentWrapping SKI
		setValue = null;
		System.out.println(configurationConstants.PIN_Agent_Wrapping_Certificate_SKI + "[" +
							pinAgentConfigurationElements.getPINAgentWrappingCertSKI()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Wrapping certificate SKI cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
			{
				if(false == isAlphaNumeric(setValue))
				{
					System.out.println("Only alphanumeric value for PIN Agent Wrapping certificate SKI are permitted");
					return false;
				}
				pinAgentConfigurationElements.setPINAgentWrappingCertSKI(setValue.trim());
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
			return false;
		}
		
		
			
		// get value of PinAgent Error Redirection URL
		setValue = null;
		System.out.println(configurationConstants.PIN_Agent_Error_Redirection_URL + "[" +
							pinAgentConfigurationElements.getPINAgentErrorRedirectionURL()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Error Redirection URL cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isValidURL(setValue)==true && (isValidHttpURL(setValue)==true || isValidHttpsURL(setValue)==true))
					pinAgentConfigurationElements.setPINAgentErrorRedirectionURL(setValue.trim());
				else
				{
					System.out.println("Please enter the valid URL format.");
					return false;
				}
			}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
			return false;
		}
		
		// get value of PinAgent Wrapped Session Key Cookie Domain
		setValue = null;
		System.out.println(configurationConstants.PIN_Agent_Wrapped_Session_Cookie_Domain + "[" +
							pinAgentConfigurationElements.getPINAgentWrappedSessionCookieDomain()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Wrapped Session Cookie Domain cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isValidDomainFormat(setValue)==false)
					return false;
				else
					pinAgentConfigurationElements.setPINAgentWrappedSessionCookieDomain(setValue.trim());
		}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
		   	return false;
		}
		
		// get value of PinAgent Retrieval Request Session Key Cookie Domain
		setValue = null;
		System.out.println(configurationConstants.PIN_Retrieval_Request_Cookie + "[" +
							pinAgentConfigurationElements.getPINRetrievalRequestCookie()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Retrieval Request Cookie Domain cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isValidDomainFormat(setValue)==false)
					return false;
				else
					pinAgentConfigurationElements.setPINRetrievalRequestCookie(setValue.trim());
		}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
	       	return false;
		}
		
		// get value of PinAgent Retrieval Response Session Key Cookie Domain
		setValue = null;
		System.out.println(configurationConstants.PIN_Retrieval_Response_Cookie + "[" +
							pinAgentConfigurationElements.getPINRetrievalResponseCookie()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Retrieval Response Cookie Domain cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isValidDomainFormat(setValue)==false)
					return false;
				else
					pinAgentConfigurationElements.setPINRetrievalResponseCookie(setValue.trim());
		}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
	       	return false;
		}
		// get value of PinAgent Change Request Session Key Cookie Domain
		setValue = null;
		System.out.println(configurationConstants.PIN_Change_Request_Cookie + "[" +
							pinAgentConfigurationElements.getPINChangeRequestCookie()+ "]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Change Request Cookie Domain cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
				if(isValidDomainFormat(setValue)==false)
					return false;
				else
					pinAgentConfigurationElements.setPINChangeRequestCookie(setValue.trim());
		}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
			 	return false;
		}
		// get value of PinAgent Replay Window
		setValue = null;
		System.out.println(configurationConstants.Replay_Window + "[" +
							pinAgentConfigurationElements.getReplayWindow()+ "][Min = 1000 Max = 600000]" + ":" );
		
		try
		{
			setValue = stdin.readLine();
			if(setValue == null)
			{
				System.out.println("PIN Agent Replay Window cannot be set null");
				return false;
			}
			if(setValue.length() != 0)
			{
				
				try
				{
					int m;
					try 
					{
						m = Integer.parseInt(setValue);
					}
					catch (NumberFormatException e) 
					{
						System.out.println("Parse Integer error.");
						return false;
					}
					if((m >= LOW_REPLAY_WINDOW) && (m <= HIGH_REPLAY_WINDOW)) 
					{
						pinAgentConfigurationElements.setReplayWindow(m);
					}
					else 
					{
						System.out.println("Invalid Replay window,Usage:Integer with valid value are allowed");
						return false;
					}	
				}
				catch (NumberFormatException e)
				{
					System.out.println("Replay Window Number not valid");
					return false;
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Could not update PINAgent ConfigurationElements");
			
        	return false;
		}
		
		return true;
	}
	
	/*
	 * Updates configuration elements of PIN Authority
	 * @param PINAuthority is the name of PINAuthority
	 * whose elements are to be updated
	 */
	private boolean updatePINAuthorityConfigurationElements(String PINAuthorityName)
	{
				
		String setValue = null;
		
		boolean authorityExists =  false;
		//get values of PinAuthority Part of PinAgent Configuration
    	int PinAuthorityCount = pinAgentConfigurationElements.getPINAuthorityCount();
    	
    	if (PinAuthorityCount == 0)
    	{
    		System.out.println("No PIN Authority found");
			return false;
    	}
    		
    	//read PinAuthrority elements
    	pinAuthorities = pinAgentConfigurationElements.getPinAuthorites();
    	
    	ListIterator<PinAuthority> iterator = pinAuthorities.listIterator();
    	
		while(iterator.hasNext())
		{

	 		PinAuthority pinAuthority = iterator.next();
	 		if(PINAuthorityName.compareToIgnoreCase(pinAuthority.getPINAuthorityName())== 0)
	 		{
	 			
	 			authorityExists = true;
	    		// get value of PINAuthority Name
				setValue = null;
				System.out.println(configurationConstants.PIN_Authority_Name + "[" +
						pinAuthority.getPINAuthorityName()+ "]" + ":" );
				
				try
				{
					setValue = stdin.readLine();
					if(setValue == null)
					{
						System.out.println("PIN Authority Name cannot be set null");
						return false;
					}
					if(setValue.length() != 0)
						if(isValidDomainFormat(setValue)==false)
							return false;
						else
							pinAuthority.setPINAuthorityName(setValue.trim());
					
				}
				catch(Exception e)
				{
					System.out.println("Could not set PIN Authority Element");
		        	return false;
				}
				
				// get value of PIN Authority  Signing Certificate Subject Key Identifier
				setValue = null;
				System.out.println(configurationConstants.PIN_Authority_Signing_Certificate_SKI + "[" +
						pinAuthority.getPINAuthoritySigningCertSKI()+ "]" + ":" );
				
				try
				{
					setValue = stdin.readLine();
					if(setValue == null)
					{
						System.out.println("PIN Authority Signing Certificate SKI cannot be set null");
						return false;
					}
					if(setValue.length() != 0)
					{
						if(false == isAlphaNumeric(setValue))
						{
							System.out.println("Only alphanumeric value for PIN Authority signing certificate SKI are permitted");
							return false;
						}
						pinAuthority.setPINAuthoritySigningCertSKI(setValue.trim());
					}
				}
				catch(Exception e)
				{
					System.out.println("Could not set PIN Authority Element");
		        	return false;
				}
				
				// get value of PIN Authority Wrapping Certificate Subject Key Identifier
				setValue = null;
				System.out.println(configurationConstants.PIN_Authority_Wrapping_Certificate_SKI + "[" +
						pinAuthority.getPINAuthorityWrappingCertSKI()+ "]" + ":" );
				
				try
				{
					setValue = stdin.readLine();
					if(setValue == null)
					{
						System.out.println("PIN Authority Wrapping Certificate SKI cannot be set null");
						return false;
					}
					if(setValue.length() != 0)
					{
						if(false == isAlphaNumeric(setValue))
						{
							System.out.println("Only alphanumeric value for PIN Authority Wrapping certificate SKI are permitted");
							return false;
						}
						pinAuthority.setPINAuthorityWrappingCertSKI(setValue.trim());
					}
				}
				catch(Exception e)
				{
					System.out.println("Could not set PIN Authority Element");
		        	return false;
				}
				
				// get value of PIN Authority Error Redirection URL
				setValue = null;
				System.out.println(configurationConstants.PIN_Authority_Error_Redirection_URL + "[" +
						pinAuthority.getPINAuthorityRedirectionURL()+ "]" + ":" );
			
				try
				{
					setValue = stdin.readLine();
					if(setValue == null)
					{
						System.out.println("PIN Authority Error Redirection URL cannot be set null");
						return false;
					}
					if(setValue.length() != 0)
						if(isValidURL(setValue)==true && (isValidHttpURL(setValue)==true || isValidHttpsURL(setValue)==true))
							pinAuthority.setPINAuthorityRedirectionURL(setValue.trim());
						else
						{
							System.out.println("Please enter the valid URL format.");
							return false;
						}
					}
				catch(Exception e)
				{
					System.out.println("Could not set PIN Authority Element");
		        	return false;
				}
	 		}
	 		if (false == authorityExists)
				System.out.println("No such PINAuthority exists");
	 		
		}
		return true;
	}
	/*
	 * addMultiplePINAuthoirty
	 * adds multiple PIN Authorities to the
	 * PIN Agent
	 */
	private boolean addMultiplePINAuthority()
	{
		String setValue = null;
		String addMultiplePINAuthorities = null;
			
		pinAuthorities = pinAgentConfigurationElements.getPinAuthorites();
		
		
		try
		{
			do
			{
		
			System.out.println("Do you want to add other PINAuthority(yes/no)?");
			addMultiplePINAuthorities = stdin.readLine();
			if(addMultiplePINAuthorities != null)
			{
					if(addMultiplePINAuthorities.compareToIgnoreCase("yes") == 0)
					{
						//set value of PINAuthority count
						int count  = pinAgentConfigurationElements.getPINAuthorityCount();
						pinAgentConfigurationElements.setPINAuthorityCount(count + 1);
						
						
						PinAuthority pinAuthority = new PinAuthority();
						// get value of PINAuthority Name
						setValue = null;
						System.out.println(configurationConstants.PIN_Authority_Name +  ":"  );
											
						try
						{
							setValue = stdin.readLine();
							if(setValue == null)
							{
								System.out.println("PIN Authority Name cannot be set null");
								return false;
							}
							if(setValue.length() != 0) 
							{
								if(isValidDomainFormat(setValue)==false)
								{
									return false;
								}
								else
									pinAuthority.setPINAuthorityName(setValue.trim());
							}
							else
							{
								System.out.println("Please enter the valid PinAuthority name");
								return false;
							}
						}
						catch(Exception e)
						{
							System.out.println("Could not set PIN Authority Element");
							return false;
						}
						
						// get value of PIN Authority  Signing Certificate Subject Key Identifier
						setValue = null;
						System.out.println(configurationConstants.PIN_Authority_Signing_Certificate_SKI + ":" );
						
						try
						{
							setValue = stdin.readLine();
							if(setValue == null)
							{
								System.out.println("PIN Authority Signing Certificate SKI cannot be set null");
								return false;
							}
							if(setValue.length() != 0)
							{
								if(isAlphaNumeric(setValue)==false)
								{
									System.out.println("Only alphanumeric value for PIN Authority Signing certificate SKI are permitted");
									return false;
								}
								else
									pinAuthority.setPINAuthoritySigningCertSKI(setValue.trim());
							}
							else
							{
								System.out.println("Please enter the valid PIN Authority Signing certificate subject key identifier");
								return false;
							}
						}
						catch(Exception e)
						{
							System.out.println("Could not set PIN Authority Element");
							return false;
						}
						
						// get value of PIN Authority Wrapping Certificate Subject Key Identifier
						setValue = null;
						System.out.println(configurationConstants.PIN_Authority_Wrapping_Certificate_SKI + ":" );
						
						try
						{
							setValue = stdin.readLine();
							if(setValue == null)
							{
								System.out.println("PIN Authority Wrapping Certificate SKI cannot be set null");
								return false;
							}
							if(setValue.length() != 0)
							{
								if(isAlphaNumeric(setValue)==false) 
								{
									System.out.println("Only alphanumeric value for PIN Authority Wrapping certificate SKI are permitted");
									return false;
								}
								else
									pinAuthority.setPINAuthorityWrappingCertSKI(setValue.trim());
							}
							else
							{
								System.out.println("Please enter the valid PIN Authority Wrapping certificate subject key identifier");
								return false;
							}
						}
						catch(Exception e)
						{
							System.out.println("Could not set PIN Authority Element");
							return false;
						}
						
						// get value of PIN Authority Error Redirection URL
						setValue = null;
						System.out.println(configurationConstants.PIN_Authority_Error_Redirection_URL + ":" );
					
						try
						{
							setValue = stdin.readLine();
							if(setValue == null)
							{
								System.out.println("PIN Authority Error Redirection URL cannot be set null");
								return false;
							}
							if(setValue.length() != 0)
							{
								if(isValidURL(setValue)==true && (isValidHttpURL(setValue)==true || isValidHttpsURL(setValue)==true))
									pinAuthority.setPINAuthorityRedirectionURL(setValue.trim());
								else
								{
									System.out.println("Please enter the valid URL format.");
									return false;
								}
							}
							else
							{
								System.out.println("Please enter the valid PIN Redirection URL");
								return false;
							}
						}
						catch(Exception e)
						{
							System.out.println("Could not set PIN Authority Element");
							return false;
						}
						
						pinAuthorities.add(pinAuthority);
					}
					else if(addMultiplePINAuthorities.compareToIgnoreCase("no") == 0)
					{
						System.out.println("Updating Agent Configuration");
					}
					else
					{
						System.out.println("enter yes or no");
						return false;
					}
			}
			else
			{
				System.out.println("enter yes or no");
				return false;
			}
				
			}//end of do
			while(addMultiplePINAuthorities.compareToIgnoreCase("yes") == 0);
		}
		catch(IOException e)
		{
			System.out.println("Could not add PIN Authority");
        	return false;
		}
		
		return true;
	}

	/*
	 * deletePINAuthorityPINAuthoirty
	 * Deletes  PIN Authority whose name is passed
	 * @param PINAuthorityName: name of the PIN Authority which is to be deleted
	 *
	 */
	private boolean deletePINAuthority(String PINAuthorityName)
	{
		String setValue = null;
		boolean authorityExists = false;
		String delPINAuthority = null;		
		pinAuthorities = pinAgentConfigurationElements.getPinAuthorites();
    	
    	ListIterator<PinAuthority> iterator = pinAuthorities.listIterator();
    	try
    	{
			while(iterator.hasNext())
			{
				PinAuthority pinAuthority = iterator.next();
		 		if(PINAuthorityName.compareToIgnoreCase(pinAuthority.getPINAuthorityName())== 0)
		 		{
		 			authorityExists = true;
		 			System.out.println("Confirm deletion of PIN Authority " + PINAuthorityName + " yes/no");
		 			delPINAuthority = stdin.readLine();
					
					if(delPINAuthority != null)
		 			{
						if(delPINAuthority.compareToIgnoreCase("yes") == 0)
						{
							authorityExists = true;
							iterator.remove();
						}
						else if(delPINAuthority.compareToIgnoreCase("no") == 0)
						{
							System.out.println(" PIN Authority " + PINAuthorityName + " not deleted");
						}
						else
						{
							System.out.println("enter yes or no");
							return false;
						}
					}
					else
					{
						System.out.println("enter yes or no");
						return false;
					}
		 		}
			}
			if (false == authorityExists)
				System.out.println("No such PINAuthority exists");
				
			return true;
    	}
    	catch(Exception e)
    	{
    		System.out.println("Could not delete PIN Authority ");
	    	return false;
    	}
	}
	/*
	 * Updates the PIN Agent Configuration File
	 * @param virtualHostName is the name of the PIN Agent
	 * whose elements are to be updated
	 */
	public boolean updatePINAgentConfigurationFile(String virtualHostName)
	{
		Document pinAuthConfDocument;
		DocumentBuilder db;
		DocumentBuilderFactory dbf;
		Element rootElement;
		Element pinAgentNameElement;
		Element pinAgentPathElement;
		Element signingCertElement;
		Element SSLCertElement;
		Element signingCertSKIElement;
		Element SSLCertSKIElement;
		Element wrappingCertElement;
		Element wrappingCertSKIElement;
		Element pinAgentErrorRedirectionElement;
		Element pinAgentWrappedSessionCookieElement;
		Element pinAgentWrappedSessionCookieNameElement;
		Element pinAgentWrappedSessionCookieDomainElement;
		Element pinRetrievalRequestCookieElement;
		Element pinRetrievalRequestCookieNameElement;
		Element pinRetrievalRequestCookieDomainElement;
		Element pinRetrievalResponseCookieElement;
		Element pinRetrievalResponseCookieNameElement;
		Element pinRetrievalResponseCookieDomainElement;
		Element pinChangeRequestCookieElement;
		Element pinChangeRequestCookieNameElement;
		Element pinChangeRequestCookieDomainElement;
		Element pinAgentReplayWindowElement;
		Element pinAgentDigestMethodAlgorithmElement;
		Element pinAgentSignatureMethodAlgorithmElement;
		Element pinAuthoritiesElement;
		Element pinAuthorityNameElement;
		Element redirectionURLElement;
		Element pinAuthorityElement;
		Element wrappingPaddingSchemeElement;
		Element sessionKeyElement;
		Element algorithmNameElement;
		Element bitLengthElement;
		Element pinAgentPortNumberElement;
		
		pinAuthConfDocument =  null;
		db = null;
		dbf = null;
		rootElement = null;
		pinAgentNameElement = null;
		pinAgentPathElement = null;
		signingCertElement = null;
		signingCertSKIElement = null;
		SSLCertElement =null;
		SSLCertSKIElement =null;
		wrappingCertElement = null;
		wrappingCertSKIElement = null;
		pinAgentErrorRedirectionElement = null;
		pinAgentWrappedSessionCookieElement = null;
		pinAgentWrappedSessionCookieNameElement = null;
		pinAgentWrappedSessionCookieDomainElement = null;
		pinRetrievalRequestCookieElement = null;
		pinRetrievalRequestCookieNameElement = null;
		pinRetrievalRequestCookieDomainElement = null;
		pinRetrievalResponseCookieElement = null;
		pinRetrievalResponseCookieNameElement = null;
		pinRetrievalResponseCookieDomainElement = null;
		pinChangeRequestCookieElement = null;
		pinChangeRequestCookieNameElement = null;
		pinChangeRequestCookieDomainElement = null;
		pinAgentReplayWindowElement = null;
		pinAgentDigestMethodAlgorithmElement = null;
		pinAgentSignatureMethodAlgorithmElement = null;
		pinAuthoritiesElement = null;
		pinAuthorityNameElement = null;
		redirectionURLElement = null;
		pinAuthorityElement = null;
		wrappingPaddingSchemeElement = null;
		sessionKeyElement = null;
		algorithmNameElement = null;
		bitLengthElement = null;
		pinAgentPortNumberElement = null;
		
		 // Instantiate document builder factory
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);

        try
        {
			 // Instantiate document builder
            db = dbf.newDocumentBuilder();
        }
        catch (Exception e)
        {
        	System.out.println("Could not Update Configuration File");
        	return false;
			
        }
        try
        {
	        // instantiate the document
	        pinAuthConfDocument = db.newDocument();
	        
	        //create the root element
	    	rootElement = pinAuthConfDocument.createElement( "PinAgent");
	    	//append the rootELement to the Document
	    	pinAuthConfDocument.appendChild(rootElement);
	    	
	    	
	    	//Create pin agent name element
	    	pinAgentNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
	    	pinAgentNameElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentName()));
	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAgentNameElement);
	    	
	       
	    	//create pinAGentSigning certificate and SKI elements
	    	signingCertElement = pinAuthConfDocument.createElement( configurationConstants.Signing_Certificate);
	    	signingCertSKIElement = pinAuthConfDocument.createElement( configurationConstants.Subject_Key_Identifier);
	    	signingCertSKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentSigningCertSKI()));
	
	    	//append SKI element to signing certificate 
	    	signingCertElement.appendChild(signingCertSKIElement);
	    	
			//append signing certificate element to rootElement
	    	rootElement.appendChild(signingCertElement);
			
	    	
	    	//create pinAGentSSL certificate and SKI elements
	    	SSLCertElement = pinAuthConfDocument.createElement( configurationConstants.SSL_Certificate);
	    	SSLCertSKIElement = pinAuthConfDocument.createElement( configurationConstants.Subject_Key_Identifier);
	    	SSLCertSKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentSSLCertSKI()));
		    	//append SKI element to SSL certificate 
	    	SSLCertElement.appendChild(SSLCertSKIElement);
	    	
			//append SSL certificate element to rootElement
	    	rootElement.appendChild(SSLCertElement);
	    	
	    	
	     	//Create pin agent Signature Method Algorithm Element
	    	pinAgentSignatureMethodAlgorithmElement = pinAuthConfDocument.createElement( configurationConstants.Signature_Method_Algorithm);
	    	pinAgentSignatureMethodAlgorithmElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentSignatureMethodAlgorithm()));
	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAgentSignatureMethodAlgorithmElement);
	    	
	    	//create pinAGentWrapping certificate and SKI elements
	    	wrappingCertElement = pinAuthConfDocument.createElement( configurationConstants.Wrapping_Certificate);
	    	wrappingCertSKIElement = pinAuthConfDocument.createElement( configurationConstants.Subject_Key_Identifier);
	    	wrappingCertSKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentWrappingCertSKI()));
	
	    	//append SKI element to signing certificate 
	    	wrappingCertElement.appendChild(wrappingCertSKIElement);
	    	
			//append signing certificate element to rootElement
	    	rootElement.appendChild(wrappingCertElement);
	    	
	    
	      	//create pinAGent ErrorRedirectionUrl
	    	pinAgentErrorRedirectionElement = pinAuthConfDocument.createElement( configurationConstants.Error_Redirection_URL);
	       	pinAgentErrorRedirectionElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentErrorRedirectionURL()));
	
	    	//append signing certificate element to rootElement
	    	rootElement.appendChild(pinAgentErrorRedirectionElement);
	    	 	
	    	
	    	//Create pin agent port element
	    	pinAgentPortNumberElement = pinAuthConfDocument.createElement( configurationConstants.Port_Number);
	    	pinAgentPortNumberElement.appendChild(pinAuthConfDocument.createTextNode(new String(String.valueOf(pinAgentConfigurationElements.getPINAgentPortNumber()))));
	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAgentPortNumberElement);

	    	//create pinAGent Wrapped Session Cookie 
	    	pinAgentWrappedSessionCookieElement = pinAuthConfDocument.createElement( configurationConstants.Wrapped_Session_Key_Cookie);
	    	pinAgentWrappedSessionCookieNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
	    	pinAgentWrappedSessionCookieNameElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Wrapped_Session_Key_Cookie_Name));
	    	pinAgentWrappedSessionCookieDomainElement = pinAuthConfDocument.createElement( configurationConstants.Domain);
	    	pinAgentWrappedSessionCookieDomainElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentWrappedSessionCookieDomain()));
	    	pinAgentPathElement = pinAuthConfDocument.createElement( configurationConstants.Path);
	    	pinAgentPathElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Path_Value));
	     
	    	//append name element to wrapped session cookie element
	    	pinAgentWrappedSessionCookieElement.appendChild(pinAgentWrappedSessionCookieNameElement);
	
	     	//append domain element to wrapped session cookie element
	    	pinAgentWrappedSessionCookieElement.appendChild(pinAgentWrappedSessionCookieDomainElement);
	    	
	    	//append path element to wrapped session cookie element
	    	pinAgentWrappedSessionCookieElement.appendChild(pinAgentPathElement);
	    	
	    	//append wrapped session cookie element to rootElement
	    	rootElement.appendChild(pinAgentWrappedSessionCookieElement);
	   	 
	    	//create Pin Retrieval Request Cookie
	    	pinRetrievalRequestCookieElement = pinAuthConfDocument.createElement( configurationConstants.Pin_Retrieval_Request_Cookie);
	    	pinRetrievalRequestCookieNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
	    	pinRetrievalRequestCookieNameElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Pin_Retrieval_Request_Cookie_Name));
	    	pinRetrievalRequestCookieDomainElement = pinAuthConfDocument.createElement( configurationConstants.Domain);
	    	pinRetrievalRequestCookieDomainElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINRetrievalRequestCookie()));
	    	pinAgentPathElement = pinAuthConfDocument.createElement( configurationConstants.Path);
	    	pinAgentPathElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Path_Value));
	     	
	    	//append name element to wrapped session cookie element
	    	pinRetrievalRequestCookieElement.appendChild(pinRetrievalRequestCookieNameElement);
	
	     	//append domain element to wrapped session cookie element
	    	pinRetrievalRequestCookieElement.appendChild(pinRetrievalRequestCookieDomainElement);
	    	
	    	//append path element to wrapped session cookie element
	    	pinRetrievalRequestCookieElement.appendChild(pinAgentPathElement);
	    	
	    	//append wrapped session cookie element to rootElement
	    	rootElement.appendChild(pinRetrievalRequestCookieElement);
	  	
	    	//create Pin Retrieval Response Cookie
	    	pinRetrievalResponseCookieElement = pinAuthConfDocument.createElement( configurationConstants.Pin_Retrieval_Response_Cookie);
	    	pinRetrievalResponseCookieNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
	    	pinRetrievalResponseCookieNameElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Pin_Retrieva_Response_Cookie_Name));
	    	pinRetrievalResponseCookieDomainElement = pinAuthConfDocument.createElement( configurationConstants.Domain);
	    	pinRetrievalResponseCookieDomainElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINRetrievalResponseCookie()));
	    	pinAgentPathElement = pinAuthConfDocument.createElement( configurationConstants.Path);
	    	pinAgentPathElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Path_Value));
	     	
			//append name element to wrapped session cookie element
	    	pinRetrievalResponseCookieElement.appendChild(pinRetrievalResponseCookieNameElement);
	
	     	//append domain element to wrapped session cookie element
	    	pinRetrievalResponseCookieElement.appendChild(pinRetrievalResponseCookieDomainElement);
	    	
	    	//append path element to wrapped session cookie element
	    	pinRetrievalResponseCookieElement.appendChild(pinAgentPathElement);
	    	    	
	    	//append wrapped session cookie element to rootElement
	    	rootElement.appendChild(pinRetrievalResponseCookieElement);

	    	//create Pin Change Request Cookie
	    	pinChangeRequestCookieElement = pinAuthConfDocument.createElement( configurationConstants.Pin_Change_Request_Cookie);
	    	pinChangeRequestCookieNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
	    	pinChangeRequestCookieNameElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Pin_Change_Request_Cookie_Name));
	    	pinChangeRequestCookieDomainElement = pinAuthConfDocument.createElement( configurationConstants.Domain);
	    	pinChangeRequestCookieDomainElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINChangeRequestCookie()));
	    	pinAgentPathElement = pinAuthConfDocument.createElement( configurationConstants.Path);
	    	pinAgentPathElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.Path_Value));
	     	
	    	//append name element to Change Request cookie element
	    	pinChangeRequestCookieElement.appendChild(pinChangeRequestCookieNameElement);
	
	     	//append domain element to Change Request cookie element
	    	pinChangeRequestCookieElement.appendChild(pinChangeRequestCookieDomainElement);
	    	
	    	//append path element to Change Request cookie element
	    	pinChangeRequestCookieElement.appendChild(pinAgentPathElement);
	    	
	    	//append Change Request cookie element to rootElement
	    	rootElement.appendChild(pinChangeRequestCookieElement);
	    	
	    	
	    	
	    	
	      	//Create pin agent replay window element
	    	pinAgentReplayWindowElement = pinAuthConfDocument.createElement( configurationConstants.Replay_Win);
	    	pinAgentReplayWindowElement.appendChild(pinAuthConfDocument.createTextNode(new String(String.valueOf(pinAgentConfigurationElements.getReplayWindow()))));
	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAgentReplayWindowElement);
	    	
	       	//Create pin agent Digest Method Algorithm Element
	    	pinAgentDigestMethodAlgorithmElement = pinAuthConfDocument.createElement( configurationConstants.Digest_Method_Algorithm);
	    	pinAgentDigestMethodAlgorithmElement.appendChild(pinAuthConfDocument.createTextNode(pinAgentConfigurationElements.getPINAgentDigestMethodAlgorithm()));
	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAgentDigestMethodAlgorithmElement);
	    
	    	//create PinAuthorities element
	    	pinAuthoritiesElement = pinAuthConfDocument.createElement("PinAuthorities");
	    	    	
	    	//append name node to the rootElement
	    	rootElement.appendChild(pinAuthoritiesElement);
	    	pinAuthorities = pinAgentConfigurationElements.getPinAuthorites();
	    	
	    	ListIterator<PinAuthority> iterator = pinAuthorities.listIterator();
			while(iterator.hasNext())
			{
				PinAuthority pinAuthority = iterator.next();
		    	//create PinAuthorities element
		    	pinAuthorityElement = pinAuthConfDocument.createElement(configurationConstants.PIN_Authority);
		    	    	
				
		    	//append name node to the rootElement
		    	pinAuthoritiesElement.appendChild(pinAuthorityElement);
		    	
		    	//create PinAuthorityName Element
		    	pinAuthorityNameElement = pinAuthConfDocument.createElement( configurationConstants.Name);
		    	pinAuthorityNameElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthority.getPINAuthorityName()));
		    	    	
		    	//append name node to the rootElement
		    	pinAuthorityElement.appendChild(pinAuthorityNameElement);
		    				
		    	//create pinAuthority Signing certificate and SKI elements
		    	signingCertElement = pinAuthConfDocument.createElement( configurationConstants.Signing_Certificate);
		    	signingCertSKIElement = pinAuthConfDocument.createElement( configurationConstants.Subject_Key_Identifier);
		    	signingCertSKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthority.getPINAuthoritySigningCertSKI()));
		
		    	//append SKI element to signing certificate 
		    	signingCertElement.appendChild(signingCertSKIElement);
		    	
				//append signing certificate element to pinAuthorityElement
		    	pinAuthorityElement.appendChild(signingCertElement);
							
		    	//create pinAuthority Wrapping certificate and SKI elements
		    	wrappingCertElement = pinAuthConfDocument.createElement( configurationConstants.Wrapping_Certificate);
		    	wrappingCertSKIElement = pinAuthConfDocument.createElement( configurationConstants.Subject_Key_Identifier);
		    	wrappingCertSKIElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthority.getPINAuthorityWrappingCertSKI()));
		
		    	//append SKI element to signing certificate 
		    	wrappingCertElement.appendChild(wrappingCertSKIElement);
		    	
				//append signing certificate element to pinAuthorityElement
		    	pinAuthorityElement.appendChild(wrappingCertElement);
		    	
							
		      	//create  ErrorRedirectionUrl
		    	redirectionURLElement = pinAuthConfDocument.createElement( configurationConstants.Redirection_URL);
		    	redirectionURLElement.appendChild(pinAuthConfDocument.createTextNode(pinAuthority.getPINAuthorityRedirectionURL()));
		
		    	//append signing certificate element to pinAuthorityElement
		    	pinAuthorityElement.appendChild(redirectionURLElement);
		    	
		    	//create Wrapping Padding Scheme Element
		    	wrappingPaddingSchemeElement = pinAuthConfDocument.createElement( configurationConstants.Wrapping_Padding_Scheme);
		    	wrappingPaddingSchemeElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.PIN_Authority_Wrapping_Padding_Scheme_Name));
		
		    	//append Wrapping Padding Scheme Element to pinAuthorityElement
		    	pinAuthorityElement.appendChild(wrappingPaddingSchemeElement);
		    	
							
		    	  	
		    	//create SessionKey Element
		    	sessionKeyElement = pinAuthConfDocument.createElement(configurationConstants.Session_Key);
		    	algorithmNameElement = pinAuthConfDocument.createElement(configurationConstants.Algorithm_Name);
		    	algorithmNameElement.appendChild(pinAuthConfDocument.createTextNode(configurationConstants.PIN_Authority_Session_Key_Algorithm_Name));
		    	
				
		    
		    	bitLengthElement = pinAuthConfDocument.createElement(configurationConstants.Bit_Length);
				bitLengthElement.appendChild(pinAuthConfDocument.createTextNode(String.valueOf(configurationConstants.PIN_Authority_Session_Key_Bit_Length)));
		    	

				sessionKeyElement.appendChild(algorithmNameElement);
				sessionKeyElement.appendChild(bitLengthElement);
				
		    	//append session Key Element to pinAuthorityElement
		    	pinAuthorityElement.appendChild(sessionKeyElement);
								
			    	 	
	    	}
        }
		catch(Exception e)
		{
			System.out.println("Could not Update Configuration File");
			
			
			return false;
		}
			
		
		
    	//write it to the xml
    
    	try
    	{
    		OutputFormat format = new OutputFormat(pinAuthConfDocument);
			format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);


			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(new File("/usr-files/agentconfiguration.xml.new")), format);
			//new FileOutputStream(new File("E:\\viewPINDev\\PinAgent\\book.xml")), format);
		
			serializer.serialize(pinAuthConfDocument);
			
			try
			{
				FileHandler.copyFile(new File(configurationConstants.USR_FILE_DIR + configurationConstants.New_Agent_Config_File),
								new File(configurationConstants.Webapp_Directory + virtualHostName+configurationConstants.Config_Directory
							+ configurationConstants.Agent_Config_File));
			
			}
			catch(IOException e)
			{
				System.out.println("Could not Update Configuration File");
			
				return false;
			}
			
			if (false == FileHandler.deleteFile(new File(configurationConstants.USR_FILE_DIR + configurationConstants.New_Agent_Config_File)))
			{
				System.out.println("Could not Update Configuration File");
				
				return false;
			}
			
			if(false == FileHandler.deleteFile(new File(configurationConstants.Webapp_Directory +virtualHostName + configurationConstants.Config_Directory
					+ configurationConstants.New_Agent_Config_File)))
			{
				System.out.println("Could not Update Configuration File");
					return false;
			}	
			
    	}
    	catch (Exception e)
    	{
    		System.out.println("Could not Update Configuration File");
			
    		return false;
    	}
    	return true;
	}
	
		
	/*
	 * Function to check whehter the
	 * @param s is a alphanumeric string or not
	 */
	
	private boolean isAlphaNumeric(String s) {
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) continue; // lowercase
		   if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
		   if ((c >= '0') && (c <= '9')) continue; // numeric
		   return false;
		 }  
		 return true;
	}
	
	/*
	 * Function to check whehter the
	 * @param s is a numeric value or not
	 */
	
	private boolean isNumeric(String s) {
			
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) {      
		   final char c = chars[x];
		     if ((c >= '0') && (c <= '9')) continue; // numeric
		     
		     
		   return false;
		 }  
		 return true;
	}
		
	/*
	 * Function to check whehter the
	 * @param s is a valid domin name or not,currently we are putting check for stoping user not to enter invalid char.
	 TBD: give some regular expression to check correct DomainName.
	 */
	private boolean isValidDomainFormat(String s) 
	{
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) 
		 {      
		   final char c = chars[x];
		   if ((c >= 'a') && (c <= 'z')) continue; 
		   if ((c >= 'A') && (c <= 'Z')) continue; 
		   if ((c >= '0') && (c <= '9')) continue; 
		   if (c == '.') continue;
		   
		   System.out.println("Please enter the valid domain name format.");
		   return false;
		 }  
		 return true;
	}
	
	/*
	 * Function to check whehter the
	 * @param s is a valid URL or not,currently we are putting check for stoping user not to enter invalid char.
	 TBD: give some regular expression to check correct URL.
	 */
	private boolean isValidHttpURL(String s) 
	{
		 String sub_string = s.substring(0,7);
		 if(sub_string.equalsIgnoreCase("HTTP://")==true)
		 {
			return isValidURL(s);
		 }
		 else
		 {
			return false;
		 }
	}
	
	private boolean isValidHttpsURL(String s)
	{
		 String sub_string = s.substring(0,8);
		 if(sub_string.equalsIgnoreCase("HTTPS://")==true)
		 {
			return isValidURL(s);
		 }
		 else
		 {	
			return false;
		 }
	}
	
	private boolean isValidURL(String s) 
	{
		 // we are expecting user has enter atleast 10 character to validate the URL format.
		 if(s.length()<10) return false;
		 final char[] chars = s.toCharArray();
		 for (int x = 0; x < chars.length; x++) 
		 {      
		   final char c = chars[x];                                                                        
		   if ((c >= 'a') && (c <= 'z')) continue; 
		   if ((c >= 'A') && (c <= 'Z')) continue; 
		   if ((c >= '0') && (c <= '9')) continue; 
		   if (c == '.' || c == '_' || c == '-' || c == '/' || c == ':' || c == '?' || c == ';') continue;
		   
		   return false;
		 }  
		 return true;
	}
}
