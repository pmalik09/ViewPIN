package com.safenetinc.viewpin.cli;

import javax.xml.xpath.XPathConstants;
import java.io.*;
import com.safenetinc.viewpin.cli.FileHandler;
import com.safenetinc.viewpin.cli.XPathReader;
import com.safenetinc.viewpin.cli.ConfigurationConstants;
import com.safenetinc.viewpin.cli.PinAuthorityConfigurationElements;

/**
* class to read the PinAuthorityConfiguration file and store the element in object
*/
public class ReadPinAuthorityConfiguration {

	static XPathReader reader = null;
	static PinAuthorityConfigurationElements pinAuthorityConfigurationElements = new PinAuthorityConfigurationElements();
	static ConfigurationConstants configurationConstants = new ConfigurationConstants();
	/**
	* Interface to check the file status,is file is approachable or not
	@return boolean true/false
	*/
	public static boolean checkFileStatus()
	{
		String config_file = configurationConstants.WebApp_Directory+configurationConstants.PinAuthority_Directory+configurationConstants.Authority_Config_File;
		boolean fileStatus = FileHandler.fileExists(configurationConstants.Authority_Config_File, new File(configurationConstants.WebApp_Directory));
       	if(false == fileStatus)
		{
			return false;
		}
		else
		{
			reader = new XPathReader(config_file);
		}
		return true;
	}
	
	public ReadPinAuthorityConfiguration() {
	}
	
	public static boolean readPinAuthorityConfigurationElements() 
	{
		String expression = null;
		if(!checkFileStatus()) {	
			return false;
		}
		
		expression = configurationConstants.PinAuthorityWrappingCertificateSKIPath;
		pinAuthorityConfigurationElements.setPinAuthorityWrappingCertificateSKIKey((String)reader.read(expression,XPathConstants.STRING));
		
		expression = configurationConstants.PinAuthoritySigningSKIPath;
		pinAuthorityConfigurationElements.setPinAuthoritySigningKeySKI((String)reader.read(expression,XPathConstants.STRING));
		
		expression = configurationConstants.MaximumReplayOpportunityWindowPath;
		pinAuthorityConfigurationElements.setMaximumReplayOpportunityWindow((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPANEncryptedPath;
		pinAuthorityConfigurationElements.setCardHolderPANEncryption((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderCVVEncryptedPath;
		pinAuthorityConfigurationElements.setCardHolderCVVEncryption((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderExpiryDateEncryptedPath;
		pinAuthorityConfigurationElements.setCardHolderExpiryDateEncryption((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPINEncryptedPath;
		pinAuthorityConfigurationElements.setCardHolderPINEncryption((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPANPath;
		pinAuthorityConfigurationElements.setCardHolderPAN((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderCVVPath;
		pinAuthorityConfigurationElements.setCardHolderCVV((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderExpiryDatePath;
		pinAuthorityConfigurationElements.setCardHolderExpiryDate((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPINPath;
		pinAuthorityConfigurationElements.setCardHolderPIN((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPANKeyIdentifierPath;
		pinAuthorityConfigurationElements.setCardHolderPANKeyIdentifier((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderCVVKeyIdentifierPath;
		pinAuthorityConfigurationElements.setCardHolderCVVKeyIdentifier((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderExpiryDateKeyIdentifierPath;
		pinAuthorityConfigurationElements.setCardHolderExpiryDateKeyIdentifier((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPINKeyIdentifierPath;
		pinAuthorityConfigurationElements.setCardHolderPINKeyIdentifier((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPANKeyTypePath;
		pinAuthorityConfigurationElements.setCardHolderPANKeyType((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderCVVKeyTypePath;
		pinAuthorityConfigurationElements.setCardHolderCVVKeyType((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderExpiryDateKeyTypePath;
		pinAuthorityConfigurationElements.setCardHolderExpiryDateKeyType((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPINKeyTypePath;
		pinAuthorityConfigurationElements.setCardHolderPINKeyType((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderPANTransformationPath;
		pinAuthorityConfigurationElements.setCardHolderPANEncryptionTransformation((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderCVVTransformationPath;
		pinAuthorityConfigurationElements.setCardHolderCVVEncryptionTransformation((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.CardHolderExpiryDateTransformationPath;
		pinAuthorityConfigurationElements.setCardHolderExpiryDateEncryptionTransformation((String)reader.read(expression,XPathConstants.STRING));
		expression = configurationConstants.CardHolderPINTransformationPath;
		pinAuthorityConfigurationElements.setCardHolderPINEncryptionTransformation((String)reader.read(expression,XPathConstants.STRING));
	
		expression = configurationConstants.MaxFailedAuthenticAttemptsPath;
		pinAuthorityConfigurationElements.setMaxFailedAttempts((String)reader.read(expression,XPathConstants.STRING));
		return true;
	}

}