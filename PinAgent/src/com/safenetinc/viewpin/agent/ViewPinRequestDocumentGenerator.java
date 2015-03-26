// $Id: PinAgent/src/com/safenetinc/viewpin/agent/ViewPinRequestDocumentGenerator.java 1.4 2012/01/24 15:08:28IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.ds.CanonicalizationMethod;
import com.safenetinc.ds.DigestMethod;
import com.safenetinc.ds.DigestValue;
import com.safenetinc.ds.KeyInfo;
import com.safenetinc.ds.KeyValue;
import com.safenetinc.ds.Reference;
import com.safenetinc.ds.RsaKeyValue;
import com.safenetinc.ds.SignatureConstants;
import com.safenetinc.ds.SignatureMethod;
import com.safenetinc.ds.SignatureUtils;
import com.safenetinc.ds.SignatureValue;
import com.safenetinc.ds.SignedInfo;
import com.safenetinc.ds.Transform;
import com.safenetinc.ds.Transforms;
import com.safenetinc.ds.X509Data;
import com.safenetinc.ds.X509SKI;
import com.safenetinc.ds.XmlSignature;
import com.safenetinc.viewpin.agent.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.agent.sessionkey.SessionCipherProperties;
import com.safenetinc.viewpin.agent.sessionkey.Signer;
import com.safenetinc.viewpin.agent.sessionkey.WrappedSessionKey;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedPaddingSchemeException;
import com.safenetinc.viewpin.common.datastructures.CardHolderVerification;
import com.safenetinc.viewpin.common.datastructures.PinChangeData;
import com.safenetinc.viewpin.common.datastructures.CardPin;
import com.safenetinc.viewpin.common.utils.Utils;
import com.safenetinc.viewpin.common.xencsigmap.CipherFactory;
import com.safenetinc.viewpin.common.xencsigmap.MessageDigestFactory;
import com.safenetinc.viewpin.common.xencsigmap.SignatureFactory;
import com.safenetinc.viewpin.common.xencsigmap.XmlEncryptionAlgorithmMapper;
import com.safenetinc.viewpin.common.xml.CachedDocumentBuilder;


/**
 * Class to handle the generation of the PINRetrievalRequest documents
 * 
 * @author Manmeet Singh
 *
 */
public class ViewPinRequestDocumentGenerator 
{
	private static Logger logger = Logger.getLogger(ViewPinRequestDocumentGenerator.class);
	
	private ViewPinRequestDocumentGenerator()
    {
    	super();
    }
    
