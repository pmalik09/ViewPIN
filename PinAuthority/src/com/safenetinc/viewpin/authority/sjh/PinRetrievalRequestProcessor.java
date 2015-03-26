package com.safenetinc.viewpin.authority.sjh;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.util.Date;
import java.io.ByteArrayInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.safenetinc.luna.provider.key.LunaKey;
import com.safenetinc.viewpin.authority.ErrorCodesConstants;
import com.safenetinc.viewpin.authority.ExpiryDate;
import com.safenetinc.viewpin.authority.PinAuthorityResponse;
import com.safenetinc.viewpin.authority.PinRetrievalResponse;
import com.safenetinc.viewpin.authority.PrimaryAccountNumber;
import com.safenetinc.viewpin.authority.UrlSafeBase64;
import com.safenetinc.viewpin.authority.Utils;
import com.safenetinc.viewpin.authority.ViewPinConstants;
import com.safenetinc.viewpin.authority.XPathReader;
import com.safenetinc.viewpin.authority.exceptions.InvalidExpiryDateException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPrimaryAccountNumberException;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.CardVerificationEngine;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.CardVerificationKeyPair;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.CardVerificationValue;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.InvalidCardVerificationValueException;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.InvalidServiceCodeException;
import com.safenetinc.viewpin.authority.sjh.eft.cvv.ServiceCode;
import com.safenetinc.viewpin.authority.xml.SafeParser;
import com.safenetinc.viewpin.authority.xml.ValidationResult;
import com.safenetinc.viewpin.authority.xml.encryption.XmlEncryption;
import com.safenetinc.viewpin.authority.xml.signatures.XmlDigestMethodAlgorithms;
import com.safenetinc.viewpin.authority.xml.signatures.XmlSignature;
import com.safenetinc.viewpin.authority.xml.signatures.XmlSignatureMethodAlgorithms;

public class PinRetrievalRequestProcessor 
{
	private static Logger logger = Logger.getLogger(PinRetrievalRequestProcessor.class);
	
    private Schema preDecryptionSchema = null;
    private Schema postDecryptionSchema = null;
    private KeyStore keyStore = null;
    
	public PinRetrievalRequestProcessor() throws IOException, SAXException, KeyStoreException
	{
		super();
		
		// Initialise Apache XML security
        Init.init();
        
        initSchemas();
        
        initKeyStore();
	}
	
	public PinAuthorityResponse retrievePin(RetrievePinServiceRequest retrievePinServiceRequest)
	{
		PinAuthorityResponse pinAuthorityResponse = new PinAuthorityResponse();

		PinRetrievalRequestSession pinRetrievalRequestSession = null;
		
		try
		{
			try
	    	{
				// Process pin retrieval request
				pinRetrievalRequestSession = processPinRetrievalRequest(retrievePinServiceRequest);
	    	}
	    	catch(Exception e)
	    	{
	            getLogger().error("processing pin retrieval request " + e.getMessage());
	            e.printStackTrace();
	            pinAuthorityResponse.setIsSuccess(false);
	            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
	            
	            return pinAuthorityResponse;
	    	}
					
			boolean cardVerificationValueValid = false;
		        
			try 
			{
				// Verify card verification value
				cardVerificationValueValid = verifyCardVerificationValue(retrievePinServiceRequest, pinRetrievalRequestSession);
			}
			catch(Exception e)
			{
				getLogger().error("verifying card verification value " + e.getMessage());
				
				pinAuthorityResponse.setIsSuccess(false);
	            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
	            
	            return pinAuthorityResponse;
			}
	
			// Did we verify card verification value successfully?
			if(cardVerificationValueValid == false)
			{
				// Failed to verify card verification value
				pinAuthorityResponse.setIsSuccess(false);
	            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.CARD_HOLDER_AUTHENTICATION_FAILED);
	            
	            return pinAuthorityResponse;
			}
			
			// Have we been asked to authenticate card holders expiry date?
			if(retrievePinServiceRequest.isEnforceExpiryDateAuthentication() == true)
	    	{
				getLogger().debug("cardholder expiry date authentication requested");
				
				try 
				{
					// We have been asked to verify card holders expiry date
					boolean claimedCardHoldersExpiryDateAuthentic = verifyExpiryDate(retrievePinServiceRequest, pinRetrievalRequestSession);
					
					// Did we authenticate card holders expiry date successfully?
					if(claimedCardHoldersExpiryDateAuthentic == false)
					{
						getLogger().info("expiry date authentication failed");
						
						pinAuthorityResponse.setIsSuccess(false);
			            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.CARD_HOLDER_AUTHENTICATION_FAILED);
			            
			            return pinAuthorityResponse;
					}
					else
					{
						getLogger().debug("card holders expiry date successfully authenticated");
					}
				}
				catch(Exception e)
				{
					pinAuthorityResponse.setIsSuccess(false);
		            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
		            
		            return pinAuthorityResponse;
				}
	    	}
			
