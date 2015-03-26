package com.safenetinc.viewpin.authority;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;


//import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Hex;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;


import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.io.StringWriter;


import com.safenetinc.viewpin.authority.CVV;
import com.safenetinc.viewpin.authority.exceptions.CardAccountLockedException;
import com.safenetinc.viewpin.authority.exceptions.CardHolderAuthenticationException;
import com.safenetinc.viewpin.authority.exceptions.DuplicateCardAccountException;
import com.safenetinc.viewpin.authority.exceptions.InvalidExpiryDateException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPinException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPrimaryAccountNumberException;
import com.safenetinc.viewpin.authority.exceptions.UnknownCardAccountException;
import com.safenetinc.viewpin.authority.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.authority.exceptions.ViewPinResponseException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPINBlockFormatException;
import com.safenetinc.viewpin.authority.xml.SafeParser;
import com.safenetinc.viewpin.authority.xml.ValidationResult;
import com.safenetinc.viewpin.authority.xml.encryption.XmlEncryption;
import com.safenetinc.viewpin.authority.xml.signatures.XmlDigestMethodAlgorithms;
import com.safenetinc.viewpin.authority.xml.signatures.XmlSignature;
import com.safenetinc.viewpin.authority.xml.signatures.XmlSignatureMethodAlgorithms;
import com.sun.org.apache.xerces.internal.util.URI;
import org.bouncycastle.util.encoders.Hex;

public class ViewPinAuthorityImplementation {
	
	private static Logger                 logger                              = Logger.getLogger(ViewPinAuthority.class);	
	
	private SecureRandom				   randomNumberGenerator              = null;
	
    private static final String		       VALID_PIN_REGEX					  = "^[0-9]{4,12}$";
	//private PinAuthority pinAuthoritySimulator								 = null;
	
	private KeyStore                      agentKeyStore                       = null;

    private KeyStore                      authorityKeyStore                   = null;

    private PinRetrievalResponseGenerator pinRetrievalResponseGenerator       = null;

    private Schema                        preDecryptionSchema                 = null;

    private Schema                        postDecryptionSchema                = null;

    private static long                   maximumReplayOpportunityWindow      = 0L;

    private static XMLConfiguration		  xmlConfig							  = null;
    
