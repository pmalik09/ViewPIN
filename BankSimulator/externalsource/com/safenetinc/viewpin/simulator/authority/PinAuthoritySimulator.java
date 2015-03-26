// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/PinAuthoritySimulator.java 1.1 2008/09/04 10:38:26IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;

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

import com.safenetinc.viewpin.simulator.authority.exceptions.CardAccountLockedException;
import com.safenetinc.viewpin.simulator.authority.exceptions.CardHolderAuthenticationException;
import com.safenetinc.viewpin.simulator.authority.exceptions.DuplicateCardAccountException;
import com.safenetinc.viewpin.simulator.authority.exceptions.InvalidExpiryDateException;
import com.safenetinc.viewpin.simulator.authority.exceptions.PinRetrievalRequestException;
import com.safenetinc.viewpin.simulator.authority.exceptions.PinRetrievalResponseException;
import com.safenetinc.viewpin.simulator.authority.exceptions.UnknownCardAccountException;
import com.safenetinc.viewpin.simulator.authority.xml.SafeParser;
import com.safenetinc.viewpin.simulator.authority.xml.ValidationResult;
import com.safenetinc.viewpin.simulator.authority.xml.encryption.XmlEncryption;
import com.safenetinc.viewpin.simulator.authority.xml.signatures.XmlDigestMethodAlgorithms;
import com.safenetinc.viewpin.simulator.authority.xml.signatures.XmlSignature;
import com.safenetinc.viewpin.simulator.authority.xml.signatures.XmlSignatureMethodAlgorithms;