			// Have we been asked to authenticate only the card verification value?
			if(retrievePinServiceRequest.isAuthenticateCardVerificationValueOnly() == true)
			{
				// We have only been asked to authenticate the card verification value, therefore we do not return card pins
				pinAuthorityResponse.setIsSuccess(true);
				
				return pinAuthorityResponse;
			}
			
			PinRetrievalResponse pinRetrievalResponse = null;
			
	        try
	        {
	        	SubjectKeyIdentifier authoritySigningSubjectKeyIdentifier = retrieveAuthoritySigningSubjectKeyIdentifier();
	        	
	        	PinRetrievalResponseGenerator prrg = new PinRetrievalResponseGenerator(getKeyStore(), authoritySigningSubjectKeyIdentifier);
	        	
	        	String signatureMethodAlgorithm = XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA256; // TODO hard coded
	            
	            String digestMethodAlgorithm = XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA256; // TODO hard coded
	            
	            SecureRandom sr = SecureRandom.getInstance("LunaRNG", "LunaProvider"); // TODO hard coded
	        
	            // Generate pin retrieval response
	            pinRetrievalResponse = prrg.generatePinRetrievalResponse(pinRetrievalRequestSession.getPinRetrievalRequestDocument(),
	            	pinRetrievalRequestSession.getSessionKey(),	pinRetrievalRequestSession.getSessionEncrytionMethodAlgorithm(),
	            	signatureMethodAlgorithm, digestMethodAlgorithm, sr, retrievePinServiceRequest.getCardPins());
	            	           
	            // Indicate that we have generated pin retrieval response successfully
	            pinAuthorityResponse.setIsSuccess(true);
	            
	            // Pass pin retrieval response back to calling client
	            pinAuthorityResponse.setViewPinResponseDocument(pinRetrievalResponse.getEncodedCompressedPinRetrievalResponseDocument());
	        }
	        catch(Exception e) 
	        {
	        	getLogger().error("generating pin retrieval response " + e.getMessage());
	        	
	        	pinAuthorityResponse.setIsSuccess(false);
	            pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_RESPONSE_ERROR);
	            
