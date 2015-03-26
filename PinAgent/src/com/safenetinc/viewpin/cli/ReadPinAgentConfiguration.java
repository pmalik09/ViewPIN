package com.safenetinc.viewpin.cli; 

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.safenetinc.viewpin.cli.FileHandler;


/**
 * Class to read the PINAgent Configuration file
 * @author Pratibha Malik
 */
public class ReadPinAgentConfiguration {
	
	static PinAgentConfigurationElements pinAgentConfigurationElements = new PinAgentConfigurationElements();
	
	static Constants configurationConstants = new Constants();

	private static LinkedList<PinAuthority> pinAuthorities = new LinkedList<PinAuthority>();
	
	static XPathReader reader = null;
	
	public ReadPinAgentConfiguration() 
    {
		    	
    }
	public static boolean checkFileStatus(String virtualHostName)
	{
		
		//reader = new XPathReader("E:\\viewPINDev\\PinAgent\\agentconfiguration.xml");
		
    	String configFile = configurationConstants.Webapp_Directory +virtualHostName+configurationConstants.Config_Directory
		+ configurationConstants.Agent_Config_File;
    
    	boolean fileStatus = FileHandler.fileExists(configurationConstants.Agent_Config_File, new File(configurationConstants.Webapp_Directory +virtualHostName+configurationConstants.Config_Directory));
        	
		if(false == fileStatus)
		{
			return false;
		}
		else
		{
			reader = new XPathReader(configFile);
		}
		
		return true;
		
	}
	public static boolean readPinAgentConfigurationElements()
    {
    	String expression = null;
    	
    	//read name
		expression = configurationConstants.PINAgentNamePath;
		pinAgentConfigurationElements.setPINAgentName((String)reader.read(expression,XPathConstants.STRING));

		//read Agent Signing Certificate SKI
		expression = configurationConstants.PIN_Agent_Signing_Certificate_SKI_Path;
		pinAgentConfigurationElements.setPINAgentSigningCertSKI((String)reader.read(expression,XPathConstants.STRING));
		
		
		//read Agent SSL Certificate SKI
		expression = configurationConstants.PIN_Agent_SSL_Certificate_SKI_Path;
		pinAgentConfigurationElements.setPINAgentSSLCertSKI((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Wrapping Certificate SKI
		expression = configurationConstants.PIN_Agent_Wrapping_Certificate_SKI_Path;
		pinAgentConfigurationElements.setPINAgentWrappingCertSKI((String)reader.read(expression,XPathConstants.STRING));

		//read Agent Error Redirection URL
		expression = configurationConstants.PIN_Agent_Error_Redirection_URL_Path;
		pinAgentConfigurationElements.setPINAgentErrorRedirectionURL((String)reader.read(expression,XPathConstants.STRING));

		//read Signature Method Algorithm
		expression = configurationConstants.PIN_Agent_Signature_Method_Algorithm_Path;
		pinAgentConfigurationElements.setPINAgentSignatureMethodAlgorithm((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Wrapped Session Key Cookie Domain
		expression = configurationConstants.PIN_Agent_Wrapped_Session_Key_Cookie_Path;
		pinAgentConfigurationElements.setPINAgentWrappedSessionCookieDomain((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Wrapped Session Key Cookie Domain
		expression = configurationConstants.PIN_Agent_Wrapped_Session_Key_Cookie_Path;
		pinAgentConfigurationElements.setPINAgentWrappedSessionCookieDomain((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Wrapped Session Key Cookie Domain
		expression = configurationConstants.PIN_Agent_Retrieval_Request_Cookie_Path;
		pinAgentConfigurationElements.setPINRetrievalRequestCookie((String)reader.read(expression,XPathConstants.STRING));

		
		//read Agent Retrieval Request Cookie Domain
		expression = configurationConstants.PIN_Agent_Retrieval_Request_Cookie_Path;
		pinAgentConfigurationElements.setPINRetrievalRequestCookie((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Retrieval Request Cookie Domain
		expression = configurationConstants.PIN_Agent_Change_Request_Cookie_Path;
		pinAgentConfigurationElements.setPINChangeRequestCookie((String)reader.read(expression,XPathConstants.STRING));
		
		//read Agent Retrieval Response Cookie Domain
		expression = configurationConstants.PIN_Agent_Retrieval_Response_Cookie_Path;
		pinAgentConfigurationElements.setPINRetrievalResponseCookie((String)reader.read(expression,XPathConstants.STRING));

		//read Agent Replay Window
		expression = configurationConstants.PIN_Agent_Replay_Window_Path;
		String replayWindowPath = (String)reader.read(expression , XPathConstants.STRING);
		if(replayWindowPath != null)
		{
			Double replayWindowValue = Double.valueOf(replayWindowPath);
			int replayWindow = replayWindowValue.intValue();
			pinAgentConfigurationElements.setReplayWindow(replayWindow);
		}
		
		//read Digest Method Algorithm
		expression = configurationConstants.PIN_Agent_Digest_Method_Algorithm_Path;
		pinAgentConfigurationElements.setPINAgentDigestMethodAlgorithm((String)reader.read(expression,XPathConstants.STRING));
			
		//read Port Number
		expression = configurationConstants.PIN_Agent_Port_Number_Path;
		String portPath = (String)reader.read(expression , XPathConstants.STRING);
		if(portPath != null)
		{
			int port = Double.valueOf(portPath).intValue();
			pinAgentConfigurationElements.setPINAgentPortNumber(port);
		}
		
		return true;
    }
	public static boolean readPinAuthorityConfigurationElements()
    {
    	String expression = null;
    	
    	expression = configurationConstants.PIN_Authority_Count;
		String PinAuthPath = (String)reader.read(expression , XPathConstants.STRING);
		int pinAuthorityCount = 0;
		if(PinAuthPath != null)
		{
			Double PinAuthValue = Double.valueOf(PinAuthPath);
			pinAuthorityCount = PinAuthValue.intValue();
		}
		
		pinAgentConfigurationElements.setPINAuthorityCount(pinAuthorityCount);
		
		for(int i =0; i<pinAuthorityCount; i++)
		{
			String PinAuthorityIndex = String.valueOf(i + 1);  
			PinAuthority pinAuthority = new PinAuthority();	
			
			//read Authority Name
			expression = configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" +  configurationConstants.PIN_Authority_Name_Path;
			pinAuthority.setPINAuthorityName((String)reader.read(expression,XPathConstants.STRING));
				
			//read Authority Signing Certificate SKI
			expression = configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" +configurationConstants.PIN_Authority_Signing_Certificate_SKI_Path;
			pinAuthority.setPINAuthoritySigningCertSKI((String)reader.read(expression,XPathConstants.STRING));
				
			//read Authority Wrapping Certificate SKI
			expression = configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" + configurationConstants.PIN_Authority_Wrapping_Certificate_SKI_Path;
			pinAuthority.setPINAuthorityWrappingCertSKI((String)reader.read(expression,XPathConstants.STRING));
			
			//read Wrapping Padding Scheme
			expression = configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" + configurationConstants.PIN_Authority_Wrapping_Padding_Scheme_Path;
			pinAuthority.setPINAuthorityWrappingPaddingScheme((String)reader.read(expression,XPathConstants.STRING));
			
			//read Algorithm Name 
			expression =configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" +  configurationConstants.PIN_Authority_Session_Key_Algorithm_Name_Path;
			pinAuthority.setPINAuthorityAlgorithmName((String)reader.read(expression,XPathConstants.STRING));
			
			//read Bit Length
			expression = configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" + configurationConstants.PIN_Authority_Session_Key_Bit_Length_Path;
			pinAuthority.setPINAuthorityBitLength((String)reader.read(expression,XPathConstants.STRING));
			
			//read Authority Error Redirection URL
			expression =configurationConstants.PIN_Authority_Path + "[" + PinAuthorityIndex + "]" + configurationConstants.PIN_Authority_Error_Redirection_URL_Path;
			pinAuthority.setPINAuthorityRedirectionURL((String)reader.read(expression,XPathConstants.STRING));
			pinAuthorities.add(pinAuthority);
					
		}	
		
		pinAgentConfigurationElements.setPinAuthorities(pinAuthorities);
		
		return true;
    }

	

}