/**
 * Top level class of the PINAuthority Simulator example. The simulator is designed as a reference
 * implementation of the backend required to process request and response messages from the PINAgent.
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class PinAuthoritySimulator
{

    private static Logger                 logger                              = Logger.getLogger(PinAuthoritySimulator.class);

    private KeyStore                      agentKeyStore                       = null;

    private KeyStore                      authorityKeyStore                   = null;

    private PinRetrievalResponseGenerator pinRetrievalResponseGenerator       = null;

    private SecureRandom                  randomNumberGenerator               = null;

    private Schema                        preDecryptionSchema                 = null;

    private Schema                        postDecryptionSchema                = null;

    private CardAccounts                  cardAccounts                        = null;

    private int                           maximumFailedAuthenticationAttempts = ViewPinConstants.DEFAULT_MAXIMUM_FAILED_AUTHENTICATION_ATTEMPTS;

    private long                          maximumReplayOpportunityWindow      = 0L;

    /**
     * Constructor
     * 
     * @param signingSubjectKeyIdentifier The SKI of the PINAuthority Simulator's signing certificate
     * @param configurationPath The path to the configuration, agent.ks and authority.ks files
     * @param randomNumberGenerator The RNG to user
     * @param maximumFailedAuthenticationAttempts maximum number of failed authentication attempts before card
     *        account is locked
     * @param maximumReplayOpportunityWindow The maximum allowable replay time
     * @throws Exception Thrown if there is a problem initialising the PINAuthority Simulator
     */
    public PinAuthoritySimulator(String signingSubjectKeyIdentifier, String configurationPath, SecureRandom randomNumberGenerator, int maximumFailedAuthenticationAttempts,
            long maximumReplayOpportunityWindow) throws Exception
    {
        super();

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

        // Initialise collection of card accounts
        setCardAccounts(new CardAccounts());

        // Store maximum failed authentication attempts
        setMaximumFailedAuthenticationAttempts(maximumFailedAuthenticationAttempts);

        // Store maximum replay opportunity window
        setMaximumReplayOpportunityWindow(maximumReplayOpportunityWindow);
    }

    /**
     * Register details of a card account with the pin authority
     * 
     * @param cardAccount the CardAccount to add
     * 
     * @throws DuplicateCardAccountException
     */
    public void addCardAccount (CardAccount cardAccount) throws DuplicateCardAccountException
    {
        getCardAccounts().addCardAccount(cardAccount);
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
    public PinRetrievalResponse process (String pinRetrievalRequest, PrimaryAccountNumber primaryAccountNumber) throws UnknownCardAccountException, CardHolderAuthenticationException,
            PinRetrievalRequestException, PinRetrievalResponseException, CardAccountLockedException
    {
        PinRetrievalResponse pinRetrievalResponse;
        Document pinRetrievalRequestDocument;

        pinRetrievalResponse = null;
        pinRetrievalRequestDocument = null;

        try
        {
            // Create the pinRetrievalRequestDocument from the pinRetrievalRequest String
            pinRetrievalRequestDocument = loadPinRetrievalRequestDocumentFromString(pinRetrievalRequest);
        }
        catch (Exception e)
        {
            getLogger().error("loading pin retrieval request document " + e.getMessage());

            throw new PinRetrievalRequestException("loading pin retrieval request document");
        }

        if (getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request document prior to processing
                getLogger().debug(new String(Utils.serialise(pinRetrievalRequestDocument)));
            }
            catch (Exception e)
            {
                getLogger().warn("serialising pin retrieval request document " + e.getMessage());
            }
        }

        // Process pin retrieval request document
        pinRetrievalResponse = processPinRetrievalRequest(pinRetrievalRequestDocument, primaryAccountNumber);

        return pinRetrievalResponse;
    }

    private Document loadPinRetrievalRequestDocumentFromString (String pinRetrievalRequestString) throws ParserConfigurationException, IOException, SAXException
    {
        Document pinRetrievalRequestDocument;
        byte[] decodedCompressedPinRetrievalRequest;
        byte[] decompressedPinRetrievalRequest;
        DocumentBuilder documentBuilder;
        ValidationResult parsingValidationResult;

        pinRetrievalRequestDocument = null;
        decodedCompressedPinRetrievalRequest = null;
        decompressedPinRetrievalRequest = null;
        documentBuilder = null;
        parsingValidationResult = null;

        // Decode compressed pin retrieval request
        decodedCompressedPinRetrievalRequest = UrlSafeBase64.decode(pinRetrievalRequestString);

        // Decompress pin retrieval request
        decompressedPinRetrievalRequest = Utils.decompress(decodedCompressedPinRetrievalRequest, ViewPinConstants.DECOMPRESSION_READ_BUFFER_LENGTH);

        // Instantiate safe parser
        documentBuilder = SafeParser.getInstance(true, false);

        // Register for any problems that may occur during the parsing process
        parsingValidationResult = new ValidationResult();
        documentBuilder.setErrorHandler(parsingValidationResult);

        // Load pin retrieval request document
        pinRetrievalRequestDocument = Utils.parseDocument(decompressedPinRetrievalRequest, documentBuilder);

        // Did we parse pin retrieval request document OK?
        if (parsingValidationResult.isValid() == false)
        {
            // Failed to parse pin retrieval response document OK
            getLogger().error("parsing pin retrieval request document" + parsingValidationResult.getException().getMessage());

            throw new IOException("parsing pin retrieval request document");
        }

        return pinRetrievalRequestDocument;
    }

    private PinRetrievalResponse processPinRetrievalRequest (Document pinRetrievalRequestDocument, PrimaryAccountNumber primaryAccountNumber) throws UnknownCardAccountException,
            CardHolderAuthenticationException, PinRetrievalRequestException, PinRetrievalResponseException, CardAccountLockedException
    {
        PinRetrievalResponse pinRetrievalResponse;
        CardAccount cardAccount;
        String sessionEncrytionMethodAlgorithm;
        SecretKey sessionKey;
        boolean cardHolderAuthentic;

        pinRetrievalResponse = null;
        cardAccount = null;
        sessionEncrytionMethodAlgorithm = null;
        sessionKey = null;
        cardHolderAuthentic = false;

        // Get card account associated with primary account number
        cardAccount = getCardAccounts().getCardAccount(primaryAccountNumber);

        // Did we find card account associated with primary account number?
        if (cardAccount == null)
        {
            // Failed to find card account associated with primary account number
            getLogger().error("finding card account associated with primary account number " + primaryAccountNumber);

            throw new UnknownCardAccountException();
        }

        // Ensure card account is not locked
        if (cardAccount.getAuthenticationState().isLocked() == true)
        {
            // Card account is locked
            throw new CardAccountLockedException();
        }

        // Validate pin retrieval request document prior to decryption
        if (validatePinRetrievalRequestDocumentPreDecryption(pinRetrievalRequestDocument) == false)
        {
            // Invalid pin retrieval request document prior to decryption
            getLogger().error("invalid pin retrieval request prior to decryption");

            throw new PinRetrievalRequestException("invalid pin retrieval request prior to decryption");
        }

        try
        {
            // Establish session encryption algorithm
            sessionEncrytionMethodAlgorithm = establishSessionEncryptionMethodAlgorithm(pinRetrievalRequestDocument);
        }
        catch (Exception e)
        {
            getLogger().error("establishing session encryption algorithm " + e.getMessage());

            throw new PinRetrievalRequestException("establishing session encryption algorithm");
        }

        getLogger().debug("session encryption algorithm = " + sessionEncrytionMethodAlgorithm);

        try
        {
            // Recover session key
            sessionKey = XmlEncryption.recoverSessionKey(pinRetrievalRequestDocument, sessionEncrytionMethodAlgorithm, getAuthorityKeyStore());
        }
        catch (Exception e)
        {
            getLogger().error("recovering session key " + e.getMessage());

            throw new PinRetrievalRequestException("recovering session key");
        }

        // Did we recover session key OK?
        if (sessionKey == null)
        {
            getLogger().error("recovering session key");

            throw new PinRetrievalRequestException("recovering session key");
        }

        try
        {
            // Process EncryptedData elements
            processEncryptedData(pinRetrievalRequestDocument, sessionKey);
        }
        catch (Exception e)
        {
            getLogger().error("processing encrypted data " + e.getMessage());

            throw new PinRetrievalRequestException("processing encrypted data");
        }

        if (getLogger().isDebugEnabled() == true)
        {
            try
            {
                // Output pin retrieval request post decryption
                getLogger().debug(new String(Utils.serialise(pinRetrievalRequestDocument)));
            }
            catch (Exception e)
            {
                getLogger().warn("serialising pin retrieval request document post decryption " + e.getMessage());
            }
        }

        // Validate pin retrieval request post decryption
        if (validatePinRetrievalRequestDocumentPostDecryption(pinRetrievalRequestDocument) == false)
        {
            // Failed to validate pin retrieval request post decryption
            getLogger().error("invalid pin retrieval request post decryption");

            throw new PinRetrievalRequestException("invalid pin retrieval request post decryption");
        }

        getLogger().debug("found card account for primary account number " + primaryAccountNumber);

        try
        {
            // Authentic card holder
            cardHolderAuthentic = authenticateCardHolder(cardAccount, pinRetrievalRequestDocument);
        }
        catch (Exception e)
        {
            getLogger().error("authenticating card holder " + e.getMessage());

            throw new PinRetrievalRequestException("authenticating card holder");
        }

        // Did we authentic card holder OK?
        if (cardHolderAuthentic == false)
        {
            // Card holder failed authentication
            getLogger().warn("card holder " + cardAccount.getPrimaryAccountNumber() + " failed authentication");

            // Register failed authentication
            cardAccount.getAuthenticationState().registerFailedAuthencation();

            getLogger()
                    .debug("total failed authentication attempts for card holder " + cardAccount.getPrimaryAccountNumber() + " = " + cardAccount.getAuthenticationState().getFailedAuthentications());

            // Ensure maximum failed authentication attempts has not been reached
            if (cardAccount.getAuthenticationState().getFailedAuthentications() >= getMaximumFailedAuthenticationAttempts())
            {
                // Maximum failed authentication attempts has been reached, lockout card account
                cardAccount.getAuthenticationState().setLocked(true);

                getLogger().warn("card holder " + cardAccount.getPrimaryAccountNumber() + " has reached maximum failed authentication attempts, locking out");

                throw new CardAccountLockedException();
            }

            throw new CardHolderAuthenticationException();
        }

        getLogger().info("card holder " + cardAccount.getPrimaryAccountNumber() + " successfully authenticated");

        // Register successful authentication
        cardAccount.getAuthenticationState().registerSuccessfulAuthentication();

        
        String signatureMethodAlgorithm = XmlSignatureMethodAlgorithms.SIGNATURE_METHOD_ALGORITHM_RSA_SHA256;
        
        String digestMethodAlgorithm = XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA256;

        // Generate pin retrieval response
        pinRetrievalResponse = getPinRetrievalResponseGenerator().generatePinRetrievalResponse(pinRetrievalRequestDocument, sessionKey, sessionEncrytionMethodAlgorithm, cardAccount,
                signatureMethodAlgorithm, digestMethodAlgorithm, getRandomNumberGenerator());

        return pinRetrievalResponse;
    }

    private boolean validatePinRetrievalRequestDocumentPreDecryption (Document pinRetrievalRequestDocument) throws PinRetrievalRequestException
    {
        boolean valid;
        ValidationResult validationResult;

        valid = false;
        validationResult = null;

        try
        {
            // Validate pin retrieval request against pre-decryption schema
            validationResult = Utils.validateDocument(pinRetrievalRequestDocument, getPreDecryptionSchema());
        }
        catch (Exception e)
        {
            getLogger().error("pre-decryption schema validation processing " + e.getMessage());

            throw new PinRetrievalRequestException("pre-decryption schema validation processing");
        }

        // Did we validate pin retrieval request OK?
        if (validationResult.isValid() == false)
        {
            // Failed to validate pin retrieval request against pre-decryption schema
            getLogger().error("validating pin retrieval request document against pre-decryption schema", validationResult.getException());

            valid = false;

            return valid;
        }

        getLogger().debug("validated pin retrieval request document against pre-decryption schema OK");

        try
        {
            // Verify pin retrieval request document signature
            if (XmlSignature.verifySignature(pinRetrievalRequestDocument, getAgentKeyStore()) == false)
            {
                getLogger().error("pin retrieval request signature verification failed");

                valid = false;

                return valid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("signature verification processing " + e.getMessage());

            throw new PinRetrievalRequestException("signature verification processing");
        }

        getLogger().debug("pin retrieval request signature verified OK");

        try
        {
            // Validate time stamp
            if (validateTimestamp(pinRetrievalRequestDocument) == false)
            {
                getLogger().warn("pin retrieval request outside replay opportunity window");

                valid = false;

                return valid;
            }
        }
        catch (Exception e)
        {
            getLogger().error("timestamp validation processing " + e.getMessage());

            throw new PinRetrievalRequestException("timestamp validation processing");
        }

        valid = true;

        return valid;
    }

    private boolean validatePinRetrievalRequestDocumentPostDecryption (Document pinRetrievalRequestDocument) throws PinRetrievalRequestException
    {
        boolean valid;
        ValidationResult validationResult;

        valid = false;
        validationResult = null;

        try
        {
            // Validate pin retrieval request against post decryption schema
            validationResult = Utils.validateDocument(pinRetrievalRequestDocument, getPostDecryptionSchema());
        }
        catch (Exception e)
        {
            getLogger().error("validating pin retrieval request against post decryption schema");

            throw new PinRetrievalRequestException("validating pin retrieval request against post decryption schema");
        }

        // Did we validate pin retrieval request against post decryption schema OK?
        if (validationResult.isValid() == false)
        {
            // Failed to validate pin retrieval request against post decryption schema
            getLogger().error("failed to validate pin retrieval request against post decryption schema", validationResult.getException());

            validationResult.getException().printStackTrace();

            valid = false;

            return valid;
        }

        valid = true;

        return valid;
    }

    private boolean validateTimestamp (Document pinRetrievalRequestDocument) throws XPathExpressionException, ParseException, XPathFactoryConfigurationException
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
        timestamp = getTimestamp(pinRetrievalRequestDocument);

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

    private Date getTimestamp (Document pinRetrievalRequestDocument) throws XPathFactoryConfigurationException, XPathExpressionException, ParseException
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
        unparsedTimestamp = (String) xp.evaluate("/vp:PinRetrievalRequest/vp:timestamp/text()", pinRetrievalRequestDocument, XPathConstants.STRING);

        // Parse time stamp
        parsedTimestamp = Utils.parseDateTime(unparsedTimestamp);

        return parsedTimestamp;
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

        getLogger().debug("verification value = " + verificationValue);

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

            getLogger().debug("expiry date = " + decryptedExpiryDateMonth + "/" + decryptedExpiryDateYear);
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

            getLogger().debug("primary account number = " + primaryAccountNumber);
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

    private static Logger getLogger ()
    {
        return logger;
    }

    private void setRandomNumberGenerator (SecureRandom randomNumberGenerator)
    {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    private SecureRandom getRandomNumberGenerator ()
    {
        return this.randomNumberGenerator;
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

    private void setCardAccounts (CardAccounts cardAccounts)
    {
        this.cardAccounts = cardAccounts;
    }

    private CardAccounts getCardAccounts ()
    {
        return this.cardAccounts;
    }

    private boolean authenticateCardHolder (CardAccount cardAccount, Document pinRetrievalRequestDocument) throws XPathFactoryConfigurationException, XPathExpressionException,
            InvalidExpiryDateException
    {
        boolean cardHolderAuthentic;

        cardHolderAuthentic = false;

        // Authenticate card holder verification value
        if (authenticateCardHolderVerificationValue(cardAccount, pinRetrievalRequestDocument) == false)
        {
            // Card holder failed verification value authentication
            getLogger().warn("card holder failed verification value authentication");

            cardHolderAuthentic = false;

            return cardHolderAuthentic;
        }

        if (cardAccount.isExpiryDateAuthenticationToBeEnforced() == true)
        {
            getLogger().debug("expiry date authentication is to be enforced");

            // Authenticate expiry date
            if (authenticateExpiryDate(cardAccount, pinRetrievalRequestDocument) == false)
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

    private boolean authenticateCardHolderVerificationValue (CardAccount cardAccount, Document pinRetrievalRequestDocument) throws XPathFactoryConfigurationException, XPathExpressionException
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
        verificationValue = (String) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue", pinRetrievalRequestDocument, XPathConstants.STRING);

        getLogger().debug("verification value extracted from pin retrieval request = " + verificationValue);

        getLogger().debug("verification value of card holder with primary account number " + cardAccount.getPrimaryAccountNumber() + " = " + cardAccount.getCardHolderVerificationValue());

        // Does verification value extracted from pin retrieval request match authentic value?
        if (cardAccount.getCardHolderVerificationValue().equalsIgnoreCase(verificationValue) == true)
        {
            // Verification value extracted from pin retrieval request does match authentic value
            authenticVerificationValue = true;
        }
        else
        {
            // Verification value extracted from pin retrieval request does not match authentic value
            getLogger().warn(
                    "expecting verification value " + cardAccount.getCardHolderVerificationValue() + " got " + verificationValue + " for primary account number "
                            + cardAccount.getPrimaryAccountNumber());
        }

        return authenticVerificationValue;
    }

    private boolean authenticateExpiryDate (CardAccount cardAccount, Document pinRetrievalRequestDocument) throws XPathFactoryConfigurationException, XPathExpressionException,
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
        expiryDateElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:ExpiryDate", pinRetrievalRequestDocument, XPathConstants.NODE);

        // Did we get ExpiryDate element OK?
        if (expiryDateElement == null)
        {
            // Failed to get ExpiryDate element
            getLogger().warn("ExpiryDate element expected but not found for primary account number " + cardAccount.getPrimaryAccountNumber());

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
        if (expiryDate.equals(cardAccount.getExpiryDate()) == true)
        {
            // Expiry date submitted by card holder in pin retrieval request is authentic
            expiryDateAuthentic = true;
        }
        else
        {
            // Expiry date submitted by card holder in pin retrieval request is not authentic
            getLogger().warn("expiry date submitted by card holder " + expiryDate + " does not match authentic value " + cardAccount.getExpiryDate());

            expiryDateAuthentic = false;
        }

        return expiryDateAuthentic;
    }

    private String establishSessionEncryptionMethodAlgorithm (Document pinRetrievalRequestDocument) throws XPathFactoryConfigurationException, XPathExpressionException
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
        verificationValueEncryptedDataElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/vp:CardHolderVerification/vp:VerificationValue/xenc:EncryptedData", pinRetrievalRequestDocument,
                XPathConstants.NODE);

        // Get encryption method algorithm used to encrypt verification value encrypted data element
        encrytionMethodAlgorithm = (String) xp.evaluate("xenc:EncryptionMethod/@Algorithm", verificationValueEncryptedDataElement, XPathConstants.STRING);

        return encrytionMethodAlgorithm;
    }

    private void setMaximumFailedAuthenticationAttempts (int maximumFailedAuthenticationAttempts)
    {
        this.maximumFailedAuthenticationAttempts = maximumFailedAuthenticationAttempts;
    }

    private int getMaximumFailedAuthenticationAttempts ()
    {
        return this.maximumFailedAuthenticationAttempts;
    }

    private void setMaximumReplayOpportunityWindow (long maximumReplayOpportunityWindow)
    {
        this.maximumReplayOpportunityWindow = maximumReplayOpportunityWindow;
    }

    private long getMaximumReplayOpportunityWindow ()
    {
        return this.maximumReplayOpportunityWindow;
    }
}