    /**
     * Generate a PINRetrievalRequest
     * @param sessionKey The secret key to use in encrypting the document
     * @param sessionCipherProperties The session cipher properties to use during encryption
     * @param cardHolderVerification The CVV value to embed into the document
     * @param wrappedSessionKey The secret key in wrapped form
     * @param signer The {@link Signer} to use when signing the document
     * @param digestMethodAlgorithm The digest type to use in the signature
     * @return The PINRetrievalRequest document
     * @throws ViewPinRequestException Throw if an error occurs during generation of the document
     */
    public static Document generatePinRetrievalRequestDocument(SecretKey sessionKey,
        SessionCipherProperties sessionCipherProperties, CardHolderVerification cardHolderVerification, 
        WrappedSessionKey wrappedSessionKey, Signer signer, String digestMethodAlgorithm) throws ViewPinRequestException
    {
    	Document pinRetrievalRequestDocument;
        Element rootElement;
    	Element timestampElement;
        Element transactionIdentifierElement;
        String transactionIdentifier;
    	Element cardHolderVerificationElement;
    	Element verificationValueElement;
    	Element verificationValueEncryptedDataElement;
    	Element expiryDateElement;
    	Element monthElement;
    	Element monthEncryptedDataElement;
    	Element yearElement;
    	Element yearEncryptedDataElement;
    	Element primaryAccountNumberElement;
    	Element primaryAccountNumberEncryptedDataElement;
    	Element authorityWrappedSessionKeyEncryptedKeyElement;
    	
    	pinRetrievalRequestDocument = null;
        rootElement = null;
        timestampElement = null;
        transactionIdentifierElement = null;
        transactionIdentifier = null;
        cardHolderVerificationElement = null;
        verificationValueElement = null;
        verificationValueEncryptedDataElement = null;
        expiryDateElement = null;
    	monthElement = null;
    	monthEncryptedDataElement = null;
    	yearElement = null;
    	yearEncryptedDataElement = null;
    	primaryAccountNumberElement = null;
    	primaryAccountNumberEncryptedDataElement = null;
    	authorityWrappedSessionKeyEncryptedKeyElement = null;

		    	
    	try
    	{
            // Create empty pin retrieval request document
            pinRetrievalRequestDocument = CachedDocumentBuilder.getCachedDocumentBuilder(null).newDocument();
    	}
        catch(ParserConfigurationException pce)
        {
            getLogger().fatal("retrieving cached document builder");
            
            throw new ViewPinRequestException();
        }
      
        // Create pin retrieval request document root element
        rootElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:PinRetrievalRequest");
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:vp", ViewPinConstants.VIEWPIN_NAMESPACE_URI);
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:xenc", ViewPinConstants.XENC_NAMESPACE);
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:ds", ViewPinConstants.DSIG_NAMESPACE);

        // Append root element to pin retrieval request document
        pinRetrievalRequestDocument.appendChild(rootElement);

        // Create timestamp element
        timestampElement= pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:timestamp");

        // Append timestamp value to timestamp element
        timestampElement.appendChild(pinRetrievalRequestDocument.createTextNode(Utils.formatDate(new Date())));

        // Append timestamp element to root element of pin retrieval request document
        rootElement.appendChild(timestampElement);
                
        // Create transaction identifier element
        transactionIdentifierElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:TransactionIdentifier");
        
        // Append transaction identifier element to root element of pin retrieval request document
        rootElement.appendChild(transactionIdentifierElement);
        
        // Retrieve transaction identifier
        transactionIdentifier = (String)MDC.get("transactionIdentifier");
        
        // Append transaction identifier value to transaction identifier element
        transactionIdentifierElement.appendChild(pinRetrievalRequestDocument.createTextNode(transactionIdentifier));
        
        // Create card holder verification element
        cardHolderVerificationElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
            "vp:CardHolderVerification");

        // Append card holder verification element to root element of pin retrieval request
        rootElement.appendChild(cardHolderVerificationElement);

