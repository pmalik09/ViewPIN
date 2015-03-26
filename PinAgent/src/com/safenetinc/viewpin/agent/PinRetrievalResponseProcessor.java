// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinRetrievalResponseProcessor.java 1.9 2013/09/25 09:44:57IST Malik, Pratibha (Pmalik) Exp  $

package com.safenetinc.viewpin.agent;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;




import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.safenetinc.luna.provider.key.LunaKey;
import com.safenetinc.viewpin.agent.exceptions.PinRetrievalResponseException;
import com.safenetinc.viewpin.agent.sessionkey.SessionCipherProperties;
import com.safenetinc.viewpin.agent.sessionkey.SessionKeyWrapper;
import com.safenetinc.viewpin.agent.sessionkey.Wrapper;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.InvalidSessionKeyLengthException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedKeyTypeException;
import com.safenetinc.viewpin.common.datastructures.CardPin;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidCardPinException;
import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidSubjectKeyIdentifierException;
import com.safenetinc.viewpin.common.utils.UrlSafeBase64;
import com.safenetinc.viewpin.common.utils.Utils;
import com.safenetinc.viewpin.common.utils.XMLUtils;
import com.safenetinc.viewpin.common.xencsigmap.CipherFactory;
import com.safenetinc.viewpin.common.xencsigmap.MessageDigestFactory;
import com.safenetinc.viewpin.common.xencsigmap.SignatureFactory;
import com.safenetinc.viewpin.common.xencsigmap.XmlEncryptionAlgorithmMapper;
import com.safenetinc.viewpin.common.xml.CachedDocumentBuilder;
import com.safenetinc.viewpin.common.xml.ValidationResult;

/**
 * Class to handle parsing and validation of the PINRetrievalResponse document that is returned from the
 * PINAuthority
 * 
 * @author Stuart Horler
 *  
 */
public class PinRetrievalResponseProcessor
{
    private static Logger          logger                        = Logger.getLogger(PinRetrievalResponseProcessor.class);

    private Schema                 preDecryptionSchema           = null;

    private Schema                 postDecryptionSchema          = null;

    private ThreadLocal<Validator> preDecryptionSchemaValidator  = null;

    private ThreadLocal<Validator> postDecryptionSchemaValidator = null;

    PinRetrievalResponseProcessor() throws SAXException, IOException
    {
        super();

        // Load schemas
        setPreDecryptionSchema(XMLUtils.loadSchema(ViewPinConstants.VIEWPIN_PRE_DECRYPTION_SCHEMA));
        setPostDecryptionSchema(XMLUtils.loadSchema(ViewPinConstants.VIEWPIN_POST_DECRYPTION_SCHEMA));

        setPreDecryptionSchemaValidator(new ThreadLocal<Validator>());
        setPostDecryptionSchemaValidator(new ThreadLocal<Validator>());
    }

