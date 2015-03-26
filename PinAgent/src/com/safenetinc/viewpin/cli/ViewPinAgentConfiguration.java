
package com.safenetinc.viewpin.cli; 

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
import java.util.LinkedList;
import java.util.ListIterator;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.safenetinc.viewpin.cli.PinAgentConfigurationElements;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.safenetinc.Common;

import com.safenetinc.viewpin.cli.FileHandler;
/**
 * 
 * Class to view the PINAgent Configuration file
 * @author Pratibha Malik
 */
public class ViewPinAgentConfiguration {
	
	 
	PinAgentConfigurationElements pinAgentConfigurationElements = new PinAgentConfigurationElements();
	
	Constants configurationConstants 							= new Constants();
	
	private static final String HOSTNAME_COMMAND_LINE_ARG       = "hostname";
	
	private static final String APPLICATION_NAME                = "EditHostConfiguration";
	
	static PrintWriter output_;
	
	static BufferedReader input_;
	
	private static LinkedList<PinAuthority> pinAuthorities 		= new LinkedList<PinAuthority>();
	
	static {
	try {
		//output_ = new PrintWriter(new FileWriter("GetInfo_output.txt"), true);
		output_ = new PrintWriter(System.out, true);
		input_ = new BufferedReader(new InputStreamReader(System.in));
	} 
	catch (Throwable thr) {
		thr.printStackTrace();
		output_ = new PrintWriter(System.out, true);
		input_ = new BufferedReader(new InputStreamReader(System.in));
	}
	
	}
    
    public ViewPinAgentConfiguration()
    {
    	
    	listViewPINAgentConfiguration();
		
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
      if( false == ReadPinAgentConfiguration.checkFileStatus(cmd.getOptionValue(HOSTNAME_COMMAND_LINE_ARG)))
        {
    	  System.out.println("Either the virtual host with this name does not exist or Configuration File is missing");
    	  System.out.println("ViewPinAgentConfiguration failed");
    	  return;
        }
		new ViewPinAgentConfiguration();
		
	}
	
	public void listViewPINAgentConfiguration()
	{
		
		 	
		if(false == ReadPinAgentConfiguration.readPinAgentConfigurationElements())
		{
        	System.out.println("ViewPinAgentConfiguration failed");
        	return;
        }
		
		if(false == ReadPinAgentConfiguration.readPinAuthorityConfigurationElements())
		{
        	System.out.println("ViewPinAgentConfiguration failed");
        	return;
        }
		//print PinAgentName
		output_.println(configurationConstants.Name + ":" + 
							 pinAgentConfigurationElements.getPINAgentName());
		
		//print Pin Agent Signing Certificate SKI
		output_.println(configurationConstants.PIN_Agent_Signing_Certificate_SKI + ":" + 
				 pinAgentConfigurationElements.getPINAgentSigningCertSKI());
		
		
		//Print Pin Agent SSL Certificate SKI
		output_.println(configurationConstants.PIN_Agent_SSL_Certificate_SKI + ":" + 
				 pinAgentConfigurationElements.getPINAgentSSLCertSKI());
		
		//Print Pin Agent Wrapping Certificate SKI
		output_.println(configurationConstants.PIN_Agent_Wrapping_Certificate_SKI + ":" + 
				 pinAgentConfigurationElements.getPINAgentWrappingCertSKI());
		
		//Print Pin Agent Error Redirection URL
		output_.println(configurationConstants.PIN_Agent_Error_Redirection_URL + ":" + 
				 pinAgentConfigurationElements.getPINAgentErrorRedirectionURL());
		
		//Print Pin Agent Wrapped Session Cookie Domain 
		output_.println(configurationConstants.PIN_Agent_Wrapped_Session_Cookie_Domain + ":" + 
				 pinAgentConfigurationElements.getPINAgentWrappedSessionCookieDomain());
		
		//Print Pin Retrieval Request Cookie Domain
		output_.println(configurationConstants.PIN_Retrieval_Request_Cookie + ":" + 
				 pinAgentConfigurationElements.getPINRetrievalRequestCookie());

		//Print Pin Change  Request Cookie Domain
		output_.println(configurationConstants.PIN_Change_Request_Cookie + ":" + 
				 pinAgentConfigurationElements.getPINChangeRequestCookie());
		
		//Print Pin Retrieval Response Cookie Domain
		output_.println(configurationConstants.PIN_Retrieval_Response_Cookie + ":" + 
				 pinAgentConfigurationElements.getPINRetrievalResponseCookie());
		
		//Print Replay Window
		output_.println(configurationConstants.Replay_Window + ":" + 
				 pinAgentConfigurationElements.getReplayWindow());
		
		
		
    	pinAuthorities = pinAgentConfigurationElements.getPinAuthorites();
    	
    	ListIterator<PinAuthority> iterator = pinAuthorities.listIterator();
    	
		while(iterator.hasNext())
		{
			PinAuthority pinAuthority = iterator.next();
			
			//print Pin Authority name
			output_.println(configurationConstants.PIN_Authority_Name + ":" + 
					pinAuthority.getPINAuthorityName());
			
			//print Pin Authority Signing Certificate SKI
			output_.println(configurationConstants.PIN_Authority_Signing_Certificate_SKI + ":" + 
					pinAuthority.getPINAuthoritySigningCertSKI());
			
			//Print Pin Authority Wrapping Certificate SKI
			output_.println(configurationConstants.PIN_Authority_Wrapping_Certificate_SKI + ":" + 
					pinAuthority.getPINAuthorityWrappingCertSKI());
			
			//Print Pin Authority Error Redirection URL
			output_.println(configurationConstants.PIN_Authority_Error_Redirection_URL + ":" + 
					pinAuthority.getPINAuthorityRedirectionURL());
		
		}
	}



}