	public ViewPinAuthorityImplementation()
	{
		super();
	}
	
	
	public PinAuthorityResponse processViewPinRequest(String viewPinRequest, int requestType, CardHolderDetails cardHolderDetails)
	{
				
		// declaration
		String pan;
		PrimaryAccountNumber primaryAccountNumber;
		PinAuthorityResponse pinAuthorityResponse ;
		CardHolderDetails processedCardHolderDetails;
		
		//initializing  the variables
		pan = null;
		pinAuthorityResponse=null;
	    primaryAccountNumber = null;
	    processedCardHolderDetails=null;
	
		pinAuthorityResponse = new PinAuthorityResponse();
	
		 try
	    {
			
	    	if( false == checkPINBlockFormat(cardHolderDetails.getInputPINBlockFormat()))
	    	{
		    	getLogger().fatal("Invalid PINBlock Format");
		    	pinAuthorityResponse.setViewPinResponseDocument(null);
				pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINBLOCK_VERIFICATION_FAILED);
				pinAuthorityResponse.setIsSuccess(false);
	    		return pinAuthorityResponse;
	    	}	
	    	if( false == checkPINBlockFormat(cardHolderDetails.getOutputPINBlockFormat()))
	    	{
	    		getLogger().fatal("Invalid PINBlock Format");
	    		pinAuthorityResponse.setViewPinResponseDocument(null);
				pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINBLOCK_VERIFICATION_FAILED);
				pinAuthorityResponse.setIsSuccess(false);
	    		return pinAuthorityResponse;
	    	}
			if( false == (checkPANRequired(cardHolderDetails,cardHolderDetails.getOutputPINBlockFormat())))
			{
	    		getLogger().fatal("PAN expected for PINBlockFormat");
	    		pinAuthorityResponse.setViewPinResponseDocument(null);
				pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINBLOCK_VERIFICATION_FAILED);
				pinAuthorityResponse.setIsSuccess(false);
	    		return pinAuthorityResponse;
			}
			if( false == (checkPANRequired(cardHolderDetails,cardHolderDetails.getInputPINBlockFormat())))
			{
	    		getLogger().fatal("PAN expected for PINBlockFormat");
	    		pinAuthorityResponse.setViewPinResponseDocument(null);
				pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINBLOCK_VERIFICATION_FAILED);
				pinAuthorityResponse.setIsSuccess(false);
	    		return pinAuthorityResponse;
			}
			
		

		
			
		
	    }
	    catch(Exception e)
	    {
	    	getLogger().fatal("Could not verify PIN BlockFormat or PAN expected");
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINBLOCK_VERIFICATION_FAILED);
			pinAuthorityResponse.setIsSuccess(false);
    		return pinAuthorityResponse;

	    }
	 
		try
		{
			//Procees Cardholder Elements as described in PinAuthorityConfiguration.XML
			getLogger().debug("Processing CardHolder Details");
			
			processedCardHolderDetails = processCardHolderDetails(cardHolderDetails);
			//processedCardHolderDetails = cardHolderDetails;
		    		
				    	
	    }
	    catch (Exception e)
	    {
	    	getLogger().fatal("Could not get card holder details");
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PROCESSING_CARD_HOLDER_DETAILS_FAILED);
			pinAuthorityResponse.setIsSuccess(false);
			return pinAuthorityResponse;
	     }
	    try
	    {
	    	initViewPinAuthorityImplementation();
	    }
	    catch(Exception e)
	    {
	    	getLogger().fatal("Could not initialize ViewPINAuthority");
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PINAUTHORITY_INITIALIZATION_FAILED);
			pinAuthorityResponse.setIsSuccess(false);
    		return pinAuthorityResponse;
	    }
	   
		//call process pin retrieval or change based on request type
		try
		{
			if(ViewPinConstants.PIN_VIEW_REQUEST == requestType)
			{
				if((processedCardHolderDetails != null)&&
					(processedCardHolderDetails.getCardHolderVerificationValue() != null))
				{	
					String response = processPinRetrieval(viewPinRequest, processedCardHolderDetails).getEncodedCompressedPinRetrievalResponseDocument();
					if(null != response)
					{
						pinAuthorityResponse.setViewPinResponseDocument(response);
						pinAuthorityResponse.setErrorCode(0);
						pinAuthorityResponse.setIsSuccess(true);
					}
					else
					 {
						getLogger().fatal("Could not get card holder details");
						pinAuthorityResponse.setViewPinResponseDocument(null);
						pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PROCESSING_CARD_HOLDER_DETAILS_FAILED);
						pinAuthorityResponse.setIsSuccess(false);
						return pinAuthorityResponse;
					 }
				}


			}
			else if(ViewPinConstants.PIN_CHANGE_REQUEST == requestType)
			{
				if((processedCardHolderDetails != null)&&
						(processedCardHolderDetails.getCardHolderVerificationValue() != null))
				{	
					String response = processPinChange(viewPinRequest, processedCardHolderDetails);
					if(null != response)
					{
						pinAuthorityResponse.setViewPinResponseDocument(response);
						pinAuthorityResponse.setErrorCode(0);
						pinAuthorityResponse.setIsSuccess(true);
					}
					else
					{
						getLogger().fatal("Could not get card holder details");
						pinAuthorityResponse.setViewPinResponseDocument(null);
						pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PROCESSING_CARD_HOLDER_DETAILS_FAILED);
						pinAuthorityResponse.setIsSuccess(false);
						return pinAuthorityResponse;
					  }
				}
				else
				 {
					getLogger().fatal("Could not get card holder details");
					pinAuthorityResponse.setViewPinResponseDocument(null);
					pinAuthorityResponse.setErrorCode(ErrorCodesConstants.PROCESSING_CARD_HOLDER_DETAILS_FAILED);
					pinAuthorityResponse.setIsSuccess(false);
					return pinAuthorityResponse;
				 }
				
			}
			
		}
	
		catch (CardHolderAuthenticationException e)
		{
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.CARD_HOLDER_AUTHENTICATION_FAILED);
			pinAuthorityResponse.setIsSuccess(false);
			getLogger().fatal("CardHolderAuthenticationException " + e.getMessage());
			return pinAuthorityResponse;
		}
		catch(ViewPinRequestException e)
		{
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
			pinAuthorityResponse.setIsSuccess(false);
			getLogger().fatal("ViewPinRequestException " + e.getMessage());
			return pinAuthorityResponse;
		}
		catch(ViewPinResponseException e)
		{
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_RESPONSE_ERROR);
			pinAuthorityResponse.setIsSuccess(false);
			getLogger().fatal("ViewPinResponseException " + e.getMessage());
			return pinAuthorityResponse;
		}
		catch(InvalidPinException e)
		{
			pinAuthorityResponse.setViewPinResponseDocument(null);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.INVALID_PIN_ERROR);
			pinAuthorityResponse.setIsSuccess(false);
			getLogger().fatal("InvalidPinException " + e.getMessage());
			return pinAuthorityResponse;
		}
	
		
		return pinAuthorityResponse;
		
	}
	
	 private static Logger getLogger ()
	 {
	       return logger;
	  }
	 

	//getting instance of Pin Authority Simulator
	    
    private void initViewPinAuthorityImplementation() throws Exception
	{
    	
    	String expression 										= null; 
    //	PinAuthority  objPinAuthoritySimulator 		= null;
    	String	 signingSubjectKeyIdentifier 		 			= null;
    	String 	 configurationPath 				  				= null;
    	long 	 replayOpportunityWindow 			 			= 0L;
    	int      maxFailedAuthenticationAttempts 	  			= 0;
    	
    	try
        {
            // Obtain a random number generator for the pin authority to use
            setRandomNumberGenerator(SecureRandom.getInstance("SHA1PRNG"));
            
            // Trigger random number generator seeding
            getRandomNumberGenerator().nextLong();
        }
        catch(Exception e)
        {
        	getLogger().fatal("instantiating random number generator " + e.getMessage());
        	
        	throw e;
        }
    	try{
			
				 URL configurationFile = Thread.currentThread().getContextClassLoader().getResource(ViewPinConstants.Authority_Configuration_File);
			    				 
				 XPathReader reader = new XPathReader(configurationFile.toString());
				 				 
				 expression = "/PinAuthorityConfiguration/PinAuthoritySigningKeySKI";
				
				 String SSLXpath = (String)reader.read(expression , XPathConstants.STRING);
				 if(SSLXpath != null)
				 signingSubjectKeyIdentifier =  SSLXpath.trim();
				
				 expression = "/PinAuthorityConfiguration/TrustStoreLocation";
				 String configPathXpath = (String)reader.read(expression , XPathConstants.STRING);
				 if(configPathXpath != null)
				 configurationPath = configPathXpath.trim();
					
				 expression = "/PinAuthorityConfiguration/MaximumFailedAuthenticationAttempts";
				 String maxFailedAuthenticationAttemptsXpath = (String)reader.read(expression , XPathConstants.STRING);
				 if(maxFailedAuthenticationAttemptsXpath != null)
				 {
					 Double maxFailed = Double.valueOf(maxFailedAuthenticationAttemptsXpath);
					 maxFailedAuthenticationAttempts = maxFailed.intValue();
				 }
				
				 expression = "/PinAuthorityConfiguration/MaximumReplayOpportunityWindow";
				 String maximumReplayOpportunityWindowPath = (String)reader.read(expression , XPathConstants.STRING);
				 if(maximumReplayOpportunityWindowPath != null)
				 {
					 Double maxReplay = Double.valueOf(maximumReplayOpportunityWindowPath);
					 maximumReplayOpportunityWindow = maxReplay.longValue();
				 }
					
							
				
				// do what the PinAuthority constructor used to do
				
				
				 // Store the configuration path
		        ViewPinConstants.setCONFIGURATION_FILE_PATH(configurationPath);

		        // Initialise Apache XML security
		        Init.init();

		        // Initialise agent key store
		        setAgentKeyStore(Utils.initKeyStore(new File(ViewPinConstants.CONFIGURATION_FILE_PATH + "/" + ViewPinConstants.AGENT_KEYSTORE_FILE), ViewPinConstants.DEFAULT_KEYSTORE_PASSWORD.toCharArray()));

		        // Initialise authority key store
		        setAuthorityKeyStore(Utils.initKeyStore(new File(ViewPinConstants.CONFIGURATION_FILE_PATH + "/" + ViewPinConstants.AUTHORITY_KEYSTORE_FILE), ViewPinConstants.DEFAULT_KEYSTORE_PASSWORD
		                .toCharArray()));

		        // Initialise pin retrieval response generator
		        setPinRetrievalResponseGenerator(new PinRetrievalResponseGenerator(getAuthorityKeyStore(), signingSubjectKeyIdentifier));

		        // Store random number generator
		        setRandomNumberGenerator(randomNumberGenerator);

		        // Initialise schemas
		        initSchemas();

		        /*
		        // Initialise collection of card accounts
		        setCardAccounts(new CardAccounts());
				*/
		        
		       // Store maximum replay opportunity window
		        setMaximumReplayOpportunityWindow(maximumReplayOpportunityWindow);
			
			
		}
		 catch(Exception p)
		 {
			
			 getLogger().fatal("Error getting new PinAuthority Configuration " + p.getMessage()); 
			 throw p;
		 }
		
		
		 
	}
    
    /**
     * Processes a PINRetrievalRequest
     * 
     * @param pinRetrievalRequest
     * @param primaryAccountNumber
     * 
     * @return The PINRetrievalResponse
     * 
     * @throws UnknownCardAccountException
     * @throws CardHolderAuthenticationException
     * @throws PinRetrievalResponseException
     * @throws PinRetrievalRequestException
     * @throws CardAccountLockedException
     */
    public PinRetrievalResponse processPinRetrieval (String pinRetrievalRequest, CardHolderDetails cardHolderDetails) throws  CardHolderAuthenticationException,
            ViewPinRequestException, ViewPinResponseException
    {
        PinRetrievalResponse pinRetrievalResponse;
        Document pinRetrievalRequestDocument;

        pinRetrievalResponse = null;
        pinRetrievalRequestDocument = null;

        try
        {
            // Create the pinRetrievalRequestDocument from the pinRetrievalRequest String
            pinRetrievalRequestDocument = loadViewPinRequestDocumentFromString(pinRetrievalRequest);
        }
        catch (Exception e)
        {
            getLogger().error("loading pin retrieval request document " + e.getMessage());

            throw new ViewPinRequestException("loading pin retrieval request document");
        }

        if (getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request document prior to processing
                String s = new String(Utils.serialise(pinRetrievalRequestDocument));
                
            }
            catch (Exception e)
            {
                getLogger().warn("serialising pin retrieval request document " + e.getMessage());
            }
        }

        // Process pin retrieval request document
        pinRetrievalResponse = processPinRetrievalRequest(pinRetrievalRequestDocument, cardHolderDetails);

        return pinRetrievalResponse;
    }

    /**
     * Processes a PINChangeRequest
     * 
     * @param pinChangeRequest
     * @param primaryAccountNumber
     * 
     * @return The PINChangeData
     * 
     * @throws UnknownCardAccountException
     * @throws CardHolderAuthenticationException
     * @throws ViewPinResponseException
     * @throws ViewPinRequestException
     * @throws CardAccountLockedException
     */
    public String processPinChange(String pinChangeRequest, CardHolderDetails cardHolderDetails) throws  CardHolderAuthenticationException,
    ViewPinRequestException, ViewPinResponseException, InvalidPinException
	{
    	
		Document pinChangeRequestDocument;
		String newPin;
		byte[] PINBlock;
		PINBlock PinBlock = null;
		//PinChangeData pinChangeData;
		
		pinChangeRequestDocument = null;
		newPin = null;
		//pinChangeData =  new PinChangeData();
		
		try
		{
		    // Create the pinRetrievalRequestDocument from the pinRetrievalRequest String
			pinChangeRequestDocument = loadViewPinRequestDocumentFromString(pinChangeRequest);
		}
		catch (Exception e)
		{
		    getLogger().error("loading pin change request document " + e.getMessage());
		
		    throw new ViewPinRequestException("loading pin change request document");
		}
			
		if (getLogger().isDebugEnabled() == true)
		{
		    try
		    {
		        // Output pin retrieval request document prior to processing
		        String s = new String(Utils.serialise(pinChangeRequestDocument));
		    }
		    catch (Exception e)
		    {
		        getLogger().warn("serialising pin change request document " + e.getMessage());
		    }
		}
		
		// Process pin Change request document
		//processPinChangeRequest(pinChangeRequestDocument, primaryAccountNumber, pinChangeData);
		newPin = processPinChangeRequest(pinChangeRequestDocument, cardHolderDetails);
		
		
		return newPin;
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
        if (parsingValidationResult.isValid() == false)
        {
            // Failed to parse pin retrieval response document OK
			if(null != parsingValidationResult.getException())
			{
	            getLogger().error("parsing pin retrieval request document" + parsingValidationResult.getException().getMessage());
			}
            throw new IOException("parsing pin retrieval request document");
        }

        return viewPinRequestDocument;
    }

    private PinRetrievalResponse processPinRetrievalRequest (Document pinRetrievalRequestDocument, CardHolderDetails cardHolderDetails) throws 
            CardHolderAuthenticationException, ViewPinRequestException, ViewPinResponseException
    {
        PinRetrievalResponse pinRetrievalResponse;
        String sessionEncrytionMethodAlgorithm;
        SecretKey sessionKey;
        String rootElement;
        String sessionEncryptionPath;
        String schemaCVVPath;
		String expiryDatePath;
        boolean cardHolderAuthentic;

        pinRetrievalResponse = null;
        sessionEncrytionMethodAlgorithm = null;
        sessionKey = null;
        rootElement = "PinRetrievalRequest";
        cardHolderAuthentic = false;
        sessionEncryptionPath = "/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue/xenc:EncryptedData";
        schemaCVVPath = "/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue"; 
        expiryDatePath = "/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:ExpiryDate";
        
         // Validate pin retrieval request document prior to decryption
        if (validateViewPinRequestDocumentPreDecryption(pinRetrievalRequestDocument, rootElement) == false)
        {
            // Invalid pin retrieval request document prior to decryption
            getLogger().error("invalid pin retrieval request prior to decryption");

            throw new ViewPinRequestException("invalid pin retrieval request prior to decryption");
        }

        try
        {
            // Establish session encryption algorithm
            sessionEncrytionMethodAlgorithm = establishSessionEncryptionMethodAlgorithm(pinRetrievalRequestDocument, sessionEncryptionPath);
        }
        catch (Exception e)
        {
            getLogger().error("establishing session encryption algorithm " + e.getMessage());

            throw new ViewPinRequestException("establishing session encryption algorithm");
        }

        getLogger().debug("session encryption algorithm = " + sessionEncrytionMethodAlgorithm);

        try
        {
            // Recover session key
            sessionKey = XmlEncryption.recoverSessionKey(pinRetrievalRequestDocument, sessionEncrytionMethodAlgorithm, getAuthorityKeyStore(), rootElement);
        }
        catch (Exception e)
        {
            getLogger().error("recovering session key " + e.getMessage());

            throw new ViewPinRequestException("recovering session key");
        }

        // Did we recover session key OK?
        if (sessionKey == null)
        {
            getLogger().error("recovering session key");

            throw new ViewPinRequestException("recovering session key");
        }

        try
        {
            // Process EncryptedData elements
            processEncryptedData(pinRetrievalRequestDocument, sessionKey, cardHolderDetails);
        }
        catch(CardHolderAuthenticationException e)
		{
			 // Card holder failed authentication
		    getLogger().warn("card holder authentication failed");
			
		    throw new CardHolderAuthenticationException();
		}
        catch (Exception e)
        {
            getLogger().error("processing encrypted data " + e.getMessage());
          //  e.printStackTrace();

            throw new ViewPinRequestException("processing encrypted data");
        }

        if (getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request post decryption
               String s = new String(Utils.serialise(pinRetrievalRequestDocument));

            }
            catch (Exception e)
            {
                getLogger().warn("serialising pin retrieval request document post decryption " + e.getMessage());
            }
        }

        // Validate pin retrieval request post decryption
        if (validateViewPinRequestDocumentPostDecryption(pinRetrievalRequestDocument) == false)
        {
            // Failed to validate pin retrieval request post decryption
            getLogger().error("invalid pin retrieval request post decryption");

            throw new ViewPinRequestException("invalid pin retrieval request post decryption");
        }



        try
        {
            // Authentic card holder
            cardHolderAuthentic = authenticateCardHolder(cardHolderDetails, pinRetrievalRequestDocument, schemaCVVPath, rootElement, expiryDatePath);
        }
        catch (Exception e)
        {
            getLogger().error("authenticating card holder " + e.getMessage());

            throw new ViewPinRequestException("authenticating card holder");
        }
       
        // Did we authentic card holder OK?
        if (cardHolderAuthentic == false)
        {
            // Card holder failed authentication
            getLogger().warn("card holder  failed authentication");
            throw new CardHolderAuthenticationException();
           
        }
     
        
        String signatureMethodAlgorithm = XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA256;
        
        String digestMethodAlgorithm = XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA256;

        // Generate pin retrieval response
        pinRetrievalResponse = getPinRetrievalResponseGenerator().generatePinRetrievalResponse(pinRetrievalRequestDocument, sessionKey, sessionEncrytionMethodAlgorithm, cardHolderDetails,
                signatureMethodAlgorithm, digestMethodAlgorithm, getRandomNumberGenerator());
		
		getLogger().info("card holder successfully authenticated");
        return pinRetrievalResponse;
    }

    private String processPinChangeRequest (Document pinChangeRequestDocument, CardHolderDetails cardHolderDetails) throws 
    CardHolderAuthenticationException, ViewPinRequestException, ViewPinResponseException
	{
		
		String sessionEncrytionMethodAlgorithm;
		SecretKey sessionKey;
		boolean cardHolderAuthentic;
		String rootElement;
		String sessionEncryptionPath;
		String schemaCVVPath;
		String newPin;
		String expiryDatePath;
	
		sessionEncrytionMethodAlgorithm = null;
		sessionKey = null;
		cardHolderAuthentic = false;
		rootElement = "PinChangeRequest";
		sessionEncryptionPath = "/vp:PinChangeRequest/vp:PinChangeData/vp:VerificationValue/xenc:EncryptedData";
		schemaCVVPath = "/vp:PinChangeRequest/vp:PinChangeData/vp:VerificationValue";
		expiryDatePath = "/vp:PinChangeRequest/vp:PinChangeData/vp:ExpiryDate";
		newPin = null;
	
		// Validate pin retrieval request document prior to decryption
		if (validateViewPinRequestDocumentPreDecryption(pinChangeRequestDocument, rootElement) == false)
		{
		    // Invalid pin retrieval request document prior to decryption
		    getLogger().error("invalid pin retrieval request prior to decryption");
		
		    throw new ViewPinRequestException("invalid pin retrieval request prior to decryption");
		}
		
		try
		{
		    // Establish session encryption algorithm
		    sessionEncrytionMethodAlgorithm = establishSessionEncryptionMethodAlgorithm(pinChangeRequestDocument, sessionEncryptionPath);
		}
		catch (Exception e)
		{
		    getLogger().error("establishing session encryption algorithm " + e.getMessage());
		
		    throw new ViewPinRequestException("establishing session encryption algorithm");
		}
		
		try
		{
		    // Recover session key
		    sessionKey = XmlEncryption.recoverSessionKey(pinChangeRequestDocument, sessionEncrytionMethodAlgorithm, getAuthorityKeyStore(), rootElement);
		}
		catch (Exception e)
		{
		    getLogger().error("recovering session key " + e.getMessage());
		
		    throw new ViewPinRequestException("recovering session key");
		}
		
		// Did we recover session key OK?
		if (sessionKey == null)
		{
		    getLogger().error("recovering session key");
		
		    throw new ViewPinRequestException("recovering session key");
		}
		
		try
		{
		    // Process EncryptedData elements

		   newPin =  processPinChangeData(pinChangeRequestDocument, sessionKey, cardHolderDetails);
		}
		catch(CardHolderAuthenticationException e)
		{
			 // Card holder failed authentication
		    getLogger().warn("card holder authentication failed");
			
		    throw new CardHolderAuthenticationException();
		}
		catch (Exception e)
		{
		    getLogger().error("processing encrypted data " + e.getMessage());
		
		    throw new ViewPinRequestException("processing pin Changed data");
		}
		
		if (getLogger().isDebugEnabled() == true)
		{
		    try
		    {
		        // Output pin retrieval request post decryption
		        String s = new String(Utils.serialise(pinChangeRequestDocument));
		    }
		    catch (Exception e)
		    {
		        getLogger().warn("serialising pin change request document post decryption " + e.getMessage());
		    }
		}
		
		// Validate pin retrieval request post decryption
		if (validateViewPinRequestDocumentPostDecryption(pinChangeRequestDocument) == false)
		{
		    // Failed to validate pin retrieval request post decryption
		    getLogger().error("invalid pin change request post decryption");
		
		    throw new ViewPinRequestException("invalid pin change request post decryption");
		}
		
		try
		{
		    // Authentic card holder
		    cardHolderAuthentic = authenticateCardHolder(cardHolderDetails, pinChangeRequestDocument, schemaCVVPath, rootElement, expiryDatePath);
		}
		catch (Exception e)
		{
		    getLogger().error("authenticating card holder " + e.getMessage());
		
		    throw new ViewPinRequestException("authenticating card holder");
		}
		
		// Did we authentic card holder OK?
		if (cardHolderAuthentic == false)
		{
		    // Card holder failed authentication
		    getLogger().warn("card holder failed authentication");
			
		    throw new CardHolderAuthenticationException();
		}
		
		getLogger().info("card holder successfully authenticated");
		
		return newPin;
		 
	}
    
    private boolean validateViewPinRequestDocumentPreDecryption (Document viewPinRequestDocument, String rootElement) throws ViewPinRequestException
    {
        boolean valid;
        ValidationResult validationResult;

        valid = false;
        validationResult = null;

        try
        {
            // Validate pin retrieval request against pre-decryption schema
            validationResult = Utils.validateDocument(viewPinRequestDocument, getPreDecryptionSchema());
            // Dump serialised pin retrieval request
            String s = new String(Utils.serialise(viewPinRequestDocument));
            
        }
        catch (Exception e)
        {
            getLogger().error("pre-decryption schema validation processing " + e.getMessage());

            throw new ViewPinRequestException("pre-decryption schema validation processing");
        }

        // Did we validate pin retrieval request OK?
        if (validationResult.isValid() == false)
        {
            // Failed to validate pin retrieval request against pre-decryption schema
        	if(validationResult.getException() != null)
        	{
        		getLogger().error("validating view Pin  request document against pre-decryption schema", validationResult.getException());
        	}
            valid = false;

            return valid;
        }

        getLogger().debug("validated view Pin request document against pre-decryption schema OK");

        try
        {
            // Verify pin retrieval request document signature
            
            if (XmlSignature.verifySignature(viewPinRequestDocument, getAgentKeyStore(), rootElement) == false)
            {
                getLogger().error("view Pin  request signature verification failed");

                valid = false;

                return valid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("signature verification processing " + e.getMessage());

            throw new ViewPinRequestException("signature verification processing");
        }

        getLogger().debug("view Pin request signature verified OK");

        try
        {
            // Validate time stamp
            if (validateTimestamp(viewPinRequestDocument,rootElement) == false)
            {
                getLogger().warn("view Pin request outside replay opportunity window");

                valid = false;

                return valid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("timestamp validation processing " + e.getMessage());

            throw new ViewPinRequestException("timestamp validation processing");
        }

        valid = true;

        return valid;
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
            //dumping the schema
            // Dump serialised pin retrieval request
            String s = new String(Utils.serialise(viewPinRequestDocument));
          
        }
        catch (Exception e)
        {
            getLogger().error("validating pin retrieval request against post decryption schema");

            throw new ViewPinRequestException("validating pin retrieval request against post decryption schema");
        }

        // Did we validate pin retrieval request against post decryption schema OK?
        if (validationResult.isValid() == false)
        {
            // Failed to validate pin retrieval request against post decryption schema
        	 if( validationResult.getException() != null)
             {
        		 getLogger().error("failed to validate pin retrieval request against post decryption schema", validationResult.getException());
                   
            	validationResult.getException().printStackTrace();
            }

            valid = false;

            return valid;
        }

        valid = true;

        return valid;
    }


    private boolean validateTimestamp (Document viewPinRequestDocument, String rootElement) throws XPathExpressionException, ParseException, XPathFactoryConfigurationException
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

        if (getLogger().isDebugEnabled() == true)
        {
            getLogger().debug("timestamp = " + timestamp.getTime());
            getLogger().debug("current time = " + currentTime.getTime());
            getLogger().debug("elapsed time = " + elapsedTime);
            getLogger().debug("positive maximum replay opportunity window = " + +getMaximumReplayOpportunityWindow());
            getLogger().debug("negative maximum replay opportunity window = " + -getMaximumReplayOpportunityWindow());
        }
     
        // Is elapsed time outside maximum replay opportunity window?
        if (elapsedTime < -getMaximumReplayOpportunityWindow() || elapsedTime > +getMaximumReplayOpportunityWindow())
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
    
    private void processEncryptedData (Document pinRetrievalRequestDocument, SecretKey sessionKey, CardHolderDetails cardHolderDetails) throws XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
    NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException,CardHolderAuthenticationException,XPathFactoryConfigurationException
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
		if (expiryDateElement != null)
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
		if (primaryAccountNumberElement != null)
		{
		    // Primary account number element is present, get primary account number encrypted data element
		    primaryAccountNumberEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", primaryAccountNumberElement, XPathConstants.NODE);
		
		    // Decrypt primary account number encrypted data element
		    primaryAccountNumber = XmlEncryption.decryptEncryptedData(sessionKey, primaryAccountNumberEncryptedDataElement);
		
		    // Replace encrypted primary account number with decrypted primary account number
		    primaryAccountNumberElement.replaceChild(pinRetrievalRequestDocument.createTextNode(primaryAccountNumber), primaryAccountNumberEncryptedDataElement);
		
		    if(primaryAccountNumber.compareToIgnoreCase(cardHolderDetails.getPrimaryAccountNumber()) != 0)
            	throw new CardHolderAuthenticationException();
		}
		
		// Get pin retrieval request root element
		pinRetrievalRequestRootElement = pinRetrievalRequestDocument.getDocumentElement();
		
		// Get EncryptedKey element
		encryptedKeyElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/xenc:EncryptedKey[@Id = 'EK']", pinRetrievalRequestDocument, XPathConstants.NODE);
		
		// Remove EncryptedKey element from pin retrieval request
		pinRetrievalRequestRootElement.removeChild(encryptedKeyElement);
		
		// Remove XML digital signature and XML encryption namespace declarations from pin retrieval request
		// root element
		pinRetrievalRequestRootElement.removeAttribute("xmlns:xenc");
		pinRetrievalRequestRootElement.removeAttribute("xmlns:ds");
	}

    private String processPinChangeData (Document pinChangeRequestDocument, SecretKey sessionKey,CardHolderDetails cardHolderDetails) throws XPathExpressionException, InvalidKeyException, NoSuchAlgorithmException,
    NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, XPathFactoryConfigurationException,CardHolderAuthenticationException, ViewPinRequestException, Exception
    {
		XPath xp;
	
		Element verificationValueElement;
		Element verificationValueEncryptedDataElement;
		String verificationValue;
		String oldPindecrypted;
		String newPindecrypted;
		String pinChangeDataPanDecrypted;
		String newPinChangeDataPanDecrypted;
		Element expiryDateElement;
		Element expiryDateMonthElement;
		Element expiryDateYearElement;
		Element expiryDateMonthEncryptedDataElement;
		Element expiryDateYearEncryptedDataElement;
		String decryptedExpiryDateMonth;
		String decryptedExpiryDateYear;
		Element oldPin;
		Element newPin;
		Element pinChangeDataPan;
		Element newPinChangeDataPan;
		Element oldPinEncryptedDataElement;
		Element newPinEncryptedDataElement;
		Element primaryAccountNumberElement;
		Element primaryAccountNumberEncryptedDataElement;
		String primaryAccountNumber;
		Element pinChangeRequestRootElement;
        Element encryptedKeyElement;
        Element pinChangeDataPanEncryptedDataElement;
        Element newPinChangeDataPanEncryptedDataElement;
		boolean	matchedOldPin;
		String pinChangeResponse;
		
		xp = null;
		verificationValueElement = null;
		verificationValueEncryptedDataElement = null;
		verificationValue = null;
		pinChangeDataPanDecrypted = null;
		newPinChangeDataPanDecrypted = null;
		oldPin = null;
		newPin = null;
		pinChangeDataPan = null;
		newPinChangeDataPan = null;
		expiryDateElement = null;
		oldPindecrypted = null;
		newPindecrypted = null;
		oldPinEncryptedDataElement = null;
		newPinEncryptedDataElement = null;
		expiryDateMonthElement = null;
		expiryDateYearElement = null;
		expiryDateMonthEncryptedDataElement = null;
		expiryDateYearEncryptedDataElement = null;
		decryptedExpiryDateMonth = null;
		decryptedExpiryDateYear = null;
		primaryAccountNumberElement = null;
		primaryAccountNumberEncryptedDataElement = null;
		primaryAccountNumber = null;
		pinChangeRequestRootElement = null;
        encryptedKeyElement = null;
        pinChangeDataPanEncryptedDataElement = null;
        newPinChangeDataPanEncryptedDataElement = null;
        pinChangeResponse = null;
        
		
		// Initialise XPath
		xp = Utils.createXPath();
		
		// Get verification value element
		verificationValueElement = (Element) xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:VerificationValue", pinChangeRequestDocument, XPathConstants.NODE);
		
		// Get verification value EncryptedDataElement
		verificationValueEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", verificationValueElement, XPathConstants.NODE);
		
		// Decrypted verification value
		verificationValue = XmlEncryption.decryptEncryptedData(sessionKey, verificationValueEncryptedDataElement);
		
		
		   // Replace encrypted verification value with decrypted verification value
        verificationValueElement.replaceChild(pinChangeRequestDocument.createTextNode(verificationValue), verificationValueEncryptedDataElement);
        
    
		// Get expiry date element
		expiryDateElement = (Element) xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:ExpiryDate", pinChangeRequestDocument, XPathConstants.NODE);
		
		// Is expiry date element present?
		if (expiryDateElement != null)
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
            expiryDateMonthElement.replaceChild(pinChangeRequestDocument.createTextNode(decryptedExpiryDateMonth), expiryDateMonthEncryptedDataElement);
            expiryDateYearElement.replaceChild(pinChangeRequestDocument.createTextNode(decryptedExpiryDateYear), expiryDateYearEncryptedDataElement);
            
		}  
		    
		// Get primary account number element
		primaryAccountNumberElement = (Element) xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:PrimaryAccountNumber", pinChangeRequestDocument, XPathConstants.NODE);
		
		// Is primary account number element present?
		if (primaryAccountNumberElement != null)
		{
			// Primary account number element is present, get primary account number encrypted data element
		    primaryAccountNumberEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", primaryAccountNumberElement, XPathConstants.NODE);
		
		    // Decrypt primary account number encrypted data element
		    primaryAccountNumber = XmlEncryption.decryptEncryptedData(sessionKey, primaryAccountNumberEncryptedDataElement);

		    // Replace encrypted primary account number with decrypted primary account number
            primaryAccountNumberElement.replaceChild(pinChangeRequestDocument.createTextNode(primaryAccountNumber), primaryAccountNumberEncryptedDataElement);
            
            if(primaryAccountNumber.compareToIgnoreCase(cardHolderDetails.getPrimaryAccountNumber()) != 0)
            	throw new CardHolderAuthenticationException();
            	
		   	  
		}
		
		//Get OldPin 
		oldPin = (Element)xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:OldPin/vp:Pin", pinChangeRequestDocument, XPathConstants.NODE);
		
		// Get OldPin EncryptedDataElement
		oldPinEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", oldPin, XPathConstants.NODE);
		
		oldPindecrypted = XmlEncryption.decryptEncryptedData(sessionKey, oldPinEncryptedDataElement);
		
		 // Replace encrypted primary account number with decrypted primary account number
		oldPin.replaceChild(pinChangeRequestDocument.createTextNode(oldPindecrypted), oldPinEncryptedDataElement);
		
		
		
				
		
		//Get Pan from PinchangeData 
		pinChangeDataPan = (Element)xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:OldPin/vp:PrimaryAccountNumber", pinChangeRequestDocument, XPathConstants.NODE);
		// Is primary account number element present?
		if (pinChangeDataPan != null)
		{
			
			// Get Pan EncryptedDataElement
			pinChangeDataPanEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", pinChangeDataPan, XPathConstants.NODE);
			
			pinChangeDataPanDecrypted = XmlEncryption.decryptEncryptedData(sessionKey, pinChangeDataPanEncryptedDataElement);
			
			 // Replace encrypted primary account number with decrypted primary account number
			pinChangeDataPan.replaceChild(pinChangeRequestDocument.createTextNode(pinChangeDataPanDecrypted), pinChangeDataPanEncryptedDataElement);
			
			
		}
		//Get New Pin 
		newPin = (Element)xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:NewPin/vp:Pin", pinChangeRequestDocument, XPathConstants.NODE);
		 
		// Get New Pin EncryptedDataElement
		newPinEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", newPin, XPathConstants.NODE);
		
		newPindecrypted = XmlEncryption.decryptEncryptedData(sessionKey, newPinEncryptedDataElement);
		
		 // Replace encrypted PinChangedData with decrypted PinchangedData
		newPin.replaceChild(pinChangeRequestDocument.createTextNode(newPindecrypted), newPinEncryptedDataElement);
				
		
					
		//Get Pan from PinchangeData (newCardPin) 
		newPinChangeDataPan = (Element)xp.evaluate("/vp:PinChangeRequest/vp:PinChangeData/vp:NewPin/vp:PrimaryAccountNumber", pinChangeRequestDocument, XPathConstants.NODE);
		
		// Is primary account number element present?
		if (newPinChangeDataPan != null)
		{
			// Get Pan EncryptedDataElement (newCardPin)
			newPinChangeDataPanEncryptedDataElement = (Element) xp.evaluate("xenc:EncryptedData", newPinChangeDataPan, XPathConstants.NODE);
			
			newPinChangeDataPanDecrypted = XmlEncryption.decryptEncryptedData(sessionKey, newPinChangeDataPanEncryptedDataElement);
			
			 // Replace encrypted primary account number with decrypted primary account number (newCardPin)
			newPinChangeDataPan.replaceChild(pinChangeRequestDocument.createTextNode(newPinChangeDataPanDecrypted), newPinChangeDataPanEncryptedDataElement);
			
		}
		// Get pin retrieval request root element
        pinChangeRequestRootElement = pinChangeRequestDocument.getDocumentElement();

        // Get EncryptedKey element
        encryptedKeyElement = (Element) xp.evaluate("/vp:PinChangeRequest/xenc:EncryptedKey[@Id = 'EK']", pinChangeRequestDocument, XPathConstants.NODE);

        // Remove EncryptedKey element from pin retrieval request
        pinChangeRequestRootElement.removeChild(encryptedKeyElement);

        // Remove XML digital signature and XML encryption namespace declarations from pin retrieval request
        // root element
        pinChangeRequestRootElement.removeAttribute("xmlns:xenc");
        pinChangeRequestRootElement.removeAttribute("xmlns:ds");
        
        //setting pinChangeData
        /*
        pinChangeData.setCardHolderVerificationValue(verificationValue);
        pinChangeData.setExpiryDate(decryptedExpiryDateMonth+decryptedExpiryDateYear);
        pinChangeData.setPrimaryAccountNumber(primaryAccountNumber);
        pinChangeData.setOldPin(oldPindecrypted);
        pinChangeData.setNewPin(newPindecrypted);
         */


		 pinChangeResponse = createPINChangeResponse(oldPindecrypted, newPindecrypted, cardHolderDetails);
	        
		return pinChangeResponse;
			
        				
	}

    
	private CardHolderDetails processCardHolderDetails(CardHolderDetails cardHolderDetails) throws Exception
	{
		CardHolderDetails processedCardHolderDetails=null;
		String encryptionKeyName=null;
		String encryptionKeyType=null;
		String encryptionTransformation=null;
		String decryptedElement=null;
		String expression=null;
		String isEncrypted=null;
		
		KeyStore authorityKeyStore=null;
		Key encryptionKey=null;
	
	
		try
		{
			processedCardHolderDetails=new CardHolderDetails();
			//set the Input and Output PINBlock

			processedCardHolderDetails.setOutputPINBlockFormat(cardHolderDetails.getOutputPINBlockFormat());
			processedCardHolderDetails.setInputPINBlockFormat(cardHolderDetails.getInputPINBlockFormat());
			
			//get new Xpath element
			URL configurationFile = Thread.currentThread().getContextClassLoader().getResource(ViewPinConstants.Authority_Configuration_File);
			
			XPathReader reader = new XPathReader(configurationFile.toString());
				    		    	
			//initialize key store
			authorityKeyStore = Utils.initKeyStore(null,null);
			
		
			//Process PAN Element based on BankSimulator configuration file
			if(cardHolderDetails.getPrimaryAccountNumber() != null)
			{
				expression = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/Encrypted";
				String valueFromXpath = (String)reader.read(expression , XPathConstants.STRING);
				
				if(valueFromXpath != null)
					isEncrypted =  valueFromXpath.trim();
							
				if(isEncrypted != null)
				{
					if (0 == isEncrypted.compareToIgnoreCase("true"))
					{
						expression = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/EncryptionProperties/KeyIdentifier";
						String encryptionKeyNameXpath = (String)reader.read(expression , XPathConstants.STRING);
						if(encryptionKeyNameXpath != null)
						encryptionKeyName = encryptionKeyNameXpath.trim();
						
								
						expression = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/EncryptionProperties/Transformation";
						String encryptionTransformationXpath = (String)reader.read(expression , XPathConstants.STRING);
						if(encryptionTransformationXpath != null)
						encryptionTransformation =encryptionTransformationXpath.trim();
						
						encryptionKey=authorityKeyStore.getKey(encryptionKeyName, null);
						
						decryptedElement=new String(EncryptDecrypt.decrypt(cardHolderDetails.getPrimaryAccountNumber(),encryptionKey,encryptionTransformation));
										
						processedCardHolderDetails.setPrimaryAccountNumber(decryptedElement);
					}
					else
					{
						processedCardHolderDetails.setPrimaryAccountNumber(cardHolderDetails.getPrimaryAccountNumber());
					}
				}
				else
				{
					getLogger().debug(" could not process CardHolderDetails");
					return processedCardHolderDetails;
				}
					
			}
			//Process PIN Element based on BankSimulator configuration file
			
			Iterator<Pin> cardAccountPins;
					
			ArrayList<String> PinList = new ArrayList<String>();
			
			ArrayList<String> decryptedPinList = new ArrayList<String>();
			int PINElementCount = 0;
			expression = "count(//PinAuthorityConfiguration/CardHolderDataElements/PINElement)";
			
			String countPINPath = (String)reader.read(expression , XPathConstants.STRING);
			if(countPINPath != null)
			{
				Double countPIN = Double.valueOf(countPINPath);
				PINElementCount = countPIN.intValue();
			}
			
			ArrayList<String> cardHolderPins = new ArrayList<String>();

			PinList  = cardHolderDetails.getPinNumbers();
			if(PinList != null)
			{
						
				// get the decrypted PINs
				for(int i=0; i<PinList.size();i++)
				{
					
					expression = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/Encrypted";
					String valueFromXpath = (String)reader.read(expression , XPathConstants.STRING);
					
					if(valueFromXpath != null)
						isEncrypted =  valueFromXpath.trim();
					
					byte[] decryptedPin = null;
					//String pin = cardHolderDetails.getPins().next().getPin();
					
					String pin = PinList.get(i);
					if(isEncrypted != null)
					{
						if (0 == isEncrypted.compareToIgnoreCase("true"))
						{
							
							expression = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/EncryptionProperties/KeyIdentifier";
							String encryptionKeyNameXpath = (String)reader.read(expression , XPathConstants.STRING);
							if(encryptionKeyNameXpath != null)
							encryptionKeyName = encryptionKeyNameXpath.trim();
							
							//encryptionKeyType=cardHolderPANElement.getString("EncryptionProperties.KeyType(0)");
							
							expression = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/EncryptionProperties/Transformation";
							String encryptionTransformationXpath = (String)reader.read(expression , XPathConstants.STRING);
							if(encryptionTransformationXpath != null)
							encryptionTransformation =encryptionTransformationXpath.trim();
							
							encryptionKey = authorityKeyStore.getKey(encryptionKeyName,null);
							decryptedPin = EncryptDecrypt.decrypt(pin, encryptionKey, encryptionTransformation);
		
							Pin pin2 = null;
							
							try
							{
								if(processedCardHolderDetails.getPrimaryAccountNumber() != null)
									pin2 = PINBlock.getPINFromPINBlock(new String(Hex.encode(decryptedPin)),new PrimaryAccountNumber(processedCardHolderDetails.getPrimaryAccountNumber()));
								else
									pin2 = PINBlock.getPINFromPINBlock(new String(Hex.encode(decryptedPin)),null);
		
							}
							catch(InvalidPINBlockFormatException e)
							{
								getLogger().debug("InvalidPINBlockFormatException");
							}
							catch(InvalidPrimaryAccountNumberException e)
							{
								getLogger().debug("InvalidPrimaryAccountNumberException");
							}
							
							decryptedPinList.add(pin2.getPin());
																	
						}
						else
						{
							decryptedPinList.add(pin);
						}
					}
					else
					{
						getLogger().debug(" could not process CardHolderDetails");
						return processedCardHolderDetails;
					}
				}		
					
				
				
				// Add the decrypted Pins String in  <ArrayList<String> to processed Card holderDetails
				
				processedCardHolderDetails.setPinNumbers(decryptedPinList);
			}
			//processedCardHolderDetails.setPinNumbers(PinList);
			//Process CVV Element based on BankSimulator configuration file
			expression = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/Encrypted";
			String CVVValueFromXpath = (String)reader.read(expression , XPathConstants.STRING);
			
			if(CVVValueFromXpath != null)
				isEncrypted =  CVVValueFromXpath.trim();
			
			if(isEncrypted != null)
			{
				if (0 == isEncrypted.compareToIgnoreCase("true"))
				{
					expression = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/EncryptionProperties/KeyIdentifier";
					String encryptionKeyNameXpath = (String)reader.read(expression , XPathConstants.STRING);
					if(encryptionKeyNameXpath != null)
					encryptionKeyName = encryptionKeyNameXpath.trim();
					//encryptionKeyType=cardHolderPANElement.getString("EncryptionProperties.KeyType(0)");
					
					expression = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/EncryptionProperties/Transformation";
					String encryptionTransformationXpath = (String)reader.read(expression , XPathConstants.STRING);
					if(encryptionTransformationXpath != null)
					encryptionTransformation =encryptionTransformationXpath.trim();
					
					encryptionKey=authorityKeyStore.getKey(encryptionKeyName,null);
					decryptedElement=new String(EncryptDecrypt.decrypt(cardHolderDetails.getCardHolderVerificationValue(), encryptionKey, encryptionTransformation));
					
					processedCardHolderDetails.setCardHolderVerificationValue(decryptedElement);
				}
				else
				{
					processedCardHolderDetails.setCardHolderVerificationValue(cardHolderDetails.getCardHolderVerificationValue());
				}
			}
			else
			{
				getLogger().debug(" could not process CardHolderDetails");
				return processedCardHolderDetails;
			}
			
			//Process CVV Element based on BankSimulator configuration file
			if(cardHolderDetails.getExpiryDate() != null)
			{
				expression = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/Encrypted";
				String valueFromXpath = (String)reader.read(expression , XPathConstants.STRING);
				
				if(valueFromXpath != null)
					isEncrypted =  valueFromXpath.trim();
				
				if(isEncrypted != null)
				{
					if (0 == isEncrypted.compareToIgnoreCase("true"))
					{
						expression = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/EncryptionProperties/KeyIdentifier";
						String encryptionKeyNameXpath = (String)reader.read(expression , XPathConstants.STRING);
						if(encryptionKeyNameXpath != null)
						encryptionKeyName = encryptionKeyNameXpath.trim();
						
						//encryptionKeyType=cardHolderPANElement.getString("EncryptionProperties.KeyType(0)");
						
						expression = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/EncryptionProperties/Transformation";
						String encryptionTransformationXpath = (String)reader.read(expression , XPathConstants.STRING);
						if(encryptionTransformationXpath != null)
						encryptionTransformation =encryptionTransformationXpath.trim();
						
						encryptionKey=authorityKeyStore.getKey(encryptionKeyName,null);
						decryptedElement=new String(EncryptDecrypt.decrypt(cardHolderDetails.getExpiryDate(), encryptionKey, encryptionTransformation));
						
						processedCardHolderDetails.setExpiryDate(decryptedElement);
					}
					else
					{
						processedCardHolderDetails.setExpiryDate(cardHolderDetails.getExpiryDate());
					}
				}
				else
				{
					getLogger().debug(" could not process CardHolderDetails");
					return processedCardHolderDetails;
				}
			}
			
			
			processedCardHolderDetails.setExpiryDateAuthenticationToBeEnforced(cardHolderDetails.isExpiryDateAuthenticationToBeEnforced());
			getLogger().debug(" processed CardHolderDetails");
			return processedCardHolderDetails;
		}
		catch (Exception e)
		{
			getLogger().error("Error while processing CardHolderDetails" + e.getMessage());
			throw e;
		}
	}
	 
	private boolean authenticateCardHolder (CardHolderDetails cardHolderDetails, Document viewPinRequestDocument, String schemaCVVPath, String rootElement, String expiryDatePath) throws XPathFactoryConfigurationException, XPathExpressionException,
     InvalidExpiryDateException
	{
	 boolean cardHolderAuthentic;
	
	 cardHolderAuthentic = false;
	
	 // Authenticate card holder verification value
	 if (authenticateCardHolderVerificationValue(cardHolderDetails, viewPinRequestDocument, schemaCVVPath) == false)
	 {
	     // Card holder failed verification value authentication
	     getLogger().warn("card holder failed verification value authentication");
	
	     cardHolderAuthentic = false;
	
	     return cardHolderAuthentic;
	 }
	
	 if (cardHolderDetails.isExpiryDateAuthenticationToBeEnforced() == true)
	 {
	     getLogger().debug("expiry date authentication is to be enforced");
	
	     // Authenticate expiry date
	     if (authenticateExpiryDate(cardHolderDetails, viewPinRequestDocument, rootElement, expiryDatePath) == false)
	     {
	         cardHolderAuthentic = false;
	
	         return cardHolderAuthentic;
	     }
	 }
	 else
	 {
	     getLogger().debug("expiry date authentication is not to be enforced");
	 }
	
	 cardHolderAuthentic = true;
	
	 return cardHolderAuthentic;
	}
	
	private boolean authenticateCardHolderVerificationValue (CardHolderDetails cardHolderDetails, Document viewPinRequestDocument, String schemaCVVPath) throws XPathFactoryConfigurationException, XPathExpressionException
	{
	 boolean authenticVerificationValue;
	 XPath xp;
	 String verificationValue;
	
	 authenticVerificationValue = false;
	 xp = null;
	 verificationValue = null;
	
	 // Instantiate XPath object
	 xp = Utils.createXPath();
	
	 // Get verification value from pin retrieval request
	 verificationValue = (String) xp.evaluate(schemaCVVPath, viewPinRequestDocument, XPathConstants.STRING);
	
	 // Does verification value extracted from pin retrieval request match authentic value?
	 if (cardHolderDetails.getCardHolderVerificationValue().equalsIgnoreCase(verificationValue) == true)
	 {
	     // Verification value extracted from pin retrieval request does match authentic value
	     authenticVerificationValue = true;
	 }
	 else
	 {
	     // Verification value extracted from pin retrieval request does not match authentic value
	     getLogger().warn("expecting verification value is not matching with value received");
	 }
	
	 return authenticVerificationValue;
	}
	
	private boolean authenticateExpiryDate (CardHolderDetails cardHolderDetails, Document viewPinRequestDocument, String rootElement, String expiryDatePath) throws XPathFactoryConfigurationException, XPathExpressionException,
	     InvalidExpiryDateException
	{
	 boolean expiryDateAuthentic;
	 XPath xp;
	 Element expiryDateElement;
	 String expiryDateMonth;
	 String expiryDateYear;
	 ExpiryDate expiryDate;
	
	 expiryDateAuthentic = false;
	 xp = null;
	 expiryDateElement = null;
	 expiryDateMonth = null;
	 expiryDateYear = null;
	 expiryDate = null;
	
	 // Instantiate XPath object
	 xp = Utils.createXPath();
	
	 // Get ExpiryDate element
	 expiryDateElement = (Element) xp.evaluate(expiryDatePath, viewPinRequestDocument, XPathConstants.NODE);
	
	 // Did we get ExpiryDate element OK?
	 if (expiryDateElement == null)
	 {
	     // Failed to get ExpiryDate element
	     getLogger().warn("ExpiryDate element expected but not found for account number ");
	
	     expiryDateAuthentic = false;
	
	     return expiryDateAuthentic;
	 }
	
	 // Get expiry date month
	 expiryDateMonth = (String) xp.evaluate("vp:Month", expiryDateElement, XPathConstants.STRING);

	 // Get expiry date year
	 expiryDateYear = (String) xp.evaluate("vp:Year", expiryDateElement, XPathConstants.STRING);
	
	 // Instantiate ExpiryDate
	 expiryDate = new ExpiryDate(expiryDateMonth, expiryDateYear);
	
	 // Is expiry date submitted by card holder in pin retrieval request authentic?
	 if ((expiryDate.getFormatted()).equals(cardHolderDetails.getExpiryDate()) == true)
	 {
	     // Expiry date submitted by card holder in pin retrieval request is authentic
	     expiryDateAuthentic = true;
	 }
	 else
	 {
	     // Expiry date submitted by card holder in pin retrieval request is not authentic
	     getLogger().warn("expiry date submitted by card holder does not match authentic value " );
	
	     expiryDateAuthentic = false;
	 }
	
	 return expiryDateAuthentic;
	}
	
	private String establishSessionEncryptionMethodAlgorithm (Document viewPinRequestDocument, String sessionEncryptionPath) throws XPathFactoryConfigurationException, XPathExpressionException
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
	 verificationValueEncryptedDataElement = (Element) xp.evaluate(sessionEncryptionPath, viewPinRequestDocument,
	         XPathConstants.NODE);
	
	 // Get encryption method algorithm used to encrypt verification value encrypted data element
	 encrytionMethodAlgorithm = (String) xp.evaluate("xenc:EncryptionMethod/@Algorithm", verificationValueEncryptedDataElement, XPathConstants.STRING);
	
	 return encrytionMethodAlgorithm;
	}

	private void initSchemas () throws IOException, SAXException
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
    private synchronized void setRandomNumberGenerator(SecureRandom randomNumberGenerator) 
	{
		this.randomNumberGenerator = randomNumberGenerator;
	}
	
	private SecureRandom getRandomNumberGenerator()
	{
		return this.randomNumberGenerator;
	}
	 
	
	private void setAgentKeyStore (KeyStore agentKeyStore)
    {
        this.agentKeyStore = agentKeyStore;
    }

    private KeyStore getAgentKeyStore ()
    {
        return this.agentKeyStore;
    }

    private void setAuthorityKeyStore (KeyStore authorityKeyStore)
    {
        this.authorityKeyStore = authorityKeyStore;
    }

    private KeyStore getAuthorityKeyStore ()
    {
        return this.authorityKeyStore;
    }

    private void setPinRetrievalResponseGenerator (PinRetrievalResponseGenerator pinRetrievalResponseGenerator)
    {
        this.pinRetrievalResponseGenerator = pinRetrievalResponseGenerator;
    }

    private PinRetrievalResponseGenerator getPinRetrievalResponseGenerator ()
    {
        return this.pinRetrievalResponseGenerator;
    }
   
    private void setMaximumReplayOpportunityWindow (long maximumReplayOpportunityWindow)
    {
        this.maximumReplayOpportunityWindow = maximumReplayOpportunityWindow;
    }

    private long getMaximumReplayOpportunityWindow ()
    {
        return this.maximumReplayOpportunityWindow;
    }
    private boolean checkPINBlockFormat(int PINBlockFormat)
    {
    	boolean checkPINBlockFormat = false;
		
    	
    		if(PINBlockFormat< 0 || PINBlockFormat >3)
    			return checkPINBlockFormat;
    		else
    			checkPINBlockFormat = true;
    	
    	return checkPINBlockFormat;
    	  			
    }
    private boolean checkPANRequired(CardHolderDetails cardHolderDetails, int PINBlockFormat)
    {
    	boolean checkPANRequired = false;
    
    		if((0 == PINBlockFormat) || (3 == PINBlockFormat))
    		{
    			if(cardHolderDetails.getPrimaryAccountNumber() != null)
    			{
    				if(cardHolderDetails.getPrimaryAccountNumber().length() > 0)
    				checkPANRequired = true;
    			} 
    				
    		}
			else 
			    checkPANRequired = true;
    	
    	return checkPANRequired;
    }

	/**
	 * 
	 * @param format holds PIN block format to create
	 * @return
	 */
     private static String createPINChangeResponse(String oldPindecrypted, String newPindecrypted, CardHolderDetails cardHolderDetails) throws Exception, ViewPinRequestException
	 {
		  Document pinChangeResponseDocument;
          XPathFactory xpf;
          XPath xp;
         // SimpleNamespaceContext nsc;
          String oldPIN;
          String newPIN;
          DocumentBuilderFactory dbf;
          DocumentBuilder db;
          Element rootElement;
          Element oldPINElement;
          Element newPINElement;
          String changePINDocument;
		  String oldPINBlock;
		  String newPINBlock;


		  oldPIN = null;
		  newPIN = null;
		  rootElement = null;
		  oldPINElement = null;
		  newPINElement = null;
		  changePINDocument = null;
		  oldPINBlock = null;
		  newPINBlock = null;
		  db= null;

		  dbf = DocumentBuilderFactory.newInstance();
          dbf.setNamespaceAware(true);

          try
          {
              // Instantiate document builder
              db = dbf.newDocumentBuilder();
          }
          catch (Exception e)
          {
             
              System.out.println("instantiating document builder");
          }

          // Create empty pin retrieval response document
          pinChangeResponseDocument = db.newDocument();

           // Create pin retrieval response document root element
		   rootElement = pinChangeResponseDocument.createElement("PINs");
      
		    // Append root element to pin retrieval response document
          pinChangeResponseDocument.appendChild(rootElement);

		     // Create timestamp element
          oldPINElement = pinChangeResponseDocument.createElement("OldPIN");
		
		  try
		  {
			  oldPINBlock = convertToPINBlock(cardHolderDetails, oldPindecrypted);
		  }
		  catch (ViewPinRequestException e)
		  {
			   getLogger().error("Could not generate old PIN Block");
			   throw new ViewPinRequestException("Could not generate old PIN Block");
			
		  }
		
          // Append timestamp value to timestamp element
          oldPINElement.appendChild(pinChangeResponseDocument.createTextNode(oldPINBlock));

		 // Append timestamp element to root element of pin retrieval response document
          rootElement.appendChild(oldPINElement);


		     // Create timestamp element
          newPINElement = pinChangeResponseDocument.createElement("NewPIN");

		  try
		  {
			  newPINBlock = convertToPINBlock(cardHolderDetails, newPindecrypted);
		  }
		  catch (ViewPinRequestException e)
		  {
			   getLogger().error("Could not generate new PIN Block");
			   throw new ViewPinRequestException("Could not generate new PIN Block");
			
		  }
		
          // Append timestamp value to timestamp element
          newPINElement.appendChild(pinChangeResponseDocument.createTextNode(newPINBlock));


		// Append timestamp element to root element of pin retrieval response document
          rootElement.appendChild(newPINElement);

		  
          
		 //Serialize DOM
		  OutputFormat format    = new OutputFormat (pinChangeResponseDocument); 
		
		  // as a String
		  StringWriter stringOut = new StringWriter();    
		  XMLSerializer serial   = new XMLSerializer (stringOut,format);

		  try
		  {
			 serial.serialize(pinChangeResponseDocument);
			 //convert to string and return
			 return stringOut.toString();
		  }
		  catch (Exception e)
		  {
			  getLogger().error("could not generate PinChangeDocument");
			  throw e;
		  }
     
	 }
      

	 private static String convertToPINBlock(CardHolderDetails cardHolderDetails, String Pin) throws ViewPinRequestException
	 {
		byte[] PINBlock;
		PINBlock PinBlock = null;
		 	//create the PINBlock object
		if(( 1 == cardHolderDetails.getOutputPINBlockFormat() ) || ( 2 == cardHolderDetails.getOutputPINBlockFormat() ) )
		{
			try
			{
				PinBlock = new PINBlock(new Pin(Pin));
			}
			catch(InvalidPinException e)
			{
				 getLogger().error("Could not generate PIN Block: invalid Pin  " + e.getMessage());
					
				 throw new ViewPinRequestException("Could not generate PIN Block: invalid Pin");
			}
		}
		else
		{
			try
			{
				PinBlock = new PINBlock(new Pin(Pin), new PrimaryAccountNumber(cardHolderDetails.getPrimaryAccountNumber()));
			}
			catch(InvalidPinException e)
			{
				 getLogger().error("Could not generate PIN Block: invalid Pin  " + e.getMessage());
					
				 throw new ViewPinRequestException("Could not generate PIN Block: invalid Pin");
			}
			catch(InvalidPrimaryAccountNumberException e)
			{
				 getLogger().error("Could not generate PIN Block: invalid PAN  " + e.getMessage());
					
				 throw new ViewPinRequestException("Could not generate PIN Block: invalid PAN");
			}
		}
		//convert it into the PBFO
		try
		{
			PINBlock = PinBlock.getPINBlock(cardHolderDetails.getOutputPINBlockFormat());
		}
		catch(InvalidPINBlockFormatException e)
		{
			 getLogger().error("Could not generate PIN Block: invalid PINBlockFormat  " + e.getMessage());
				
			 throw new ViewPinRequestException("Could not generate PIN Block: invalid PINBlockFormat");
		}
		
		return new String(Hex.encode(PINBlock));
	 }

}