    ArrayList<CardPin> processPinRetrievalResponse (String encodedCompressedPinRetrievalResponse, String encodedWrappedSessionKey, PinAgent pinAgent) throws PinRetrievalResponseException
    {
        ArrayList<CardPin> cardPins;
        byte[] compressedPinRetrievalResponse;
        byte[] decompressedPinRetrievalResponse;
        DocumentBuilder documentBuilder;
        ValidationResult parsingValidationResult;
        Document pinRetrievalResponseDocument;
        ValidationResult schemaValidationResult;
        boolean timestampValid;
        SessionCipherProperties sessionCipherProperties;
        SecretKey sessionKey;
   

        cardPins = null;
        compressedPinRetrievalResponse = null;
        decompressedPinRetrievalResponse = null;
        documentBuilder = null;
        parsingValidationResult = null;
        pinRetrievalResponseDocument = null;
        schemaValidationResult = null;
        timestampValid = false;
        sessionCipherProperties = null;
        sessionKey = null;
        
        
       
        try
        {
            // Decode compressed pin retrieval response
            compressedPinRetrievalResponse = UrlSafeBase64.decode(encodedCompressedPinRetrievalResponse);
        }
        catch (IOException ioe)
        {
            getLogger().warn("decoding compressed pin retrieval response " + ioe.getMessage());

            throw new PinRetrievalResponseException();
        }

        try
        {
            // Decompress pin retrieval response
            decompressedPinRetrievalResponse = Utils.decompress(compressedPinRetrievalResponse, ViewPinConstants.DECOMPRESSION_READ_BUFFER_LENGTH);
                    }
        catch (IOException ioe)
        {
            // Failed to decompress pin retrieval response
            getLogger().error("decompressing pin retrieval response " + ioe.getMessage());

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("decoded pin retrieval response OK");

        getLogger().debug("parsing pin retrieval response document");

        try
        {
            parsingValidationResult = new ValidationResult();

            documentBuilder = CachedDocumentBuilder.getCachedDocumentBuilder(parsingValidationResult);

            // Parse pin retrieval response document
            pinRetrievalResponseDocument = XMLUtils.parseDocument(decompressedPinRetrievalResponse, documentBuilder);
        }
        catch (Exception e)
        {
            getLogger().error("parsing pin retrieval response document " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        // Did we parse pin retrieval response document OK?
        if (parsingValidationResult.isValid() == false)
        {
            // Failed to parse pin retrieval response document OK
        	if(null != parsingValidationResult.getException())
			{
	            getLogger().error("parsing pin retrieval response document " + parsingValidationResult.getException().getMessage());
			}
            throw new PinRetrievalResponseException();
        }

        getLogger().debug("parsed pin retrieval response document OK");

        if (getLogger().isDebugEnabled())
        {
            try
            {
                String s = new String(Hex.encodeHex(XMLUtils.serialise(pinRetrievalResponseDocument)));

            }
            catch (TransformerConfigurationException e)
            {
                getLogger().error("serialising pin retrieval response pre decryption");
            }
            catch (TransformerException e)
            {
                getLogger().error("serialising pin retrieval response pre decryption");
            }
            catch (IOException e)
            {
                getLogger().error("serialising pin retrieval response pre decryption");
            }

        }

        getLogger().debug("validating pin retriveal response document against pre-decryption schema");
   
        try
        {
            // Validate pin retrieval response document against pre-decryption schema
            schemaValidationResult = XMLUtils.validateAgainstSchema(pinRetrievalResponseDocument, getPreDecryptionSchema(), getPreDecryptionSchemaValidator());
        }
        catch (Exception e)
        {
            // Failed to validate pin retrieval response document against pre-decryption schema
            getLogger().warn("validating pin retriveal response document against pre-decryption schema " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        // Did we validate pin retrieval response document against pre-decryption schema OK?
        if (schemaValidationResult.isValid() == false)
        {
            // Failed to validate pin retrieval response document against pre-decryption schema
			if(schemaValidationResult.getException() != null)
			{
				getLogger().error("validating pin retrieval response document against pre-decryption schema " + schemaValidationResult.getException().getMessage());
			}
            throw new PinRetrievalResponseException();
        }

        getLogger().debug("pin retrieval response validated against pre decryption schema OK");

        try
        {
            // Verify pin retrieval response document signature
            if (verifySignature(pinRetrievalResponseDocument, pinAgent) == false)
            {
                // Failed to verify pin retrieval response document signature
                getLogger().error("verifying pin retrieval response document signature");

                throw new PinRetrievalResponseException();
            }
        }
        catch (Exception e)
        {
            getLogger().error("verifying pin retrieval response document signature " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("verified pin retrieval response document signature ok");

        try
        {
            // Process transaction identifier
            processTransactionIdentifier(pinRetrievalResponseDocument);
        }
        catch (Exception e)
        {
            getLogger().error("processing transaction identifier " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        // Validate timestamp
        timestampValid = validateTimestamp(pinRetrievalResponseDocument, pinAgent.getReplayWindow());

        // Ensure timestamp is valid
        if (timestampValid == false)
        {
            getLogger().error("timestamp is outside maximum replay attack window");

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("timestamp is inside maximum replay attack window");

        try
        {
            // Determine session cipher properties
            sessionCipherProperties = determineSessionCipherProperties(pinRetrievalResponseDocument);
        }
        catch (Exception e)
        {
            getLogger().error("attempting to determine session cipher properties");

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("session cipher key type = " + sessionCipherProperties.getKeyType().getKeyType());
        getLogger().debug("session cipher key length = " + sessionCipherProperties.getKeyLength());
        getLogger().debug("session cipher block size = " + sessionCipherProperties.getBlockSize());

        getLogger().debug("recovering session key");

        try
        {
            // Recover session key
            sessionKey = recoverSessionKey(encodedWrappedSessionKey, pinAgent.getAgentWrapper(), sessionCipherProperties);

            getLogger().debug("recovered session key OK");

            getLogger().debug("processing encrypted CardPin elements");

            try
            {
                // Process encrypted CardPin elements
                processEncryptedCardPins(pinRetrievalResponseDocument, sessionKey);
            }
            catch (Exception e)
            {
                getLogger().error("processing encrypted CardPin elements " + e.getMessage());

                throw new PinRetrievalResponseException();
            }

            getLogger().debug("processed encrypted CardPin elements");
        }
        finally
        {
            if (sessionKey != null)
            {
                getLogger().debug("destroying session key");

                ((LunaKey) sessionKey).DestroyKey();

                getLogger().debug("destroyed session key OK");
            }
        }

        getLogger().debug("validating pin retriveal response document against post-decryption schema");
       
       try
        {
            // Validate pin retrieval response document against post-decryption schema
            schemaValidationResult = XMLUtils.validateAgainstSchema(pinRetrievalResponseDocument, getPostDecryptionSchema(), getPostDecryptionSchemaValidator());
        }
        catch (Exception e)
        {
            // Failed to validate pin retrieval response document against post-decryption schema
            getLogger().error("validating pin retriveal response document against post-decryption schema " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        // Did we validate pin retrieval response document against post-decryption schema OK?
        if (schemaValidationResult.isValid() == false)
        {
        	if(schemaValidationResult.getException() != null)
			{
			  // Failed to validate pin retrieval response document against post-decryption schema
			  getLogger().error("validating pin retrieval response document against post-decryption schema " + schemaValidationResult.getException().getMessage());
			}
            throw new PinRetrievalResponseException();
        }

        getLogger().debug("pin retrieval response validated against post-decryption schema OK");

        try
        {
            // Build card pins
            cardPins = buildCardPins(pinRetrievalResponseDocument);
        }
        catch (Exception e)
        {
            // Failed to build card pins
            getLogger().error("building card pins " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        return cardPins;
    }

    private ArrayList<CardPin> buildCardPins (Document pinRetrievalResponseDocument) throws PinRetrievalResponseException
    {
        Element pinRetrievalResponseElement;
        ArrayList<CardPin> cardPins;
        ArrayList<Element> cardPinElements;
        Element nextPinElement;
        Element nextCardPinElement;
        String nextPin;
        Element nextPrimaryAccountNumberElement;
        String nextPrimaryAccountNumber;
        CardPin nextCardPin;

        pinRetrievalResponseElement = null;
        cardPins = null;
        cardPinElements = null;
        nextPinElement = null;
        nextCardPinElement = null;
        nextPin = null;
        nextPrimaryAccountNumberElement = null;
        nextPrimaryAccountNumber = null;
        nextCardPin = null;

        // Get pin retrieval response element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Instantiate collection to hold card pins
        cardPins = new ArrayList<CardPin>();

        // Get all CardPin elements
        cardPinElements = XMLUtils.getChildElementsMatchingName(pinRetrievalResponseElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "CardPin");

        // Work through each CardPin element
        for (int i = 0; i < cardPinElements.size(); i++)
        {
            // Get next CardPin element
            nextCardPinElement = cardPinElements.get(i);

            // Get next Pin element
            nextPinElement = XMLUtils.getFirstChildElementMatchingName(nextCardPinElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "Pin");
            
            // Did we get next Pin element OK?
            if (nextPinElement == null)
            {
                // Failed to get next Pin element
                getLogger().error("retreiving Pin element");

                throw new PinRetrievalResponseException();
            }
            
            // Get next pin
            nextPin = nextPinElement.getTextContent();

            // Get next primary account number element
            nextPrimaryAccountNumberElement = XMLUtils.getFirstChildElementMatchingName(nextCardPinElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "PrimaryAccountNumber");

            // Did we get next primary account number element OK?
            if (nextPrimaryAccountNumberElement != null)
            {
                // Got next primary account number element
                nextPrimaryAccountNumber = nextPrimaryAccountNumberElement.getTextContent();
            }
            
            try
            {
            // Instantiate next CardPin object
            nextCardPin = new CardPin(nextPin);
            }
            catch(InvalidCardPinException icpe)
            {
            	
            }
            // Add next CardPin object to collection
            cardPins.add(nextCardPin);
        }

        return cardPins;
    }

    private void processEncryptedCardPins (Document pinRetrievalResponseDocument, SecretKey sessionKey) throws PinRetrievalResponseException
    {
        Element pinRetrievalResponseElement;
        ArrayList<Element> cardPinElements;
        Element nextCardPinElement;
        Element nextPinElement;
        Element nextPinEncryptedDataElement;
        String nextDecryptedPin;
        Element nextPanElement;
        Element nextPanEncryptedDataElement;
        String nextDecryptedPan;

        pinRetrievalResponseElement = null;
        cardPinElements = null;
        nextCardPinElement = null;
        nextPinElement = null;
        nextPinEncryptedDataElement = null;
        nextDecryptedPin = null;
        nextPanElement = null;
        nextPanEncryptedDataElement = null;
        nextDecryptedPan = null;
        
        // Get pin retrieval response element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Get all CardPin elements
        cardPinElements = XMLUtils.getChildElementsMatchingName(pinRetrievalResponseElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "CardPin");

        // Work through each CardPin element
        for (int i = 0; i < cardPinElements.size(); i++)
        {
            // Get next CardPin element
            nextCardPinElement = cardPinElements.get(i);

            // Get next Pin element
            nextPinElement = XMLUtils.getFirstChildElementMatchingName(nextCardPinElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "Pin");
           
            // Did we get next Pin element
            if (nextPinElement == null)
            {
                // Failed to get next Pin element
                getLogger().error("retrieving Pin element missing from CardPin element");

                throw new PinRetrievalResponseException();
            }

            // Get next pin EncryptedData element
            nextPinEncryptedDataElement = XMLUtils.getFirstChildElementMatchingName(nextPinElement, ViewPinConstants.XENC_NAMESPACE_URI, "EncryptedData");

            // Did we get next EncryptedData element?
            if (nextPinEncryptedDataElement == null)
            {
                // Failed to get next EncryptedData element
                getLogger().error("retrieving EncryptedData element from Pin element");

                throw new PinRetrievalResponseException();
            }
            
            try
            {
                // Decrypt next pin EncryptedData element
                nextDecryptedPin = decryptEncryptedData(sessionKey, nextPinEncryptedDataElement);
				 
            }
            catch (Exception e)
            {
                // Failed to decrypt next pin EncryptedData
                getLogger().error("decrypting next pin EncryptedData element " + e.getMessage());

                throw new PinRetrievalResponseException();
            }
            /*
            // Get next Pan element
            nextPanElement = XMLUtils.getFirstChildElementMatchingName(nextCardPinElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "PrimaryAccountNumber");
           
            // Did we get next Pan element
            if (nextPanElement == null)
            {
                // Failed to get next Pin element
                getLogger().error("retrieving Pan element missing from CardPin element");

                throw new PinRetrievalResponseException();
            }
            
            // Get next pan EncryptedData element
            nextPanEncryptedDataElement = XMLUtils.getFirstChildElementMatchingName(nextPanElement, ViewPinConstants.XENC_NAMESPACE_URI, "EncryptedData");

            try
            {
                // Decrypt next pan EncryptedData element
                nextDecryptedPan = decryptEncryptedData(sessionKey, nextPanEncryptedDataElement);
            }
            catch (Exception e)
            {
                // Failed to decrypt next pin EncryptedData
                getLogger().error("decrypting next pan EncryptedData element " + e.getMessage());

                throw new PinRetrievalResponseException();
            }
*/
            // Replace EncryptedData element with decrypted pin
            nextPinElement.replaceChild(pinRetrievalResponseDocument.createTextNode(nextDecryptedPin), nextPinEncryptedDataElement);
            // Replace EncryptedData element with decrypted pan
         //   nextPanElement.replaceChild(pinRetrievalResponseDocument.createTextNode(nextDecryptedPan), nextPanEncryptedDataElement);
        }
    }

    private String decryptEncryptedData (SecretKey sessionKey, Element encryptedDataElement) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            InvalidAlgorithmParameterException, BadPaddingException, IOException, NoSuchProviderException, PinRetrievalResponseException
    {
        Element cipherDataElement;
        Element cipherValueElement;
        String decryptedCipherValue;
        String encodedCipherValue;
        byte[] decodedCipherValue;
        Element encryptionMethodElement;
        String encryptionMethodAlgorithm;

        cipherDataElement = null;
        cipherValueElement = null;
        encodedCipherValue = null;
        decodedCipherValue = null;
        encryptionMethodElement = null;
        decryptedCipherValue = null;
        encryptionMethodAlgorithm = null;

        // Get CipherData element
        cipherDataElement = XMLUtils.getFirstChildElementMatchingName(encryptedDataElement, ViewPinConstants.XENC_NAMESPACE_URI, "CipherData");

        // Did we get CipherData element OK?
        if (cipherDataElement == null)
        {
            // Failed to get CipherData element
            getLogger().error("retrieving CipherData element");

            throw new PinRetrievalResponseException();
        }

        // Get CipherValue element
        cipherValueElement = XMLUtils.getFirstChildElementMatchingName(cipherDataElement, ViewPinConstants.XENC_NAMESPACE_URI, "CipherValue");

        // Did we get CipherValue element OK?
        if (cipherValueElement == null)
        {
            // Failed to get CipherValue element
            getLogger().error("retrieving CipherValue element");

            throw new PinRetrievalResponseException();
        }

        // Get encoded cipher value
        encodedCipherValue = cipherValueElement.getTextContent();

        // Ensure cipher value is correctly encoded
        if (Base64.isArrayByteBase64(encodedCipherValue.getBytes()) == false)
        {
            // Cipher value is not correctly encoded
            getLogger().error("cipher value is not correctly encoded");

            throw new IOException("cipher value is not correctly encoded");
        }

        // Decode cipher value
        decodedCipherValue = Base64.decodeBase64(encodedCipherValue.getBytes());
       
        // Get EncryptionMethod element
        encryptionMethodElement = XMLUtils.getFirstChildElementMatchingName(encryptedDataElement, ViewPinConstants.XENC_NAMESPACE_URI, "EncryptionMethod");

        // Did we get EncryptionMethod element OK?
        if (encryptionMethodElement == null)
        {
            // Failed to get EncryptionMethod element
            getLogger().error("retrieving EncryptionMethod element");

            throw new PinRetrievalResponseException();
        }

        // Get encryption method algorithm
        encryptionMethodAlgorithm = encryptionMethodElement.getAttribute("Algorithm");

        getLogger().debug("encryption method algorithm = " + encryptionMethodAlgorithm);

        // Decrypt cipher value
        decryptedCipherValue = decrypt(sessionKey, decodedCipherValue, encryptionMethodAlgorithm);

        return decryptedCipherValue;
    }

    private static String decrypt (SecretKey sessionKey, byte[] cipherTextPlusIv, String encryptionMethodAlgorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchProviderException
    {
      /*  String clearText;
        Cipher sessionCipher;
        int sessionCipherBlockSize;
        byte[] iv;
        IvParameterSpec ivps;
        byte[] cipherTextMinusIv;

        clearText = null;
        sessionCipher = null;
        sessionCipherBlockSize = 0;
        iv = null;
        ivps = null;
        cipherTextMinusIv = null;*/
        StringBuffer clearText;
        Cipher sessionCipher;
        int sessionCipherBlockSize;
        String sessionCipherTransformation;
        byte[] iv;
        IvParameterSpec ivps;
        byte[] cipherTextMinusIv;
        int totalCipherTextBlocks;
        byte[] nextClearTextBlock;
        int totalNonPaddingCipherTextBlocks;
        byte[] lastClearTextBlock;
        int totalPaddingBytes;
        int totalNonPaddingBytes;
        byte[] nonPaddingBytes;

        clearText = null;
        sessionCipher = null;
        sessionCipherBlockSize = 0;
        sessionCipherTransformation = null;
        iv = null;
        ivps = null;
        cipherTextMinusIv = null;
        totalCipherTextBlocks = 0;
        nextClearTextBlock = null;
        totalNonPaddingCipherTextBlocks = 0;
        lastClearTextBlock = null;
        totalPaddingBytes = 0;
        totalNonPaddingBytes = 0;
        nonPaddingBytes = null;
        // Instantiate session cipher
//        sessionCipher = CipherFactory.getInstance(encryptionMethodAlgorithm);
        getLogger().debug("Setting session cipher transformation ");
        // Build session cipher transformation
        sessionCipherTransformation = "AES/CBC/NoPadding";

        // Instantiate session cipher
        sessionCipher = Cipher.getInstance(sessionCipherTransformation);

        getLogger().debug("session cipher transformation = " + sessionCipherTransformation);

        // Get session cipher block size
        sessionCipherBlockSize = sessionCipher.getBlockSize();

        getLogger().debug("session cipher block size = " + sessionCipherBlockSize);
       

        // Ensure we have an initialisation vector and at least one cipher text block
        if (cipherTextPlusIv.length < (sessionCipherBlockSize * 2))
        {
            // We do not have an initialisation vector and at least one cipher text block
        	getLogger().debug("c");
           // throw new Exception("initialisation vector and at least one cipher text block is required");
        }

        // Extract intialisation vector
        iv = new byte[sessionCipherBlockSize];
        System.arraycopy(cipherTextPlusIv, 0, iv, 0, iv.length);
        ivps = new IvParameterSpec(iv);

        
        // Intialise session cipher
        sessionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivps);

        // Create buffer to hold cipher text minus initialisation vector
        cipherTextMinusIv = new byte[cipherTextPlusIv.length - sessionCipherBlockSize];

        // Remove intialisation vector from cipher text
        System.arraycopy(cipherTextPlusIv, sessionCipherBlockSize, cipherTextMinusIv, 0, cipherTextMinusIv.length);

        // Ensure length of cipher text is a multiple of the session key block size
        if ((cipherTextMinusIv.length % sessionCipherBlockSize) != 0)
        {
           // throw new Exception("cipher text is not a multiple of session key block size");
        	getLogger().debug("c 1");
        }

        // Calculate total number of cipher blocks
        totalCipherTextBlocks = cipherTextMinusIv.length / sessionCipherBlockSize;

      //  getLogger().debug("total cipher text blocks = " + totalCipherTextBlocks);

        // Work out how many non padding cipher text blocks we have
        totalNonPaddingCipherTextBlocks = totalCipherTextBlocks - 1;

        //getLogger().debug("total non padding cipher text blocks = " + totalNonPaddingCipherTextBlocks);

        clearText = new StringBuffer();

        // Work through each non padding cipher text block
        for (int i = 0; i < totalNonPaddingCipherTextBlocks; i++)
        {
            // Decrypt next non padding cipher text block
            nextClearTextBlock = sessionCipher.update(cipherTextMinusIv, i * sessionCipherBlockSize, sessionCipherBlockSize);

          //  getLogger().debug("next clear text block = " + new String(nextClearTextBlock));

            clearText.append(new String(nextClearTextBlock));
        }

        // Decrypt last cipher text block
        lastClearTextBlock = sessionCipher.doFinal(cipherTextMinusIv, totalNonPaddingCipherTextBlocks * sessionCipherBlockSize, sessionCipherBlockSize);

    //    getLogger().debug("last clear text block = " + new String(Hex.encodeHex(lastClearTextBlock)));

        // Determine total number of padding bytes in last clear text block
        totalPaddingBytes = lastClearTextBlock[sessionCipherBlockSize - 1];

    //    getLogger().debug("total padding bytes in last clear text block = " + totalPaddingBytes);

        // Ensure total padding padding bytes is between one and session cipher block size
        if ((totalPaddingBytes < 1) || (totalPaddingBytes > sessionCipherBlockSize))
        {
            // Total padding bytes is not between one and session cipher block size
           // throw new Exception("total padding bytes is not between one and session cipher block size");
        	getLogger().debug("c 2");
        }

        // Work out how many bytes of last clear text block are non padding bytes
        totalNonPaddingBytes = sessionCipherBlockSize - totalPaddingBytes;

       // getLogger().debug("total non padding bytes = " + totalNonPaddingBytes);

        // Are there any non padding bytes in the last cipher text block?
        if (totalNonPaddingBytes > 0)
        {
            // There are non padding bytes in the last cipher text block
            nonPaddingBytes = new byte[totalNonPaddingBytes];

            // Get non padding bytes in last cipher text block
            System.arraycopy(lastClearTextBlock, 0, nonPaddingBytes, 0, nonPaddingBytes.length);

            clearText.append(new String(nonPaddingBytes));
        }
        else
        {
            getLogger().debug("no non padding bytes in last block");
        }

        return clearText.toString();
		/*  sessionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        getLogger().debug("session cipher transformation = " + sessionCipher.getAlgorithm());
        getLogger().debug("session cipher provider = " + sessionCipher.getProvider());

        // Get session cipher block size
        sessionCipherBlockSize = sessionCipher.getBlockSize();

        getLogger().debug("session cipher block size = " + sessionCipherBlockSize);

        getLogger().debug("cipher text plus initialisation vector length = " + cipherTextPlusIv.length);

        // Ensure we have an initialisation vector and at least one cipher text block
        if (cipherTextPlusIv.length < (sessionCipherBlockSize * 2))
        {
            // We do not have an initialisation vector and at least one cipher text block
            throw new IllegalBlockSizeException("initialisation vector and at least one cipher text block is required");
        }

        // Extract intialisation vector
        iv = new byte[sessionCipherBlockSize];
        System.arraycopy(cipherTextPlusIv, 0, iv, 0, iv.length);
        ivps = new IvParameterSpec(iv);

        getLogger().debug("intialisation vector = " + new String(Hex.encodeHex(iv)));

        // Intialise session cipher
        sessionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivps);

        // Create buffer to hold cipher text minus initialisation vector
        cipherTextMinusIv = new byte[cipherTextPlusIv.length - sessionCipherBlockSize];

        // Remove intialisation vector from cipher text
        System.arraycopy(cipherTextPlusIv, sessionCipherBlockSize, cipherTextMinusIv, 0, cipherTextMinusIv.length);

        // Ensure length of cipher text is a multiple of the session key block size
        if ((cipherTextMinusIv.length % sessionCipherBlockSize) != 0)
        {
            throw new IllegalBlockSizeException("cipher text is not a multiple of session key block size");
        }

        // Decrypt cipher text
        clearText = new String(sessionCipher.doFinal(cipherTextMinusIv));*/

        //return clearText;
    }

    private SecretKey recoverSessionKey (String encodedWrappedSessionKey, Wrapper agentWrapper, SessionCipherProperties sessionCipherProperties) throws PinRetrievalResponseException
    {
        SecretKey sessionKey;
        byte[] decodedWrappedSessionKey;

        sessionKey = null;
        decodedWrappedSessionKey = null;

        // Ensure encoded wrapped session key is not null
        if (encodedWrappedSessionKey == null)
        {
            // Encoded wrapped session key is null
            getLogger().warn("encoded wrapped session key is null");

            throw new PinRetrievalResponseException();
        }

        try
        {
            decodedWrappedSessionKey = UrlSafeBase64.decode(encodedWrappedSessionKey);
        }
        catch (IOException ioe)
        {
            getLogger().warn("decoding wrapped session key " + ioe.getMessage());

            throw new PinRetrievalResponseException();
        }

        try
        {
            // Unwrap session key
            sessionKey = SessionKeyWrapper.unwrapSessionKey(decodedWrappedSessionKey, agentWrapper.getWrappingPaddingScheme(), agentWrapper.getWrappingKey(), sessionCipherProperties);
        }
        catch (Exception e)
        {
            // Failed to unwrap session key
            getLogger().error("unwrapping session key " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        return sessionKey;
    }

    private SessionCipherProperties determineSessionCipherProperties (Document pinRetrievalResponseDocument) throws UnsupportedKeyTypeException, InvalidSessionKeyLengthException,
            PinRetrievalResponseException
    {
        SessionCipherProperties sessionCipherProperties;
        Element pinRetrievalResponseElement;
        Element cardPinElement;
        Element pinElement;
        Element encryptedDataElement;
        Element encryptionMethodElement;

        String encryptionMethodAlgorithm;

        encryptionMethodAlgorithm = null;
        pinRetrievalResponseElement = null;
        cardPinElement = null;
        pinElement = null;
        encryptedDataElement = null;
        encryptionMethodElement = null;

        // Get pin retrieval response element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Get first CardPin element
        cardPinElement = XMLUtils.getFirstChildElementMatchingName(pinRetrievalResponseElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "CardPin");

        // Did we get first CardPin element OK?
        if (cardPinElement == null)
        {
            // Failed to get first CardPin element
            getLogger().error("retrieving first CardPin element");

            throw new PinRetrievalResponseException();
        }

        // Get pin element
        pinElement = XMLUtils.getFirstChildElementMatchingName(cardPinElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "Pin");

        // Did we get pin element OK?
        if (pinElement == null)
        {
            // Failed to get pin element
            getLogger().error("retrieving pin element");

            throw new PinRetrievalResponseException();
        }

        // Get EncryptedData element
        encryptedDataElement = XMLUtils.getFirstChildElementMatchingName(pinElement, ViewPinConstants.XENC_NAMESPACE_URI, "EncryptedData");

        // Did we get EncryptedData element OK?
        if (encryptedDataElement == null)
        {
            getLogger().error("retrieving EncryptedData element");

            throw new PinRetrievalResponseException();
        }

        // Get EncryptionMethod element
        encryptionMethodElement = XMLUtils.getFirstChildElementMatchingName(encryptedDataElement, ViewPinConstants.XENC_NAMESPACE_URI, "EncryptionMethod");

        // Did we get EncryptionMethod element OK?
        if (encryptionMethodElement == null)
        {
            // Failed to get EncryptionMethod element
            getLogger().error("retrieving EncryptionMethod element");

            throw new PinRetrievalResponseException();
        }

        // Get encryption method algorithm
        encryptionMethodAlgorithm = encryptionMethodElement.getAttribute("Algorithm");

        getLogger().debug("encryption method algorithm = " + encryptionMethodAlgorithm);

        // Map encryption method algorithm to session cipher properties
        sessionCipherProperties = XmlEncryptionAlgorithmMapper.map(encryptionMethodAlgorithm);

        // Did we map encryption method algorithm to session cipher properties OK?
        if (sessionCipherProperties == null)
        {
            // Failed to map encryption method algorithm to session cipher properties
            getLogger().error("mapping encryption method algorithm to session cipher properties");

            throw new PinRetrievalResponseException();
        }

        return sessionCipherProperties;
    }

    private boolean validateTimestamp(Document pinRetrievalResponseDocument, long replayWindow) throws PinRetrievalResponseException
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
        timestamp = getTimestamp(pinRetrievalResponseDocument);

        // Get current time
        currentTime = new Date();

        // Calculate elapsed time
        elapsedTime = currentTime.getTime() - timestamp.getTime();

        if(getLogger().isDebugEnabled() == true)
        {
        	getLogger().debug("timestamp = " + timestamp);
            getLogger().debug("timestamp in milliseconds = " + timestamp.getTime());
            getLogger().debug("current time = " + Utils.formatDate(currentTime));
            getLogger().debug("current time in milliseconds= " + currentTime.getTime());
            getLogger().debug("elapsed time in milliseconds = " + elapsedTime);
            getLogger().debug("positive replay window = " + +replayWindow);
            getLogger().debug("negative replay window = " + -replayWindow);
        }

        // Is elapsed time outside replay window?
        if(elapsedTime < -replayWindow || elapsedTime > +replayWindow)
        {
        	// Elapsed time is outside replay window
        	valid = false;
        }
    	else
    	{
    		// Elapsed time is inside replay window
    		valid = true;
    	}
    
        return valid;
    }
    
    private Date getTimestamp(Document pinRetrievalResponseDocument) throws PinRetrievalResponseException
    {
        Date parsedTimestamp;
        Element pinRetrievalResponseElement;
        Element timestampElement;
        
        parsedTimestamp = null;
        pinRetrievalResponseElement = null;
        timestampElement = null;

        // Get pin retrieval response element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Get time stamp element
        timestampElement = XMLUtils.getFirstChildElementMatchingName(pinRetrievalResponseElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "timestamp");

        // Did we get time stamp element OK?
        if(timestampElement == null)
        {
            // Failed to get time stamp element
            getLogger().error("retreiving time stamp element");

            throw new PinRetrievalResponseException();
        }

        try
        {
            // Parse time stamp
            parsedTimestamp = Utils.parseDateTime(timestampElement.getTextContent());
        }
        catch(ParseException pe)
        {
            getLogger().error("parsing time stamp " + pe.getMessage());

            throw new PinRetrievalResponseException();
        }
        
        return parsedTimestamp;
    }

    private void processTransactionIdentifier (Document pinRetrievalResponseDocument) throws PinRetrievalResponseException
    {
        Element pinRetrievalResponseElement;
        Element transactionIdentifierElement;
        String transactionIdentifier;

        pinRetrievalResponseElement = null;
        transactionIdentifierElement = null;
        transactionIdentifier = null;

        // Get PinRetrievalResponse element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Get transaction identifier element
        transactionIdentifierElement = XMLUtils.getFirstChildElementMatchingName(pinRetrievalResponseElement, ViewPinConstants.VIEWPIN_NAMESPACE_URI, "TransactionIdentifier");

        // Did we get transaction identifier element OK?
        if (transactionIdentifierElement == null)
        {
            // Failed to get transaction identifier element
            getLogger().error("retrieving transaction identifier element");

            throw new PinRetrievalResponseException();
        }

        // Get transaction identifier
        transactionIdentifier = transactionIdentifierElement.getTextContent();

        // Make transaction identifier to all code within this thread
        MDC.put("transactionIdentifier", transactionIdentifier);
    }

    private boolean verifySignature (Document pinRetrievalResponseDocument, PinAgent pinAgent) throws PinRetrievalResponseException
    {
        boolean signatureValid;
        Element rootElement;
        Element signatureElement;
        SubjectKeyIdentifier authoritySigningCertificateSubjectKeyIdentifier;
        Element signedInfoElement;
        Certificate authoritySigningCertificate;

        signatureValid = false;
        rootElement = null;
        signatureElement = null;
        authoritySigningCertificateSubjectKeyIdentifier = null;
        signedInfoElement = null;

        // Get document root element
        rootElement = pinRetrievalResponseDocument.getDocumentElement();

        // Did we get document root element OK?
        if (rootElement == null)
        {
            // Failed to retrieve document root element
            getLogger().error("retrieving document root element");

            throw new PinRetrievalResponseException();
        }

        // Get Signature element
        signatureElement = XMLUtils.getFirstChildElementMatchingName(rootElement, ViewPinConstants.DSIG_NAMESPACE_URI, "Signature");

        // Did we get Signature element ok?
        if (signatureElement == null)
        {
            // Failed to get Signature element
            getLogger().error("retrieving Signature element");

            throw new PinRetrievalResponseException();
        }

        // Get authority signing certificate subject key identifier
        authoritySigningCertificateSubjectKeyIdentifier = identifySigningCertificate(signatureElement);

        getLogger().debug("authority signing certificate subject key identifier = " + authoritySigningCertificateSubjectKeyIdentifier.getHexEncoded());

        // Get authority signing certificate
        authoritySigningCertificate = pinAgent.getPinAuthorities().getBySigning(authoritySigningCertificateSubjectKeyIdentifier).getSigningCertificate();

        // Did we retrieve authority signing certificate OK?
        if (authoritySigningCertificate == null)
        {
            getLogger().error("authority signing certificate not found");

            throw new PinRetrievalResponseException();
        }

        // Get SignedInfo element
        signedInfoElement = XMLUtils.getFirstChildElementMatchingName(signatureElement, ViewPinConstants.DSIG_NAMESPACE_URI, "SignedInfo");

        // Did we get SignedInfo element OK?
        if (signedInfoElement == null)
        {
            // Failed to get SignedInfo element
            getLogger().error("retrieving SignedInfo element");

            return false;
        }

        try
        {
            // Verify SignedInfo signature
            if (verifySignedInfo(signedInfoElement, authoritySigningCertificate) == false)
            {
                getLogger().error("SignedInfo signature verification not valid");

                signatureValid = false;

                return signatureValid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("verifying SignedInfo signature " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("verified SignedInfo signature OK");

        try
        {
            // Verify enveloped signature reference
            if (verifyEnvelopedSignatureReference(pinRetrievalResponseDocument, signedInfoElement) == false)
            {
                // Failed to verify enveloped signature reference
                getLogger().error("enveloped signature reference not valid");

                signatureValid = false;

                return signatureValid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("verifying enveloped signature reference " + e.getMessage());

            throw new PinRetrievalResponseException();
        }

        getLogger().debug("verified enveloped signature reference OK");

        signatureValid = true;

        return signatureValid;
    }

    private static boolean verifyEnvelopedSignatureReference (Document pinRetrievalResponseDocument, Element signedInfoElement) throws InvalidCanonicalizerException, CanonicalizationException,
            NoSuchAlgorithmException, IOException
    {
        boolean referenceValid;
        Element pinRetrievalResponseElement;
        Element referenceElement;
        Element digestValueElement;
        String encodedDigestValue;
        Element digestMethodElement;
        String digestMethodAlgorithm;
        Element signatureElement;
        Canonicalizer canonicalizer;
        byte[] canonicalizedPinRetrievalResponseMinusSignature;
        MessageDigest md;
        byte[] digestedCanonicalizedPinRetrievalResponseMinusSignature;
        String encodedDigestedCanonicalizedPinRetrievalResponseMinusSignature;

        referenceValid = false;
        pinRetrievalResponseElement = null;
        referenceElement = null;
        digestValueElement = null;
        encodedDigestValue = null;
        digestMethodElement = null;
        digestMethodAlgorithm = null;
        signatureElement = null;
        canonicalizer = null;
        canonicalizedPinRetrievalResponseMinusSignature = null;
        md = null;
        digestedCanonicalizedPinRetrievalResponseMinusSignature = null;
        encodedDigestedCanonicalizedPinRetrievalResponseMinusSignature = null;

        // Get PinRetrievalResponse element
        pinRetrievalResponseElement = pinRetrievalResponseDocument.getDocumentElement();

        // Get Reference element
        referenceElement = XMLUtils.getFirstChildElementMatchingName(signedInfoElement, ViewPinConstants.DSIG_NAMESPACE_URI, "Reference");

        // Did we get Reference element OK?
        if (referenceElement == null)
        {
            // Failed to get Reference element
            getLogger().error("retrieving Reference element");

            return false;
        }

        // Get DigestValue element
        digestValueElement = XMLUtils.getFirstChildElementMatchingName(referenceElement, ViewPinConstants.DSIG_NAMESPACE_URI, "DigestValue");

        // Did we get DigestValue element OK?
        if (digestValueElement == null)
        {
            // Failed to get DigestValue element
            getLogger().error("retrieving DigestValue element");

            return false;
        }

        // Get encoded DigestValue
        encodedDigestValue = digestValueElement.getTextContent();

        // Ensure encoded digest value is correctly encoded
        if (Base64.isArrayByteBase64(encodedDigestValue.getBytes()) == false)
        {
            // Encoded digest value is not correctly encoded
            getLogger().error("encoded digest value is not correctly encoded");

            throw new IOException();
        }

        // Get DigestMethod element
        digestMethodElement = XMLUtils.getFirstChildElementMatchingName(referenceElement, ViewPinConstants.DSIG_NAMESPACE_URI, "DigestMethod");

        // Did we get DigestMethod element OK?
        if (digestMethodElement == null)
        {
            // Failed to get DigestMethod element
            getLogger().error("retrieving DigestMethod element");

            return false;
        }

        // Get digest method
        digestMethodAlgorithm = digestMethodElement.getAttribute("Algorithm");

        // Get Signature element
        signatureElement = (Element) signedInfoElement.getParentNode();

        // Remove Signature element from PinRetrievalResponse
        pinRetrievalResponseElement.removeChild(signatureElement);

        // Canonicalize PinRetrievalResponse minus Signature element element
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedPinRetrievalResponseMinusSignature = canonicalizer.canonicalizeSubtree(pinRetrievalResponseElement);

        // Instantiate message digest engine
        md = MessageDigestFactory.getInstance(digestMethodAlgorithm);

        // Digest canonicalized pin retrieval request minus signature
        digestedCanonicalizedPinRetrievalResponseMinusSignature = md.digest(canonicalizedPinRetrievalResponseMinusSignature);

        // Encode canonicalized pin retrieval request minus signature
        encodedDigestedCanonicalizedPinRetrievalResponseMinusSignature = new String(Base64.encodeBase64(digestedCanonicalizedPinRetrievalResponseMinusSignature, false));

        // Ensure encoded canonicalized pin retrieval response minus signature matches encoded digest value
        if (encodedDigestedCanonicalizedPinRetrievalResponseMinusSignature.equals(encodedDigestValue) == true)
        {
            // Encoded digested canonicalized pin retrieval response matches encoded digest OK
            getLogger().debug("encoded digested canonicalized pin retrieval response matches encoded digest value OK");

            referenceValid = true;
        }
        else
        {
            // Encoded digested canonicalized pin retrieval response does not match encoded digest
            getLogger().error("Encoded digested canonicalized pin retrieval response does not match encoded digest");

            referenceValid = false;
        }

        return referenceValid;
    }

    private boolean verifySignedInfo (Element signedInfoElement, Certificate signingCertificate) throws InvalidCanonicalizerException, CanonicalizationException, SignatureException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException
    {
        boolean signedInfoValid;
        Element signatureElement;
        Canonicalizer canonicalizer;
        byte[] canonicalizedSignedInfo;
        Element signatureMethodElement;
        String signatureMethodAlgorithm;
        Element signatureValueElement;
        String encodedSignatureValue;
        byte[] decodedSignatureValue;
        Signature signatureEngine;

        signedInfoValid = false;
        signatureElement = null;
        canonicalizer = null;
        canonicalizedSignedInfo = null;
        signatureMethodElement = null;
        signatureMethodAlgorithm = null;
        signatureValueElement = null;
        encodedSignatureValue = null;
        decodedSignatureValue = null;
        signatureEngine = null;

        // Get Signature element
        signatureElement = (Element) signedInfoElement.getParentNode();

        // Canonicalize SignedInfo element
        canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        canonicalizedSignedInfo = canonicalizer.canonicalizeSubtree(signedInfoElement);

        getLogger().debug("canonicalizer algorithm URI = " + canonicalizer.getURI());

        // Get signature method element
        signatureMethodElement = XMLUtils.getFirstChildElementMatchingName(signedInfoElement, ViewPinConstants.DSIG_NAMESPACE_URI, "SignatureMethod");

        // Did we get signature method element OK?
        if (signatureMethodElement == null)
        {
            // Failed to get signature method element
            getLogger().error("retreiving SignatureMethod element");

            return false;
        }

        // Get signature method algorithm
        signatureMethodAlgorithm = signatureMethodElement.getAttribute("Algorithm");

        getLogger().debug("signature method algorithm = " + signatureMethodAlgorithm);

        // Get SignatureValue element
        signatureValueElement = XMLUtils.getFirstChildElementMatchingName(signatureElement, ViewPinConstants.DSIG_NAMESPACE_URI, "SignatureValue");

        // Did we get SignatureValue element OK?
        if (signatureValueElement == null)
        {
            // Failed to get SignatureValue element
            getLogger().error("retrieving SignatureValue element");

            return false;
        }

        // Get encoded SignatureValue
        encodedSignatureValue = signatureValueElement.getTextContent();

        // Did we get encoded SignatureValue OK?
        if (encodedSignatureValue == null)
        {
            // Failed to get encoded SignatureValue
            getLogger().error("retrieving encoded SignatureValue");

            throw new SignatureException();
        }

        // Ensure encoded SignatureValue is not empty
        if (encodedSignatureValue.getBytes().length < 1)
        {
            // Encoded SignatureValue is empty
            getLogger().error("encoded SignatureValue is empty");

            throw new SignatureException();
        }

        // Ensure encoded SignatureValue only contains valid Base64 alphabet characters
        if (Base64.isArrayByteBase64(encodedSignatureValue.getBytes()) == false)
        {
            // Encoded SignatureValue contains invalid Base64 alphabet characters
            getLogger().error("encoded SignatureValue contains invalid Base64 alphabet characters");

            throw new SignatureException();
        }

        // Decode signature value
        decodedSignatureValue = Base64.decodeBase64(encodedSignatureValue.getBytes());

        // Get signature engine instance
        signatureEngine = SignatureFactory.getInstance(signatureMethodAlgorithm);

        getLogger().debug("signature engine algorithm = " + signatureEngine.getAlgorithm());
        getLogger().debug("signature engine provider = " + signatureEngine.getProvider());

        // Verify signature
        signatureEngine.initVerify(signingCertificate);
        signatureEngine.update(canonicalizedSignedInfo);
        signedInfoValid = signatureEngine.verify(decodedSignatureValue);

        return signedInfoValid;
    }

    private SubjectKeyIdentifier identifySigningCertificate (Element signatureElement) throws PinRetrievalResponseException
    {
        SubjectKeyIdentifier signingCertificateSubjectKeyIdentifier;
        Element keyInfoElement;

        signingCertificateSubjectKeyIdentifier = null;
        keyInfoElement = null;

        // Get KeyInfo element
        keyInfoElement = XMLUtils.getFirstChildElementMatchingName(signatureElement, ViewPinConstants.DSIG_NAMESPACE_URI, "KeyInfo");

        // Did we get KeyInfo element OK?
        if (keyInfoElement == null)
        {
            // Failed to get KeyInfo element
            getLogger().error("retrieving KeyInfo element");

            throw new PinRetrievalResponseException();
        }

        try
        {
            // Get signing certificate subject key identifier
            signingCertificateSubjectKeyIdentifier = Utils.getX509Ski(keyInfoElement);
        }
        catch (InvalidSubjectKeyIdentifierException iskie)
        {
            getLogger().error("invalid signing certificate subject key identifier");

            throw new PinRetrievalResponseException();
        }

        // Did we get signing certificate subject key identifier OK?
        if (signingCertificateSubjectKeyIdentifier == null)
        {
            getLogger().error("retrieving signing certificate subject key identifier");

            throw new PinRetrievalResponseException();
        }

        return signingCertificateSubjectKeyIdentifier;
    }

    private void setPostDecryptionSchema (Schema postDecryptionSchema)
    {
        this.postDecryptionSchema = postDecryptionSchema;
    }

    private Schema getPostDecryptionSchema ()
    {
        return this.postDecryptionSchema;
    }

    private void setPreDecryptionSchema (Schema preDecryptionSchema)
    {
        this.preDecryptionSchema = preDecryptionSchema;
    }

    private Schema getPreDecryptionSchema ()
    {
        return this.preDecryptionSchema;
    }

    private void setPreDecryptionSchemaValidator (ThreadLocal<Validator> preDecryptionSchemaValidator)
    {
        this.preDecryptionSchemaValidator = preDecryptionSchemaValidator;
    }

    private ThreadLocal<Validator> getPreDecryptionSchemaValidator ()
    {
        return this.preDecryptionSchemaValidator;
    }

    private void setPostDecryptionSchemaValidator (ThreadLocal<Validator> postDecryptionSchemaValidator)
    {
        this.postDecryptionSchemaValidator = postDecryptionSchemaValidator;
    }

    private ThreadLocal<Validator> getPostDecryptionSchemaValidator ()
    {
        return this.postDecryptionSchemaValidator;
    }

    private static Logger getLogger ()
    {
        return logger;
    }
}