	            return pinAuthorityResponse;
			}
		}
		finally
		{
			if(pinRetrievalRequestSession.getSessionKey() != null)
			{
				// Destroy session key
				((LunaKey)pinRetrievalRequestSession.getSessionKey()).DestroyKey();
			}
		}
		
		return pinAuthorityResponse;
	}
	
    private PinRetrievalRequestSession processPinRetrievalRequest(RetrievePinServiceRequest retrievePinServiceRequest) throws ViewPinRequestException
    {
    	Document pinRetrievalRequestDocument;
		String rootElement;
    	String sessionEncrytionMethodAlgorithm;
    	String sessionEncryptionPath;
    	SecretKey sessionKey;
    	
    	pinRetrievalRequestDocument = null;
		rootElement = "PinRetrievalRequest";
    	sessionEncrytionMethodAlgorithm = null;
    	sessionEncryptionPath = null;
    	sessionKey = null;
    	
    	try
        {
    		// Load pin retrieval request document
            pinRetrievalRequestDocument = loadViewPinRequestDocumentFromString(retrievePinServiceRequest.getEncodedCompressedPinRetrievalRequestDocument());
        }
        catch(Exception e)
        {
            getLogger().error("loading pin retrieval request document " + e.getMessage());
          
			 
            throw new ViewPinRequestException("loading pin retrieval request document");
        }
    	
    	if(getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request document prior to processing
                String serialisedPinRetrievalRequestDocument = new String(Utils.serialise(pinRetrievalRequestDocument));
                
                getLogger().debug(serialisedPinRetrievalRequestDocument);
            }
            catch(Exception e)
            {
                getLogger().error("serialising pin retrieval request document " + e.getMessage());
            }
        }
    	
    	// Validate pin retrieval request document prior to decryption
        if(validateViewPinRequestDocumentPreDecryption(pinRetrievalRequestDocument, rootElement) == false)
        {
            // Invalid pin retrieval request document prior to decryption
            throw new ViewPinRequestException("invalid pin retrieval request prior to decryption");
        }
        
		try
		{
			sessionEncryptionPath = "/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue/xenc:EncryptedData";
		    	
		    // Establish session encryption algorithm
		    sessionEncrytionMethodAlgorithm = establishSessionEncryptionMethodAlgorithm(pinRetrievalRequestDocument, sessionEncryptionPath);
		}
		catch(Exception e)
		{
		    getLogger().error("establishing session encryption algorithm " + e.getMessage());
		
		    throw new ViewPinRequestException("establishing session encryption algorithm");
		}
		
		getLogger().debug("session encryption algorithm = " + sessionEncrytionMethodAlgorithm);

        try
        {
            // Recover session key
            sessionKey = XmlEncryption.recoverSessionKey(pinRetrievalRequestDocument, sessionEncrytionMethodAlgorithm,
                getKeyStore(), rootElement);
        }
        catch(Exception e)
        {
            getLogger().error("recovering session key " + e.getMessage());

            throw new ViewPinRequestException("recovering session key");
        }

        // Did we recover session key OK?
        if(sessionKey == null)
        {
            getLogger().error("recovering session key");

            throw new ViewPinRequestException("recovering session key");
        }
        
        try
        {
	        // Process EncryptedData elements
	        processEncryptedData(pinRetrievalRequestDocument, sessionKey);
    	}
        catch(Exception e)
        {
            getLogger().error("processing encrypted data " + e.getMessage());
            
            ((LunaKey)sessionKey).DestroyKey();

            throw new ViewPinRequestException("processing encrypted data");
        }
        
        if(getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request post decryption
               String serialisedPinRetrievalRequestDocumentPostDecryption = new String(Utils.serialise(pinRetrievalRequestDocument));
               
               getLogger().debug(serialisedPinRetrievalRequestDocumentPostDecryption);
            }
            catch(Exception e)
            {
                getLogger().warn("serialising pin retrieval request document post decryption " + e.getMessage());
            }
        }
        
        // Validate pin retrieval request post decryption
        if(validateViewPinRequestDocumentPostDecryption(pinRetrievalRequestDocument) == false)
        {
            // Failed to validate pin retrieval request post decryption
            getLogger().error("invalid pin retrieval request post decryption");
            
            ((LunaKey)sessionKey).DestroyKey();

            throw new ViewPinRequestException("invalid pin retrieval request post decryption");
        }
        
        PinRetrievalRequest pinRetrievalRequest = null;
		
        try
        {
        	// Parse pin retrieval request
        	pinRetrievalRequest = parsePinRetrievalRequest(pinRetrievalRequestDocument);
        }
        catch(Exception e)
        {
        	getLogger().error("parsing pin retrieval request " + e.getMessage());
        	
            ((LunaKey)sessionKey).DestroyKey();
        	
        	throw new ViewPinRequestException("parsing pin retrieval request");
        }
		
        PinRetrievalRequestSession session = new PinRetrievalRequestSession(pinRetrievalRequestDocument,
        	pinRetrievalRequest, sessionKey, sessionEncrytionMethodAlgorithm);
           
        return session;
    }
    
    private boolean verifyExpiryDate(RetrievePinServiceRequest retrievePinServiceRequest,
        PinRetrievalRequestSession pinRetrievalRequestSession) throws InvalidExpiryDateException, ViewPinRequestException
    {
    	boolean expiryDateAuthentic = false;
    	
    	// Ensure authentic expiry date is not null
    	if(retrievePinServiceRequest.getExpiryDate() == null)
    	{
    		throw new InvalidExpiryDateException("authentic expiry date is null");
    	}
    	
    	ExpiryDate authenticExpiryDate = new ExpiryDate(retrievePinServiceRequest.getExpiryDate());
    	
    	ExpiryDate claimedCardHolderExpiryDate = pinRetrievalRequestSession.getPinRetrievalRequest().getExpiryDate();
    	
    	// Ensure expiry date has been provided by card holder
    	if(claimedCardHolderExpiryDate == null)
    	{
    		getLogger().info("expiry date authentication requested but no expiry date provided by cardholder");
    		
    		expiryDateAuthentic = false;
    		
    		return expiryDateAuthentic;
    	}
    	
    	getLogger().debug("claimed expiry date = " + claimedCardHolderExpiryDate.getFormatted());
    	getLogger().debug("authentic expiry date = " + authenticExpiryDate.getFormatted());
    	
    	// Check if expiry date submitted by the card holder matches the authentic value
    	if(claimedCardHolderExpiryDate.equals(authenticExpiryDate) == true)
    	{
    		expiryDateAuthentic = true;
    	}
    	else
    	{
    		expiryDateAuthentic = false;
    	}
    	
    	return expiryDateAuthentic;
    }
    
    private boolean verifyCardVerificationValue(RetrievePinServiceRequest retrievePinServiceRequest,
    	PinRetrievalRequestSession pinRetrievalRequestSession) throws KeyStoreException, UnrecoverableKeyException,
    	NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, NoSuchPaddingException,
    	IllegalBlockSizeException, BadPaddingException, InvalidPrimaryAccountNumberException,
    	InvalidExpiryDateException, InvalidServiceCodeException
    {
    	// Build CVK key pair
    	CardVerificationKeyPair cardVerificationKeyPair = buildCardVerificationKeyPair(retrievePinServiceRequest);
    	
    	// TODO
    	//System.out.println("CVKA KCV = " + calculateSingleDesKeyCheckValue(kp.getKeyA()));
    	//System.out.println("CVKB KCV = " + calculateSingleDesKeyCheckValue(kp.getKeyB()));
    	
    	CardVerificationEngine cve = new CardVerificationEngine("LunaProvider"); // TODO
    	
    	// Verify card verification value
    	return cve.verifyCardVerificationValue(pinRetrievalRequestSession.getPinRetrievalRequest().getCardVerificationValue(),
    		cardVerificationKeyPair, new PrimaryAccountNumber(retrievePinServiceRequest.getPrimaryAccountNumber()),
    		new ExpiryDate(retrievePinServiceRequest.getExpiryDate()), new ServiceCode(retrievePinServiceRequest.getServiceCode().trim()));
    }
    
    private CardVerificationKeyPair buildCardVerificationKeyPair(RetrievePinServiceRequest retrievePinServiceRequest) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException
    {
    	CardVerificationKeyPair kp = null;
    	
    	// Ensure CVK key pair name is not null
    	if(retrievePinServiceRequest.getCardVerificationKeyPairName() == null)
    	{
    		throw new KeyStoreException("CVK key pair name is null");
    	}
    	
    	String cardVerificationKeyAlpha = retrievePinServiceRequest.getCardVerificationKeyPairName() + "a"; 
    	String cardVerificationKeyBravo = retrievePinServiceRequest.getCardVerificationKeyPairName() + "b";
    	
    	if(getKeyStore().isKeyEntry(cardVerificationKeyAlpha) == false)
    	{
    		throw new KeyStoreException("CVK key " + cardVerificationKeyAlpha + " not found");
    	}
    	
    	if(getKeyStore().isKeyEntry(cardVerificationKeyBravo) == false)
    	{
    		throw new KeyStoreException("CVK key " + cardVerificationKeyBravo + " not found");
    	}
    	
    	SecretKey cvka = (SecretKey)getKeyStore().getKey(cardVerificationKeyAlpha, null);
    	SecretKey cvkb = (SecretKey)getKeyStore().getKey(cardVerificationKeyBravo, null);
    	
    	kp = new CardVerificationKeyPair(cvka, cvkb);
    	
    	return kp;
    }
    
    private PinRetrievalRequest parsePinRetrievalRequest(Document pinRetrievalRequestDocument) throws XPathExpressionException, XPathFactoryConfigurationException, InvalidExpiryDateException, InvalidPrimaryAccountNumberException, InvalidCardVerificationValueException
    {
    	PinRetrievalRequest pinRetrievalRequest;
    	XPath xp;
    	String cardHolderVerificationValue;
    	Element expiryDateElement;
    	String expiryDateMonth;
   	    String expiryDateYear;
   	    ExpiryDate expiryDate;
   	    Element primaryAccountNumberElement;
   		PrimaryAccountNumber primaryAccountNumber;
   	    
   	    pinRetrievalRequest = null;
    	xp = null;
    	cardHolderVerificationValue = null;
    	expiryDateElement = null;
    	expiryDateMonth = null;
   	    expiryDateYear = null;
   	    expiryDate = null;
   	    primaryAccountNumberElement = null;
   	    primaryAccountNumber = null;
   	    
        // Instantiate XPath object
   	    xp = Utils.createXPath();
   	    	
        // Get verification value from pin retrieval request
   	    cardHolderVerificationValue = (String)xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue", pinRetrievalRequestDocument, XPathConstants.STRING);
   	    
   	    // Get ExpiryDate element
   	    expiryDateElement = (Element)xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:ExpiryDate", pinRetrievalRequestDocument, XPathConstants.NODE);
   	    
   	    if(expiryDateElement != null)
   	    {
	   	    // Get expiry date month
	   	    expiryDateMonth = (String)xp.evaluate("vp:Month", expiryDateElement, XPathConstants.STRING);
	   	    
	   	    // Get expiry date year
	   	    expiryDateYear = (String) xp.evaluate("vp:Year", expiryDateElement, XPathConstants.STRING);
   	
	   	    // Instantiate ExpiryDate
	   	    expiryDate = new ExpiryDate(expiryDateMonth, expiryDateYear);
   	    }
   	    
        // Get primary account number element
   		primaryAccountNumberElement = (Element)xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:PrimaryAccountNumber", pinRetrievalRequestDocument, XPathConstants.NODE);
   		   		
   		if(primaryAccountNumberElement != null)
   		{
   			primaryAccountNumber = new PrimaryAccountNumber(primaryAccountNumberElement.getTextContent());
   		}
   		
   		pinRetrievalRequest = new PinRetrievalRequest(new CardVerificationValue(cardHolderVerificationValue), primaryAccountNumber, expiryDate);
   		
   		return pinRetrievalRequest;
    }
    
    private void processEncryptedData (Document pinRetrievalRequestDocument, SecretKey sessionKey) throws XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
		NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, XPathFactoryConfigurationException
	{
		XPath xp;
		Element verificationValueElement;
		Element verificationValueEncryptedDataElement;
		String verificationValue;
		Element expiryDateElement;
		Element expiryDateMonthElement;
		Element expiryDateYearElement;
		Element expiryDateMonthEncryptedDataElement;
		Element expiryDateYearEncryptedDataElement;
		String decryptedExpiryDateMonth;
		String decryptedExpiryDateYear;
		Element primaryAccountNumberElement;
		Element primaryAccountNumberEncryptedDataElement;
		String primaryAccountNumber;
		Element pinRetrievalRequestRootElement;
		Element encryptedKeyElement;
		
		xp = null;
		verificationValueElement = null;
		verificationValueEncryptedDataElement = null;
		verificationValue = null;
		expiryDateElement = null;
		expiryDateMonthElement = null;
		expiryDateYearElement = null;
		expiryDateMonthEncryptedDataElement = null;
		expiryDateYearEncryptedDataElement = null;
		decryptedExpiryDateMonth = null;
		decryptedExpiryDateYear = null;
		primaryAccountNumberElement = null;
		primaryAccountNumberEncryptedDataElement = null;
		primaryAccountNumber = null;
		pinRetrievalRequestRootElement = null;
		encryptedKeyElement = null;
		
		// Initialise XPath
		xp = Utils.createXPath();
		
		// Get verification value element
		verificationValueElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue", pinRetrievalRequestDocument, XPathConstants.NODE);
		
		// Get verification value EncryptedDataElement
		verificationValueEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", verificationValueElement, XPathConstants.NODE);
		
		// Decrypted verification value
		verificationValue = XmlEncryption.decryptEncryptedData(sessionKey, verificationValueEncryptedDataElement);
		
		// Replace encrypted verification value with decrypted verification value
		verificationValueElement.replaceChild(pinRetrievalRequestDocument.createTextNode(verificationValue), verificationValueEncryptedDataElement);
		
		// Get expiry date element
		expiryDateElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:ExpiryDate", pinRetrievalRequestDocument, XPathConstants.NODE);
		
		// Is expiry date element present?
		if(expiryDateElement != null)
		{
		    // Expiry date element is present
		    expiryDateMonthElement = (Element) xp.evaluate("vp:Month", expiryDateElement, XPathConstants.NODE);
		    expiryDateYearElement = (Element) xp.evaluate("vp:Year", expiryDateElement, XPathConstants.NODE);
		
		    // Get expiry date month and year encrypted data elements
		    expiryDateMonthEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", expiryDateMonthElement, XPathConstants.NODE);
		    expiryDateYearEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", expiryDateYearElement, XPathConstants.NODE);
		
		    // Decrypt expiry date month and year encrypted data elements
		    decryptedExpiryDateMonth = XmlEncryption.decryptEncryptedData(sessionKey, expiryDateMonthEncryptedDataElement);
		    decryptedExpiryDateYear = XmlEncryption.decryptEncryptedData(sessionKey, expiryDateYearEncryptedDataElement);
	
		    // Replace encrypted month and year with decrypted month and year
		    expiryDateMonthElement.replaceChild(pinRetrievalRequestDocument.createTextNode(decryptedExpiryDateMonth), expiryDateMonthEncryptedDataElement);
		    expiryDateYearElement.replaceChild(pinRetrievalRequestDocument.createTextNode(decryptedExpiryDateYear), expiryDateYearEncryptedDataElement);
		}
		
		// Get primary account number element
		primaryAccountNumberElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:PrimaryAccountNumber", pinRetrievalRequestDocument, XPathConstants.NODE);
		
		// Is primary account number element present?
		if(primaryAccountNumberElement != null)
		{
		    // Primary account number element is present, get primary account number encrypted data element
		    primaryAccountNumberEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", primaryAccountNumberElement, XPathConstants.NODE);
		
		    // Decrypt primary account number encrypted data element
		    primaryAccountNumber = XmlEncryption.decryptEncryptedData(sessionKey, primaryAccountNumberEncryptedDataElement);
		
		    // Replace encrypted primary account number with decrypted primary account number
		    primaryAccountNumberElement.replaceChild(pinRetrievalRequestDocument.createTextNode(primaryAccountNumber), primaryAccountNumberEncryptedDataElement);
		}
		
		// Get pin retrieval request root element
		pinRetrievalRequestRootElement = pinRetrievalRequestDocument.getDocumentElement();
		
		// Get EncryptedKey element
		encryptedKeyElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/xenc:EncryptedKey[@Id = 'EK']", pinRetrievalRequestDocument, XPathConstants.NODE);
		
		// Remove EncryptedKey element from pin retrieval request
		pinRetrievalRequestRootElement.removeChild(encryptedKeyElement);
		
		// Remove XML digital signature and XML encryption namespace declarations from pin retrieval request root element
		pinRetrievalRequestRootElement.removeAttribute("xmlns:xenc");
		pinRetrievalRequestRootElement.removeAttribute("xmlns:ds");
	}
    
    private boolean validateViewPinRequestDocumentPreDecryption(Document viewPinRequestDocument, String rootElement) throws ViewPinRequestException
    {
        boolean valid;
        ValidationResult validationResult;

        valid = false;
        validationResult = null;

        try
        {
            // Validate ViewPIN request document against pre-decryption schema
            validationResult = Utils.validateDocument(viewPinRequestDocument, getPreDecryptionSchema());
     
            if(getLogger().isDebugEnabled() == true)
            {
            	// Dump serialised ViewPIN request document
            	String serialisedViewPinRequestDocument = new String(Utils.serialise(viewPinRequestDocument));
            	
            	getLogger().debug(serialisedViewPinRequestDocument);
            }
        }
        catch(Exception e)
        {
            getLogger().error("pre-decryption schema validation processing " + e.getMessage());

            throw new ViewPinRequestException("pre-decryption schema validation processing");
        }

        // Did we validate ViewPIN request document OK?
        if(validationResult.isValid() == false)
        {
            // Failed to validate ViewPIN request document against pre-decryption schema
        	if(validationResult.getException() != null)
        	{
        		getLogger().error("validating ViewPIN request document against pre-decryption schema", validationResult.getException());
        	}
        	
            valid = false;

            return valid;
        }

        getLogger().debug("validated ViewPIN request document against pre-decryption schema OK");

        try
        {
            // Verify ViewPIN request document signature
            if(XmlSignature.verifySignature(viewPinRequestDocument, getKeyStore(), rootElement) == false)
            {
                getLogger().error("ViewPIN request document signature verification failed");

                valid = false;

                return valid;
            }
        }
        catch(Exception e)
        {
            getLogger().error("signature verification processing " + e.getMessage());

            throw new ViewPinRequestException("signature verification processing");
        }

        getLogger().debug("ViewPIN request document signature verified OK");

        // TODO
        try
        {
            // Validate time stamp
            if(validateTimestamp(viewPinRequestDocument,rootElement) == false)
            {
                getLogger().error("ViewPIN request outside replay opportunity window");

                valid = false;

                return valid;
            }
        }
        catch(Exception e)
        {
            getLogger().error("timestamp validation processing " + e.getMessage());

            throw new ViewPinRequestException("timestamp validation processing");
        }
        
        valid = true;

        return valid;
    }
    
    private boolean validateTimestamp(Document viewPinRequestDocument, String rootElement) throws XPathExpressionException, ParseException, XPathFactoryConfigurationException
    {
        boolean valid;
        Date timestamp;
        Date currentTime;
        long elapsedTime;

        valid = false;
        timestamp = null;
        currentTime = null;
        elapsedTime = 0L;

        // Get time stamp
        timestamp = getTimestamp(viewPinRequestDocument, rootElement);

        // Get current time
        currentTime = new Date();

        // Calculate elapsed time
        elapsedTime = currentTime.getTime() - timestamp.getTime();
        
        long maximumReplayOpportunityWindow = getMaximumReplayOpportunityWindow();

        if (getLogger().isDebugEnabled() == true)
        {
            getLogger().debug("timestamp = " + timestamp.getTime());
            getLogger().debug("current time = " + currentTime.getTime());
            getLogger().debug("elapsed time = " + elapsedTime);
            getLogger().debug("positive maximum replay opportunity window = " + +maximumReplayOpportunityWindow);
            getLogger().debug("negative maximum replay opportunity window = " + -maximumReplayOpportunityWindow);
        }
     
        // Is elapsed time outside maximum replay opportunity window?
        if (elapsedTime < -maximumReplayOpportunityWindow || elapsedTime > +maximumReplayOpportunityWindow)
        {
            // Elapsed time is outside maximum replay opportunity window
            valid = false;
        }
        else
        {
            // Elapsed time is inside maximum replay opportunity window
            valid = true;
        }

        return valid;
    }
    
    private long getMaximumReplayOpportunityWindow()
    {
    	long maximumReplayOpportunityWindow = 0L;
    	
        URL authorityConfigurationFile = Thread.currentThread().getContextClassLoader().getResource(ViewPinConstants.Authority_Configuration_File);
	    
    	XPathReader reader = new XPathReader(authorityConfigurationFile.toString());
    	
   	    String configuredMaximumReplayOpportunityWindow = (String)reader.read("/PinAuthorityConfiguration/MaximumReplayOpportunityWindow" , XPathConstants.STRING);
   	    configuredMaximumReplayOpportunityWindow = configuredMaximumReplayOpportunityWindow.trim();
   	    		
   	    if(configuredMaximumReplayOpportunityWindow != null)
	    {
   	    	getLogger().debug("configured maximum replay opportunity window = " + configuredMaximumReplayOpportunityWindow);
   	    	
		    maximumReplayOpportunityWindow = Long.valueOf(configuredMaximumReplayOpportunityWindow);
	    }
   	    
   	    getLogger().debug("maximum replay opportunity window = " + maximumReplayOpportunityWindow);

		return maximumReplayOpportunityWindow;
    }

    private Date getTimestamp (Document pinRetrievalRequestDocument, String rootElement) throws XPathFactoryConfigurationException, XPathExpressionException, ParseException
    {
        Date parsedTimestamp;
        XPath xp;
        String unparsedTimestamp;

        parsedTimestamp = null;
        xp = null;
        unparsedTimestamp = null;

        // Initialise XPath object
        xp = Utils.createXPath();

        // Get time stamp from pin retrieval request document
        unparsedTimestamp = (String) xp.evaluate("/vp:"+rootElement+"/vp:timestamp/text()", pinRetrievalRequestDocument, XPathConstants.STRING);

        // Parse time stamp
        parsedTimestamp = Utils.parseDateTime(unparsedTimestamp);

        return parsedTimestamp;
    }
    
    private boolean validateViewPinRequestDocumentPostDecryption (Document viewPinRequestDocument) throws ViewPinRequestException
    {
        boolean valid;
        ValidationResult validationResult;

        valid = false;
        validationResult = null;

        try
        {
            // Validate pin retrieval request against post decryption schema
            validationResult = Utils.validateDocument(viewPinRequestDocument, getPostDecryptionSchema());
        }
        catch(Exception e)
        {
            getLogger().error("validating pin retrieval request against post decryption schema");

            throw new ViewPinRequestException("validating pin retrieval request against post decryption schema");
        }

        // Did we validate pin retrieval request against post decryption schema OK?
        if(validationResult.isValid() == false)
        {
            // Failed to validate pin retrieval request against post decryption schema
        	if(validationResult.getException() != null)
            {
        	    getLogger().error("validating pin retrieval request against post decryption schema", validationResult.getException());
            }
        	else
        	{
        		getLogger().error("validating pin retrieval request against post decryption schema");
        	}

            valid = false;

            return valid;
        }

        valid = true;

        return valid;
    }

    private String establishSessionEncryptionMethodAlgorithm(Document viewPinRequestDocument, String sessionEncryptionPath) throws XPathFactoryConfigurationException, XPathExpressionException
	{
		XPath xp;
		Element verificationValueEncryptedDataElement;
		String encrytionMethodAlgorithm;
		
		xp = null;
		verificationValueEncryptedDataElement = null;
		encrytionMethodAlgorithm = null;
		
		// Instantiate XPath object
		xp = Utils.createXPath();
		
		// Get verification value encrypted data element
		verificationValueEncryptedDataElement = (Element)xp.evaluate(sessionEncryptionPath, viewPinRequestDocument,
		    XPathConstants.NODE);
		
		// Get encryption method algorithm used to encrypt verification value encrypted data element
		encrytionMethodAlgorithm = (String) xp.evaluate("xenc:EncryptionMethod/@Algorithm", verificationValueEncryptedDataElement, XPathConstants.STRING);
	
		return encrytionMethodAlgorithm;
	}
	
	private Document loadViewPinRequestDocumentFromString (String viewPinRequestString) throws ParserConfigurationException, IOException, SAXException
    {
        Document viewPinRequestDocument;
        byte[] decodedCompressedViewPinRequest;
        byte[] decompressedViewPinRequest;
        DocumentBuilder documentBuilder;
        ValidationResult parsingValidationResult;

        viewPinRequestDocument = null;
        decodedCompressedViewPinRequest = null;
        decompressedViewPinRequest = null;
        documentBuilder = null;
        parsingValidationResult = null;
        
        // Ensure ViewPIN request is not null
        if(viewPinRequestString == null)
        {
        	getLogger().error("ViewPIN request is null");
        	
        	throw new IOException("ViewPIN request is null");
        }

        // Decode compressed pin retrieval request
        decodedCompressedViewPinRequest = UrlSafeBase64.decode(viewPinRequestString);

        // Decompress pin retrieval request
        decompressedViewPinRequest = Utils.decompress(decodedCompressedViewPinRequest, ViewPinConstants.DECOMPRESSION_READ_BUFFER_LENGTH);
     
        // Instantiate safe parser
        documentBuilder = SafeParser.getInstance(true, false);

        // Register for any problems that may occur during the parsing process
        parsingValidationResult = new ValidationResult();
        documentBuilder.setErrorHandler(parsingValidationResult);

        // Load pin retrieval request document
        viewPinRequestDocument = Utils.parseDocument(decompressedViewPinRequest, documentBuilder);

        // Did we parse pin retrieval request document OK?
        if(parsingValidationResult.isValid() == false)
        {
            // Failed to parse pin retrieval response document OK
			if(parsingValidationResult.getException() != null)
			{
	            getLogger().error("parsing pin retrieval request document" + parsingValidationResult.getException().getMessage());
			}
			
            throw new IOException("parsing pin retrieval request document");
        }

        return viewPinRequestDocument;
    }
	
	private void initSchemas() throws IOException, SAXException
    {
        URL preDecryptionSchemaUrl;
        URL postDecryptionSchemaUrl;

        preDecryptionSchemaUrl = null;
        postDecryptionSchemaUrl = null;

        preDecryptionSchemaUrl = Thread.currentThread().getContextClassLoader().getResource("schemas/ViewPinPreDecryption.xsd");

        if (preDecryptionSchemaUrl == null)
        {
            getLogger().fatal("pre-decryption schema not found");

            throw new IOException("pre-decryption schema not found");
        }

        postDecryptionSchemaUrl = Thread.currentThread().getContextClassLoader().getResource("schemas/ViewPinPostDecryption.xsd");

        if (postDecryptionSchemaUrl == null)
        {
            getLogger().fatal("post-decryption schema not found");

            throw new IOException("post-decryption schema not found");
        }

        // Load pre-decryption XML schema
        setPreDecryptionSchema(Utils.loadSchema(preDecryptionSchemaUrl));

        // Load post-decryption XML schema
        setPostDecryptionSchema(Utils.loadSchema(postDecryptionSchemaUrl));
    }
	
	private void setPreDecryptionSchema (Schema preDecryptionSchema)
    {
        this.preDecryptionSchema = preDecryptionSchema;
    }

    private Schema getPreDecryptionSchema ()
    {
        return this.preDecryptionSchema;
    }

    private void setPostDecryptionSchema (Schema postDecryptionSchema)
    {
        this.postDecryptionSchema = postDecryptionSchema;
    }

    private Schema getPostDecryptionSchema ()
    {
        return this.postDecryptionSchema;
    }
    
    private void initKeyStore() throws KeyStoreException
	{
	    KeyStore ks;
	    ByteArrayInputStream  is1; 
		
        ks = null;
        is1 = new ByteArrayInputStream(("slot:1").getBytes()); 
		
        try 
        {
            ks = KeyStore.getInstance("Luna", "LunaProvider");
            ks.load(is1, null);
            
            setKeyStore(ks);
        }
        catch(Exception e)
        {    
            getLogger().fatal("initialising key store " + e.getMessage());       
            
            throw new KeyStoreException();
        }
    }
    
    private KeyStore getKeyStore() 
	{
		return this.keyStore;
	}

	private void setKeyStore(KeyStore keyStore) 
	{
		this.keyStore = keyStore;
	}
	
	private static Logger getLogger()
	{
	    return logger;
	}
	
	private SubjectKeyIdentifier retrieveAuthoritySigningSubjectKeyIdentifier() throws InvalidSubjectKeyIdentifierException
	{
		URL authorityConfigurationFile = Thread.currentThread().getContextClassLoader().getResource(ViewPinConstants.Authority_Configuration_File);
	    
    	XPathReader reader = new XPathReader(authorityConfigurationFile.toString());
		
		String authoritySigningSubjectKeyIdentifier = (String)reader.read("/PinAuthorityConfiguration/PinAuthoritySigningKeySKI" , XPathConstants.STRING);
	
		authoritySigningSubjectKeyIdentifier = authoritySigningSubjectKeyIdentifier.trim();
    
		return new SubjectKeyIdentifier(authoritySigningSubjectKeyIdentifier);
	}
}