        // Create verification value element
        verificationValueElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
            "vp:VerificationValue");

        // Append verification value element to card holder verification element
        cardHolderVerificationElement.appendChild(verificationValueElement);
        
        // Generate card holder verification value EncryptedData element
        verificationValueEncryptedDataElement = generateEncryptedDataElement(pinRetrievalRequestDocument,
        	cardHolderVerification.getCardHolderVerificationValue().getCardHolderVerificationValue().getBytes(),
    	    sessionKey, sessionCipherProperties);
        
        // Append verification value EncryptedData element to verification value element
        verificationValueElement.appendChild(verificationValueEncryptedDataElement);
    
        // Has expiry date been passed in?
        if(cardHolderVerification.getExpiryDate() != null)
        {
        	// Create expiry date element
        	expiryDateElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:ExpiryDate");
            
        	// Append expiry date element to card holder verification element
        	cardHolderVerificationElement.appendChild(expiryDateElement);
        	
            // Create month element
            monthElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:Month");
            
            // Append month element to expiry date element
            expiryDateElement.appendChild(monthElement);
            
            // Generate month EncryptedData element
           
            monthEncryptedDataElement = generateEncryptedDataElement(pinRetrievalRequestDocument,
    	        String.valueOf(cardHolderVerification.getExpiryDate().getMonth()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
           
            // Append month encrypted data element to month element
            monthElement.appendChild(monthEncryptedDataElement);
            
            // Create year element
            yearElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:Year");
            
            // Append year element to expiry date element
            expiryDateElement.appendChild(yearElement);
        
            // Generate year EncryptedData element
            yearEncryptedDataElement = generateEncryptedDataElement(pinRetrievalRequestDocument,
    	        String.valueOf(cardHolderVerification.getExpiryDate().getYear()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
            
            // Append year encrypted data element to year element
            yearElement.appendChild(yearEncryptedDataElement);
        }
        
        // Has primary account number been passed in?
        if(cardHolderVerification.getPrimaryAccountNumber() != null)
        {
            // Create primary account number element
        	primaryAccountNumberElement = pinRetrievalRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:PrimaryAccountNumber");
            
        	// Append primary account number element to card holder verification element
        	cardHolderVerificationElement.appendChild(primaryAccountNumberElement);
     
            // Generate primary account number EncryptedData element
            primaryAccountNumberEncryptedDataElement = generateEncryptedDataElement(pinRetrievalRequestDocument,
    	        String.valueOf(cardHolderVerification.getPrimaryAccountNumber()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
     
            // Append primary account number encrypted data element to primary account number element
            primaryAccountNumberElement.appendChild(primaryAccountNumberEncryptedDataElement);
        }
        
        // Create authority wrapped session key EncryptedKey element
        authorityWrappedSessionKeyEncryptedKeyElement = generateEncryptedKeyElement(pinRetrievalRequestDocument, wrappedSessionKey);
     
		// Append authority wrapped session key EncryptedKey element to root element of pin retrieval request
        pinRetrievalRequestDocument.getDocumentElement().appendChild(authorityWrappedSessionKeyEncryptedKeyElement);
	    
        // Sign pin retrieval request
		sign(pinRetrievalRequestDocument, signer, digestMethodAlgorithm);
		
    	return pinRetrievalRequestDocument;
    }
 
    /**
     * Generate a PINChangeRequest
     * @param sessionKey The secret key to use in encrypting the document
     * @param sessionCipherProperties The session cipher properties to use during encryption
     * @param pinChangeData The Pin Change Data value to embed into the document
     * @param wrappedSessionKey The secret key in wrapped form
     * @param signer The {@link Signer} to use when signing the document
     * @param digestMethodAlgorithm The digest type to use in the signature
     * @return The PINChangeRequest document
     * @throws ViewPinRequestException Throw if an error occurs during generation of the document
     */
    public static Document generatePinChangeRequestDocument(SecretKey sessionKey,
        SessionCipherProperties sessionCipherProperties, PinChangeData pinChangeData, 
        WrappedSessionKey wrappedSessionKey, Signer signer, String digestMethodAlgorithm) throws ViewPinRequestException
    {
    	Document pinChangeRequestDocument;
        Element rootElement;
    	Element timestampElement;
        Element transactionIdentifierElement;
        String transactionIdentifier;
    	Element pinChangeDataElement;
    	Element verificationValueElement;
    	Element verificationValueEncryptedDataElement;
    	Element expiryDateElement;
    	Element monthElement;
    	Element monthEncryptedDataElement;
    	Element yearElement;
    	Element yearEncryptedDataElement;
    	Element primaryAccountNumberElement;
    	Element primaryAccountNumberEncryptedDataElement;
    	Element oldPinElement;
    	Element newPinElement;
    	Element authorityWrappedSessionKeyEncryptedKeyElement;
    	
    	pinChangeRequestDocument = null;
        rootElement = null;
        timestampElement = null;
        transactionIdentifierElement = null;
        transactionIdentifier = null;
        pinChangeDataElement = null;
        verificationValueElement = null;
        verificationValueEncryptedDataElement = null;
        expiryDateElement = null;
    	monthElement = null;
    	monthEncryptedDataElement = null;
    	yearElement = null;
    	yearEncryptedDataElement = null;
    	primaryAccountNumberElement = null;
    	primaryAccountNumberEncryptedDataElement = null;
    	oldPinElement=null;
    	newPinElement=null;
    	authorityWrappedSessionKeyEncryptedKeyElement = null;
    	
    	try
    	{
            // Create empty pin change request document
    		pinChangeRequestDocument = CachedDocumentBuilder.getCachedDocumentBuilder(null).newDocument();
    	}
        catch(ParserConfigurationException pce)
        {
            getLogger().fatal("retrieving cached document builder");
            
            throw new ViewPinRequestException();
        }
        
        // Create pin change request document root element
        rootElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:PinChangeRequest");
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:vp", ViewPinConstants.VIEWPIN_NAMESPACE_URI);
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:xenc", ViewPinConstants.XENC_NAMESPACE);
        rootElement.setAttributeNS(ViewPinConstants.NAMESPACE_NAMESPACE, "xmlns:ds", ViewPinConstants.DSIG_NAMESPACE);

        // Append root element to pin change request document
        pinChangeRequestDocument.appendChild(rootElement);

        // Create timestamp element
        timestampElement= pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:timestamp");

        // Append timestamp value to timestamp element
        timestampElement.appendChild(pinChangeRequestDocument.createTextNode(Utils.formatDate(new Date())));

        // Append timestamp element to root element of pin change request document
        rootElement.appendChild(timestampElement);
                
        // Create transaction identifier element
        transactionIdentifierElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI, "vp:TransactionIdentifier");
        
        // Append transaction identifier element to root element of pin change request document
        rootElement.appendChild(transactionIdentifierElement);
        
        // Retrieve transaction identifier
        transactionIdentifier = (String)MDC.get("transactionIdentifier");
        
        // Append transaction identifier value to transaction identifier element
        transactionIdentifierElement.appendChild(pinChangeRequestDocument.createTextNode(transactionIdentifier));
        
        // Create card holder verification element
        pinChangeDataElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
            "vp:PinChangeData");

        // Append card holder verification element to root element of pin change request
        rootElement.appendChild(pinChangeDataElement);

        // Create verification value element
        verificationValueElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
            "vp:VerificationValue");

        // Append verification value element to card holder verification element
        pinChangeDataElement.appendChild(verificationValueElement);

        // Generate card holder verification value EncryptedData element
        verificationValueEncryptedDataElement = generateEncryptedDataElement(pinChangeRequestDocument,
        	pinChangeData.getCardHolderVerificationValue().getCardHolderVerificationValue().getBytes(),
    	    sessionKey, sessionCipherProperties);
    
        // Append verification value EncryptedData element to verification value element
        verificationValueElement.appendChild(verificationValueEncryptedDataElement);
    
        // Has expiry date been passed in?
        if(pinChangeData.getExpiryDate() != null)
        {
        	// Create expiry date element
        	expiryDateElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:ExpiryDate");
            
        	// Append expiry date element to card holder verification element
        	pinChangeDataElement.appendChild(expiryDateElement);
        	
            // Create month element
            monthElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:Month");
            
            // Append month element to expiry date element
            expiryDateElement.appendChild(monthElement);
            
            // Generate month EncryptedData element
            monthEncryptedDataElement = generateEncryptedDataElement(pinChangeRequestDocument,
    	        String.valueOf(pinChangeData.getExpiryDate().getMonth()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
            
            // Append month encrypted data element to month element
            monthElement.appendChild(monthEncryptedDataElement);
            
            // Create year element
            yearElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:Year");
            
            // Append year element to expiry date element
            expiryDateElement.appendChild(yearElement);
        
            // Generate year EncryptedData element
            yearEncryptedDataElement = generateEncryptedDataElement(pinChangeRequestDocument,
    	        String.valueOf(pinChangeData.getExpiryDate().getYear()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
            
            // Append year encrypted data element to year element
            yearElement.appendChild(yearEncryptedDataElement);
        }
        
        // Has primary account number been passed in?
        if(pinChangeData.getPrimaryAccountNumber() != null)
        {
            // Create primary account number element
        	primaryAccountNumberElement = pinChangeRequestDocument.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
                "vp:PrimaryAccountNumber");
            
        	// Append primary account number element to card holder verification element
        	pinChangeDataElement.appendChild(primaryAccountNumberElement);
        
            // Generate primary account number EncryptedData element
            primaryAccountNumberEncryptedDataElement = generateEncryptedDataElement(pinChangeRequestDocument,
    	        String.valueOf(pinChangeData.getPrimaryAccountNumber()).getBytes(),
    	    	sessionKey, sessionCipherProperties);
        
            // Append primary account number encrypted data element to primary account number element
            primaryAccountNumberElement.appendChild(primaryAccountNumberEncryptedDataElement);
        }
        
     // Has oldPin(CardPin) been passed in?
        if(pinChangeData.getOldPin() != null)
        {
            // Create Old Pin element
        	oldPinElement = generateEncryptedCardPinElement(pinChangeRequestDocument,pinChangeData.getOldPin(),new String("OldPin"),     
        	    											sessionKey, sessionCipherProperties);
        	
        	pinChangeDataElement.appendChild(oldPinElement);
        	
        }
        
        // Has newPin(CardPin) been passed in?
        if(pinChangeData.getNewPin() != null)
        {
        	newPinElement= generateEncryptedCardPinElement(pinChangeRequestDocument,pinChangeData.getNewPin(), new String("NewPin"),   
					sessionKey, sessionCipherProperties);
        	
        	pinChangeDataElement.appendChild(newPinElement);
        }
        
        // Create authority wrapped session key EncryptedKey element
        authorityWrappedSessionKeyEncryptedKeyElement = generateEncryptedKeyElement( pinChangeRequestDocument, wrappedSessionKey);
    	
		// Append authority wrapped session key EncryptedKey element to root element of pin change request
        pinChangeRequestDocument.getDocumentElement().appendChild(authorityWrappedSessionKeyEncryptedKeyElement);
	    
        // Sign pin change request
		sign(pinChangeRequestDocument, signer, digestMethodAlgorithm);
		
    	return pinChangeRequestDocument;
    }
    
    private static Element generateEncryptedDataElement(Document document,
        byte[] plainText, SecretKey sessionKey, SessionCipherProperties sessionCipherProperties) throws ViewPinRequestException
    {
    	Element encryptedDataElement;
    	Element encryptionMethodElement;
    	String encryptionMethod;
    	Element keyInfoElement;
    	Element retrievalMethodElement;
    	Element cipherDataElement;
    	Element cipherValueElement;
    	IvParameterSpec ivps;
        Cipher sessionCipher;
        byte[] cipherText;
        byte[] ivPlusCipherText;
        String encodedIvPlusCipherText;

        encryptedDataElement = null;
        encryptionMethodElement = null;
        encryptionMethod = null;
        keyInfoElement = null;
        retrievalMethodElement = null;
        cipherDataElement = null;
        cipherValueElement = null;
        ivps = null;
        sessionCipher = null;
        cipherText = null;
        ivPlusCipherText = null;
        encodedIvPlusCipherText = null;

        // Create EncryptedData element
        encryptedDataElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:EncryptedData");

        // Set EncryptedData element type attribute
        encryptedDataElement.setAttributeNS(null, "Type", "http://www.w3.org/2001/04/xmlenc#Content");

        // Create EncryptionMethod element
        encryptionMethodElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:EncryptionMethod");

        // Append EncryptionMethod element to EncryptedData element
        encryptedDataElement.appendChild(encryptionMethodElement);
        
        try 
        {
            // Map encryption method
            encryptionMethod = XmlEncryptionAlgorithmMapper.map(sessionCipherProperties);
            
            getLogger().debug("encryption method = " + encryptionMethod);
		}
        catch(Exception e) 
        {
			getLogger().error("mapping encryption method " + e.getMessage());
			
			throw new ViewPinRequestException();
		}

        // Set EncryptionMethod elements algorithm attribute
        encryptionMethodElement.setAttributeNS(null, "Algorithm", encryptionMethod);

        // Create KeyInfo element
        keyInfoElement = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE, "ds:KeyInfo");

        // Append KeyInfo element to EncryptedKey element
        encryptedDataElement.appendChild(keyInfoElement);

        // Create RetrievalMethod element
        retrievalMethodElement = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE, "ds:RetrievalMethod");

        // Append RetrievalMethod element to KeyInfo element
        keyInfoElement.appendChild(retrievalMethodElement);

        // Set RetrievalMethod Type attribute
        retrievalMethodElement.setAttributeNS(null, "Type", "http://www.w3.org/2001/04/xmlenc#EncryptedKey");

        // Set RetrievalMethod Id attribute
        retrievalMethodElement.setAttributeNS(null, "URI", "#EK");

        // Create CipherData element
        cipherDataElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:CipherData");

        // Append CipherData to EncrytedData element
        encryptedDataElement.appendChild(cipherDataElement);

        // Create CipherValue element
        cipherValueElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:CipherValue");

        // Append CipherValue element to CipherData element
        cipherDataElement.appendChild(cipherValueElement);
        
        try
        {
            // Generate initialisation vector
            ivps = Utils.generateInitialisationVector(sessionCipherProperties.getBlockSize());
        } 
        catch(NoSuchAlgorithmException nsae)
        {
            getLogger().fatal("generating initialisation vector " + nsae.getMessage());
            
            throw new ViewPinRequestException();
        }

        if(getLogger().isDebugEnabled() == true)
        {
            getLogger().debug("initialisation vector = " + new String(Hex.encodeHex(ivps.getIV())));
        }

        try
        {
	        // Initialise session cipher 
	        sessionCipher = CipherFactory.getInstance(encryptionMethod);
            
	        getLogger().debug("session cipher transformation = " + sessionCipher.getAlgorithm());
	        getLogger().debug("cipher provider = " + sessionCipher.getProvider().getName());
	        
	        // Initialise session cipher
	        sessionCipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivps);

	        getLogger().debug("encrypting plain text");
	        
	        // Encrypt plain text
	        cipherText = sessionCipher.doFinal(plainText);
        }
        catch(Exception e)
        {
            getLogger().error("encrypting plain text " + e.getMessage());
			
			throw new ViewPinRequestException();
        }
        
        getLogger().debug("encrypted plain text");

        // Place IV in front of cipher text
        ivPlusCipherText = new byte[ivps.getIV().length + cipherText.length];

        System.arraycopy(ivps.getIV(), 0, ivPlusCipherText, 0, ivps.getIV().length);

        System.arraycopy(cipherText, 0, ivPlusCipherText, ivps.getIV().length, cipherText.length);

        // Encode IV plus cipher text
        encodedIvPlusCipherText = new String(Base64.encodeBase64(ivPlusCipherText));

        // Place encoded iv plus cipher text into CipherValue element
        cipherValueElement.appendChild(document.createTextNode(encodedIvPlusCipherText));

        return encryptedDataElement;
    }

    private static Element generateEncryptedCardPinElement(Document document,CardPin cardPin,String elementName,
    		SecretKey sessionKey, SessionCipherProperties sessionCipherProperties) throws ViewPinRequestException
    {
    	Element cardPinElement;
    	Element pinNumberElement;
    	Element pinNumberEncryptedElement;
    	Element panNumberElement;
    	Element panNumberEncryptedElement;
    	
    	cardPinElement = null;
    	pinNumberElement = null;
    	pinNumberEncryptedElement = null;
    	panNumberElement = null;
    	panNumberEncryptedElement = null;
    	
    	try
    	{
	    	// Create Card Pin element
	    	cardPinElement = document.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
	            "vp:"+elementName);
	        
	    	//Create Pin Element
	    	pinNumberElement = document.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
	        "vp:Pin");
	    	
	    	//Append Pin element to Old Pin Element
	    	cardPinElement.appendChild(pinNumberElement);
	    	
	        // Generate Pin EncryptedData element
	        pinNumberEncryptedElement = generateEncryptedDataElement(document,
		        String.valueOf(cardPin.getPin()).getBytes(),
		    	sessionKey, sessionCipherProperties);
	    
	        // Append pin encrypted data element to Old Pin element
	        pinNumberElement.appendChild(pinNumberEncryptedElement);

			//commenting the addition of PAN number along with the Key
	        /*
	       //Create Primary Account Number Element
	    	panNumberElement = document.createElementNS(ViewPinConstants.VIEWPIN_NAMESPACE_URI,
	        "vp:PrimaryAccountNumber");
	    	
	    	// Append Primary Account Number to Old Pin element
	    	cardPinElement.appendChild(panNumberElement);
	    	*/
	        // Generate Pin EncryptedData element
			/*
	        panNumberEncryptedElement = generateEncryptedDataElement(document,
		        String.valueOf(cardPin.getPrimaryAccountNumber()).getBytes(),
		    	sessionKey, sessionCipherProperties);
	    
	        // Append pin encrypted data element to Old Pin element
	        panNumberElement.appendChild(panNumberEncryptedElement);*/
    	}
    	catch (Exception e)
    	{
    		getLogger().fatal("generating Card Pin Element " + e.getMessage());            
            throw new ViewPinRequestException();	    	
    	}
    	
        return cardPinElement;
    
    }

    private static Element generateEncryptedKeyElement(Document document,
        WrappedSessionKey wrappedSessionKey) throws ViewPinRequestException
    {
    	Element encryptedKeyElement;
    	Element encryptionMethodElement;
    	String encryptionMethod;
    	Element keyInfoElement;
    	Element x509DataElement;
    	Element x509SkiElement;
    	Element cipherDataElement;
    	Element cipherValueElement;

    	encryptedKeyElement = null;
    	encryptionMethodElement = null;
    	keyInfoElement = null;
    	x509DataElement = null;
    	x509SkiElement = null;
    	cipherDataElement = null;
    	cipherValueElement = null;

        // Create EncryptedKey element
        encryptedKeyElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:EncryptedKey");

        // Set Id attribute
        encryptedKeyElement.setAttributeNS(null, "Id", "EK");

        // Create EncryptionMethod element
        encryptionMethodElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:EncryptionMethod");

        // Append EncryptionMethod element to EncryptedKey element
        encryptedKeyElement.appendChild(encryptionMethodElement);
        
        try 
        {
            // Map encryption method
			encryptionMethod = XmlEncryptionAlgorithmMapper.map(wrappedSessionKey.getPaddingScheme());
		
            getLogger().debug("encryption method = " + encryptionMethod);
        } 
        catch(UnsupportedPaddingSchemeException uspse) 
        {
        	getLogger().error("mapping encryption method " + uspse.getMessage());
        	
        	throw new ViewPinRequestException();
        }

        // Set EncryptionMethod elements algorithm attribute
        encryptionMethodElement.setAttributeNS(null, "Algorithm", encryptionMethod);

        // Create KeyInfo element
        keyInfoElement = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE, "ds:KeyInfo");

        // Append KeyInfo element to EncryptedKey element
        encryptedKeyElement.appendChild(keyInfoElement);

        // Create X509Data element
        x509DataElement = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE, "ds:X509Data");

        // Append X509Data element to KeyInfo element
        keyInfoElement.appendChild(x509DataElement);

        // Create X509SKI element to hold subject key identifier of wrapping certificate
        x509SkiElement = document.createElementNS(ViewPinConstants.DSIG_NAMESPACE, "ds:X509SKI");

        // Append X509SKI element to X509Data element
        x509DataElement.appendChild(x509SkiElement);

        // Create CipherData element
        cipherDataElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:CipherData");

        // Append CipherData to EncrytedKey element
        encryptedKeyElement.appendChild(cipherDataElement);

        // Create CipherValue element
        cipherValueElement = document.createElementNS(ViewPinConstants.XENC_NAMESPACE, "xenc:CipherValue");

        // Append CipherValue element to CipherData element
        cipherDataElement.appendChild(cipherValueElement);

        // Append encoded wrapping key certificate subject key identifier to X509SKI element
        x509SkiElement.appendChild(document.createTextNode(wrappedSessionKey.getSubjectKeyIdentifier().getBase64Encoded()));

        // Append encoded wrapped session key to CipherValue element
        cipherValueElement.appendChild(document.createTextNode(wrappedSessionKey.getEncodedWrappedSessionKey()));

    	return encryptedKeyElement;
    }
    
    private static void sign(Document document, Signer signer, String digestMethodAlgorithm) throws ViewPinRequestException
	{
		Transforms transforms;
		Transform t1;
		Transform t2;
		Reference r;
		byte[] canonicalizedDocument;
		MessageDigest md;
		byte[] canonicalizedDocumentMessageDigest;
		String encodedCanonicalizedDocumentMessageDigest;
		String signatureMethodAlgorithm;
		SignedInfo si;
		XmlSignature xs;
		Element signatureElement;
		Element signedInfoElement;
		byte[] canonicalizedSignedInfoElement;
		byte[] signedCanonicalizedSignedInfo;
		Signature signatureEngine;
		SignatureValue signatureValue;

		transforms = null;
		t1 = null;
		t2 = null;
		r = null;
		canonicalizedDocument = null;
		md = null;
		canonicalizedDocumentMessageDigest = null;
		encodedCanonicalizedDocumentMessageDigest = null;
		signatureMethodAlgorithm = null;
		si = null;
		xs = null;
		signatureElement = null;
		signedInfoElement = null;
		canonicalizedSignedInfoElement = null;
		signedCanonicalizedSignedInfo = null;
		signatureEngine = null;
		signatureValue = null;

        // Create transforms
        transforms = new Transforms();
        t1 = new Transform(Transform.ENVELOPED_SIGNATURE);
        t2 = new Transform(Transform.EXCLUSIVE_WITHOUT_COMMENTS);
        transforms.addTransform(t1);
        transforms.addTransform(t2);
        
        // Create reference
        r = new Reference(new DigestMethod(digestMethodAlgorithm));
        r.setTransforms(transforms);
        
        try
        {
        	// Canonicalize document
        	canonicalizedDocument = SignatureUtils.exclusiveCanonicalizeWithoutComments(document.getDocumentElement());
        }
        catch(IOException ioe)
        {
        	getLogger().error("canonicalizing document " + ioe.getMessage());
        	
        	throw new ViewPinRequestException();
        }

        try
        {
        	// Get message digest engine instance
            md = MessageDigestFactory.getInstance(digestMethodAlgorithm);
        }
        catch(NoSuchAlgorithmException nsae)
        {
        	getLogger().error("instantiating message digest engine " + nsae.getMessage());
        	
        	throw new ViewPinRequestException();
        }
        
        getLogger().debug("message digest engine algorithm = " + md.getAlgorithm());
        getLogger().debug("message digest engine provider = " + md.getProvider());

        // Produce message digest from canonicalized document
        canonicalizedDocumentMessageDigest = md.digest(canonicalizedDocument);

        // Encode canonicalized document message digest
        encodedCanonicalizedDocumentMessageDigest = new String(Base64.encodeBase64(canonicalizedDocumentMessageDigest, false));

        // Add message digest value of canonicalized document to reference 
        r.setDigestValue(new DigestValue(encodedCanonicalizedDocumentMessageDigest));

        // Get signature method algorithm
        signatureMethodAlgorithm = signer.getSignatureMethodAlgorithm();
        
        // Instantiate SignedInfo element
        si = new SignedInfo(new CanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE_WITHOUT_COMMENTS),
            new SignatureMethod(signatureMethodAlgorithm));

        // Add reference to SignedInfo element
        si.addReference(r);

        // Instantiate XML signature object
        xs = new XmlSignature(si);

        // Convert XML signature object to element
        signatureElement = xs.toDom(document);

        // Get SignedInfo element
        signedInfoElement = (Element)signatureElement.getElementsByTagNameNS(SignatureConstants.DS_NS, "SignedInfo").item(0);

        try
        {
        	// Canonicalize SignedInfo element
        	canonicalizedSignedInfoElement = SignatureUtils.exclusiveCanonicalizeWithoutComments(signedInfoElement);
        }
        catch(Exception e)
        {
        	getLogger().error("canonicalizing SignedInfo element " + e.getMessage());
        
        	throw new ViewPinRequestException();
        }

        try
        {
            // Sign SignedInfo element
            signatureEngine = SignatureFactory.getInstance(signatureMethodAlgorithm);
        	signatureEngine.initSign(signer.getSigningKey());
        	signatureEngine.update(canonicalizedSignedInfoElement);
	        signedCanonicalizedSignedInfo = signatureEngine.sign();
        }
    	catch(Exception e)
        {
    		getLogger().error("signing SignedInfo element " + e.getMessage());
    		
    		throw new ViewPinRequestException();
        }

        // Encode SignatureValue
        signatureValue = new SignatureValue(new String(Base64.encodeBase64(signedCanonicalizedSignedInfo, false)));

        // Add SignatureValue to XMLSignature object
        xs.setSignatureValue(signatureValue);
        
        // Instantiate KeyInfo
		KeyInfo ki = new KeyInfo();
		xs.addKeyInfo(ki);
		
		// Add signing certificate subject key identifier
		X509Data x509Data = new X509Data();
		X509SKI x509Ski= new X509SKI(signer.getSubjectKeyIdentifier().getUnencoded());
		x509Data.add(x509Ski);
		ki.add(x509Data);
		
        if(getLogger().isDebugEnabled() == true)
        {
        	if(signer.getSigningCertificate().getPublicKey().getAlgorithm().compareToIgnoreCase("RSA") == 0)
        	{
                KeyValue kv = new RsaKeyValue((RSAPublicKey)signer.getSigningCertificate().getPublicKey());
    	        ki.addKeyValue(kv);
        	}
	    }
        
        // Add XML signature element to document
        document.getDocumentElement().appendChild(xs.toDom(document));
	}
    
    private static Logger getLogger()
    {
    	return ViewPinRequestDocumentGenerator.logger;
    }
}