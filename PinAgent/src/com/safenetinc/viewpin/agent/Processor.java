// $Id: PinAgent/src/com/safenetinc/viewpin/agent/Processor.java 1.12 2012/07/30 13:51:59IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.xml.security.Init;

import com.safenetinc.viewpin.agent.exceptions.AgentInitException;
import com.safenetinc.viewpin.agent.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.agent.otp.OneTimePadCipher;
import com.safenetinc.viewpin.agent.otp.PadEncryptedPin;
import com.safenetinc.viewpin.agent.otp.PadEncryptedPins;
import com.safenetinc.viewpin.agent.otp.PinOneTimePadKeyGenerator;
import com.safenetinc.viewpin.agent.sessionkey.PaddingScheme;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedPaddingSchemeException;
import com.safenetinc.viewpin.common.datastructures.CardHolderVerification;
import com.safenetinc.viewpin.common.datastructures.PinChangeData;
import com.safenetinc.viewpin.common.datastructures.CardHolderVerificationValue;
import com.safenetinc.viewpin.common.datastructures.CardPin;
import com.safenetinc.viewpin.common.datastructures.ExpiryDate;
import com.safenetinc.viewpin.common.datastructures.PrimaryAccountNumber;
import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidCardHolderVerificationValueException;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidExpiryDateException;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidCardPinException;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidPrimaryAccountNumberException;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidSubjectKeyIdentifierException;
import com.safenetinc.viewpin.common.utils.CookieUtils;
import com.safenetinc.viewpin.common.utils.EntropyPool;
import com.safenetinc.viewpin.common.utils.UniversallyUniqueIdentifierGenerator;
import com.safenetinc.viewpin.common.utils.Utils;
import com.safenetinc.viewpin.common.utils.XMLUtils;

/**
 * 
 * @author Stuart Horler
 *
 *
 */
public class Processor extends HttpServlet
{
    private static final long  serialVersionUID                  = 42L;

    private Logger      logger                            = Logger.getLogger(Processor.class);

    private URL                errorRedirectionUrl               = null;

    private CookieDetails      wrappedSessionKeyCookieDetails    = null;

    private CookieDetails      pinRetrievalRequestCookieDetails  = null;

    private CookieDetails      pinRetrievalResponseCookieDetails = null;
    
    private CookieDetails      pinChangeRequestCookieDetails	 = null;

    private transient PinAgent pinAgent                          = null;

    /** 
     * init servlet initialization method
     * 
     * @see javax.servlet.GenericServlet#init()
     * 
     * @throws ServletException 
     */
    @Override
	public void init() throws ServletException
    {
        super.init();

        try
        {
            // Initialise logging
            initLogging();

            getLogger().info("pin agent version = " + ViewPinConstants.VERSION);

            // Initialise Apache XML security
            Init.init();

            // Initialise agent
            initAgent();
        }
        catch (Throwable t)
        {
            if (t.getMessage() != null)
            {
                getLogger().fatal(t.getMessage());
            }

            getLogger().fatal("intialisation failure");

            throw new ServletException();
        }
    }

    /**
     * Initialises the PINAgent servlet
     * 
     * @throws AgentInitException
     */
    private void initAgent () throws AgentInitException
    {
        File agentConfigurationFile;
        XMLConfiguration agentConfiguration;
        SubjectKeyIdentifier agentSigningCertificateSubjectKeyIdentifier;
        String signatureMethodAlgorithm;
        SubjectKeyIdentifier agentWrappingCertificateSubjectKeyIdentifier;
        PaddingScheme agentWrappingPaddingScheme;
        String errorRedirectionUrlConfigurationParameter;
        Long replayWindow;
        String digestMethodAlgorithm;

        agentConfigurationFile = null;
        agentConfiguration = null;
        agentSigningCertificateSubjectKeyIdentifier = null;
        signatureMethodAlgorithm = null;
        agentWrappingCertificateSubjectKeyIdentifier = null;
        agentWrappingPaddingScheme = null;
        errorRedirectionUrlConfigurationParameter = null;
        digestMethodAlgorithm = null;

        // Open agent configuration file
        agentConfigurationFile = ConfigurationReader.openConfigurationFile(ViewPinConstants.AGENT_CONFIGURATION_FILE, getLogger());

        // Parse agent configuration
        agentConfiguration = ConfigurationReader.parseConfigurationFile(agentConfigurationFile, getLogger());

        // Ensure exceptions are thrown for missing elements
        agentConfiguration.setThrowExceptionOnMissing(true);

        try
        {
            // Instantiate agent signing certificate subject key identifer
            agentSigningCertificateSubjectKeyIdentifier = new SubjectKeyIdentifier(ConfigurationReader.readConfigurationString(agentConfiguration, "SigningCertificate.SubjectKeyIdentifier",
                    getLogger()));
        }
        catch (InvalidSubjectKeyIdentifierException iskie)
        {
            getLogger().fatal("invalid agent signing certificate subject key identifier " + iskie.getMessage());

            throw new AgentInitException();
        }

        // Get signature method algorithm
        signatureMethodAlgorithm = ConfigurationReader.readConfigurationString(agentConfiguration, "SignatureMethodAlgorithm", getLogger());

        getLogger().info("signature method algorithm = " + signatureMethodAlgorithm);

        try
        {
            // Instantiate agent wrapping certificate subject key identifier
            agentWrappingCertificateSubjectKeyIdentifier = new SubjectKeyIdentifier(ConfigurationReader.readConfigurationString(agentConfiguration, "WrappingCertificate.SubjectKeyIdentifier",
                    getLogger()));
        }
        catch (InvalidSubjectKeyIdentifierException iskie)
        {
            getLogger().fatal("invalid agent wrapping certificate subject key identifier " + iskie.getMessage());

            throw new AgentInitException();
        }

        try
        {
            // Instantiate agent wrapping padding scheme
            agentWrappingPaddingScheme = PaddingScheme.getInstance(PaddingScheme.PADDING_SCHEME_OAEP);
        }
        catch (UnsupportedPaddingSchemeException upse)
        {
            // Agent wrapping padding scheme is invalid
            getLogger().fatal("agent wrapping padding scheme is invalid");

            throw new AgentInitException();
        }

        // Get error redirection URL
        errorRedirectionUrlConfigurationParameter = ConfigurationReader.readConfigurationString(agentConfiguration, "ErrorRedirectionUrl", getLogger());

        // Did we get error redirection URL OK?
        if(errorRedirectionUrlConfigurationParameter == null)
        {
            // Failed to get error redirection URL
            getLogger().fatal("retrieving error redirection URL");

            throw new AgentInitException();
        }

        try
        {
            // Parse error redirection URL
            setErrorRedirectionUrl(new URL(errorRedirectionUrlConfigurationParameter));
        }
        catch (MalformedURLException mue)
        {
            // Failed to parse error redirection URL
            getLogger().fatal("parsing error redirection URL");

            throw new AgentInitException();
        }

        getLogger().info("error redirection URL = " + getErrorRedirectionUrl().toExternalForm());

        // Store wrapped session key issued cookie
        setWrappedSessionKeyCookieDetails(readIssuedCookieConfiguration(agentConfiguration, "WrappedSessionKeyCookie"));

        getLogger().info("wrapped session key cookie name = " + getWrappedSessionKeyCookieDetails().getName());
        getLogger().info("wrapped session key cookie domain = " + getWrappedSessionKeyCookieDetails().getDomain());
        getLogger().info("wrapped session key cookie path = " + getWrappedSessionKeyCookieDetails().getPath());

        // Store pin retrieval request issued cookie
        setPinRetrievalRequestCookieDetails(readIssuedCookieConfiguration(agentConfiguration, "PinRetrievalRequestCookie"));

        getLogger().info("pin retrieval request cookie name = " + getPinRetrievalRequestCookieDetails().getName());
        getLogger().info("pin retrieval request cookie domain = " + getPinRetrievalRequestCookieDetails().getDomain());
        getLogger().info("pin retrieval request cookie path = " + getPinRetrievalRequestCookieDetails().getPath());
       
        // Store pin retrieval request issued cookie
        setPinChangeRequestCookieDetails(readIssuedCookieConfiguration(agentConfiguration, "PinChangeRequestCookie"));

        getLogger().info("pin change request cookie name = " + getPinChangeRequestCookieDetails().getName());
        getLogger().info("pin change request cookie domain = " + getPinChangeRequestCookieDetails().getDomain());
        getLogger().info("pin change request cookie path = " + getPinChangeRequestCookieDetails().getPath());
       
        // Store pin retrieval response issued cookie
        setPinRetrievalResponseCookieDetails(readIssuedCookieConfiguration(agentConfiguration, "PinRetrievalResponseCookie"));

        getLogger().info("pin retrieval response cookie name = " + getPinRetrievalResponseCookieDetails().getName());
        getLogger().info("pin retrieval response cookie domain = " + getPinRetrievalResponseCookieDetails().getDomain());
        getLogger().info("pin retrieval response cookie path = " + getPinRetrievalResponseCookieDetails().getPath());

        // Get replay window
        replayWindow = ConfigurationReader.readConfigurationLong(agentConfiguration, "ReplayWindow", getLogger());

        // Did we get replay window OK?
        if (replayWindow == null)
        {
            // Failed to get replay window
            getLogger().fatal("retrieving replay window");

            throw new AgentInitException();
        }

        // Ensure replay window is sensible
        if (replayWindow.longValue() < ViewPinConstants.MINIMUM_SENSIBLE_REPLAY_WINDOW || replayWindow.longValue() > ViewPinConstants.MAXIMUM_SENSIBLE_REPLAY_WINDOW)
        {
            // Replay window is not sensible
            getLogger().fatal("replay window should be within " + ViewPinConstants.MINIMUM_SENSIBLE_REPLAY_WINDOW + " and " + ViewPinConstants.MAXIMUM_SENSIBLE_REPLAY_WINDOW);

            throw new AgentInitException();
        }

        getLogger().info("replay window = " + replayWindow);

        // Get digest method algorithm
        digestMethodAlgorithm = ConfigurationReader.readConfigurationString(agentConfiguration, "DigestMethodAlgorithm", getLogger());

        getLogger().info("digest method algorithm = " + digestMethodAlgorithm);

        // Instantiate and store reference to pin agent
        setPinAgent(new PinAgent(agentSigningCertificateSubjectKeyIdentifier, signatureMethodAlgorithm, agentWrappingCertificateSubjectKeyIdentifier, agentWrappingPaddingScheme, replayWindow
                .longValue(), digestMethodAlgorithm));

        // Initialise authorities
        initAuthorities(agentConfiguration);
    }

    /**
     * Method to read details about cookies that the agent is to set
     * 
     * @param agentConfiguration The configuration to read from
     * @param issuedCookieNameKey The name of the cookie in question
     * @return The {@link CookieDetails} object
     * @throws AgentInitException
     */
    private CookieDetails readIssuedCookieConfiguration (XMLConfiguration agentConfiguration, String issuedCookieNameKey) throws AgentInitException
    {
        CookieDetails issuedCookie;
        String name;
        String domain;
        String path;

        issuedCookie = null;
        name = null;
        domain = null;
        path = null;

        // Read issued cookie name
        name = ConfigurationReader.readConfigurationString(agentConfiguration, issuedCookieNameKey + ".Name", getLogger());

        // Validate issued cookie name
        if (isValidCookieName(name) == false)
        {
            // Issued cookie name is not valid
            getLogger().fatal("issued cookie name " + name + " is not valid");

            throw new AgentInitException();
        }

        // Get issued cookie domain
        domain = ConfigurationReader.readConfigurationString(agentConfiguration, issuedCookieNameKey + ".Domain", getLogger());

        // Did we get issued cookie domain OK?
        if (domain == null)
        {
            // Failed to get issued cookie domain
            getLogger().fatal("retrieving issued cookie name " + name + " domain");

            throw new AgentInitException();
        }

        // Get issued cookie path
        path = ConfigurationReader.readConfigurationString(agentConfiguration, issuedCookieNameKey + ".Path", getLogger());

        // Did we get issued cookie path OK?
        if (path == null)
        {
            // Failed to issued cookie path
            getLogger().fatal("retrieving issued cookie " + name + " path");

            throw new AgentInitException();
        }

        // Instantiate issued cookie object
        issuedCookie = new CookieDetails(name, domain, path);

        return issuedCookie;
    }

    /**
     * 
     * @param configuration
     * @throws AgentInitException
     */
    @SuppressWarnings("unchecked")
    private void initAuthorities (XMLConfiguration configuration) throws AgentInitException
    {
        List<String> authorityNames;
        String nextAuthorityName;
        String nextAuthoritySigningCertificateSubjectKeyIdentifier;
        String nextAuthorityWrappingCertificateSubjectKeyIdentifier;
        String nextAuthorityWrappingPaddingScheme;
        String nextAuthoritySessionKeyAlgorithmName;
        Integer nextAuthoritySessionKeyBitLength;
        URL nextAuthorityRedirectionUrl = null;

        authorityNames = null;
        nextAuthorityName = null;
        nextAuthoritySigningCertificateSubjectKeyIdentifier = null;
        nextAuthorityWrappingCertificateSubjectKeyIdentifier = null;
        nextAuthorityWrappingPaddingScheme = null;
        nextAuthoritySessionKeyAlgorithmName = null;
        nextAuthoritySessionKeyBitLength = null;

        // Get authorites from configuration
        authorityNames = configuration.getList("PinAuthorities.PinAuthority.Name");

        // Process authorities from configuration
        for (int i = 0; i < authorityNames.size(); i++)
        {
            // Get next authority from configuration
            nextAuthorityName = authorityNames.get(i);
            nextAuthoritySigningCertificateSubjectKeyIdentifier = ConfigurationReader.readConfigurationString(configuration, "PinAuthorities.PinAuthority(" + i
                    + ").SigningCertificate.SubjectKeyIdentifier", getLogger());
            nextAuthorityWrappingCertificateSubjectKeyIdentifier = ConfigurationReader.readConfigurationString(configuration, "PinAuthorities.PinAuthority(" + i
                    + ").WrappingCertificate.SubjectKeyIdentifier", getLogger());
            nextAuthorityWrappingPaddingScheme = ConfigurationReader.readConfigurationString(configuration, "PinAuthorities.PinAuthority(" + i + ").WrappingPaddingScheme", getLogger());
            nextAuthoritySessionKeyAlgorithmName = ConfigurationReader.readConfigurationString(configuration, "PinAuthorities.PinAuthority(" + i + ").SessionKey.AlgorithmName", getLogger());
            nextAuthoritySessionKeyBitLength = ConfigurationReader.readConfigurationInteger(configuration, "PinAuthorities.PinAuthority(" + i + ").SessionKey.BitLength", getLogger());

            try
            {
                // Get redirection URL
                nextAuthorityRedirectionUrl = new URL(ConfigurationReader.readConfigurationString(configuration, "PinAuthorities.PinAuthority(" + i + ").RedirectionUrl", getLogger()));
            }
            catch (MalformedURLException mue)
            {
                // Failed to retrieve redirection URL
                getLogger().fatal("retrieving redirection URL");

                throw new AgentInitException();
            }

            getLogger().info("authority name = " + nextAuthorityName);
            getLogger().info("authority signing certificate subject key identifier = " + nextAuthoritySigningCertificateSubjectKeyIdentifier);
            getLogger().info("authoriry wrapping certificate subject key identifier = " + nextAuthorityWrappingCertificateSubjectKeyIdentifier);
            getLogger().info("authority wrapping padding scheme = " + nextAuthorityWrappingPaddingScheme);
            getLogger().info("authority session key algotihm name = " + nextAuthoritySessionKeyAlgorithmName);
            getLogger().info("authority session key bit length = " + nextAuthoritySessionKeyBitLength);
            getLogger().info("authority redirection url = " + nextAuthorityRedirectionUrl);

            // Add next pin authority
            getPinAgent().addPinAuthority(nextAuthorityName, nextAuthoritySigningCertificateSubjectKeyIdentifier, nextAuthorityWrappingCertificateSubjectKeyIdentifier,
                    nextAuthorityWrappingPaddingScheme, nextAuthoritySessionKeyAlgorithmName, nextAuthoritySessionKeyBitLength.intValue(), nextAuthorityRedirectionUrl);
        }
    }

    @Override
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UUID transactionIdentifier;
        String requestTypeStr;
        int requestType;
        
        transactionIdentifier = null;
        requestTypeStr=null;
        requestType=0;
        
        getLogger().info("received post request");

        try
        {
            // Prepare request
            enterRequestContext(request);

            // Generate transaction identifier
            transactionIdentifier = UniversallyUniqueIdentifierGenerator.generateVersionFour();

            //Get value of the requestType
            requestTypeStr=request.getParameter(ViewPinConstants.REQUEST_TYPE);
            if(requestTypeStr!=null)
            	requestType=Integer.parseInt(requestTypeStr);
            else
            {
            	getLogger().error("request type not found");
            	redirectOnFailure(response);
            }
            	
            
            
                       
           // getLogger().debug("generated new transaction identifier = " + transactionIdentifier.toString());

            // Make transaction identifier available to all code in this thread
            MDC.put("transactionIdentifier", transactionIdentifier.toString());

            if(ViewPinConstants.PIN_VIEW_REQUEST == requestType)
            {
            	// Request pin Retrieval
            	requestPinRetrieval(request, response);
            }
            else if(ViewPinConstants.PIN_CHANGE_REQUEST == requestType)
            {
            	//Request pin Change
            	requestPinChange(request, response);
            }
            else
            {
            	getLogger().error("request type not identified");
            	redirectOnFailure(response);
            }
            getLogger().debug("remaining bytes in entropy pool = " + EntropyPool.getBytesRemaining());
            
            }
        catch (Throwable t)
        {
            String exceptionMessage = null;

            exceptionMessage = "unexpected exception occured whilst requesting pin";

            if (t.getMessage() != null)
            {
                exceptionMessage = exceptionMessage + " " + t.getMessage();
            }

            getLogger().fatal(exceptionMessage);
			t.printStackTrace();
            redirectOnFailure(response);
        }
        finally
        {
            // Clear up after request
            leaveRequestContext();
        }

        getLogger().info("processed post request");
    }

    private void enterRequestContext (HttpServletRequest request) throws NoSuchAlgorithmException
    {
        UUID loggingIdentifier;
        String clientIpAddress;

        loggingIdentifier = null;
        clientIpAddress = null;

        // Clear MDC before we start
        if (MDC.getContext() != null)
        {
            MDC.getContext().clear();
        }

        // Generate logging identifier
        loggingIdentifier = UniversallyUniqueIdentifierGenerator.generateVersionFour();

        // Make logging identifier available to all code in this thread
        MDC.put("loggingIdentifier", loggingIdentifier.toString());

        // Make hostname available to this thread
        MDC.put("hostname", request.getServerName());

        // Get IP address of client
        clientIpAddress = request.getRemoteAddr();

        // Make client IP address available to all code in this thread
        MDC.put("clientIpAddress", clientIpAddress);
    }

    private void leaveRequestContext ()
    {
        // Clear MDC before we leave
        if (MDC.getContext() != null)
        {
            MDC.getContext().clear();
        }
    }

    @Override
	protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        getLogger().info("received get request");

        try
        {
            // Prepare request
            enterRequestContext(request);

            // Process pin retrieval response
            processPinRetrievalResponse(request, response);
        }
        catch (Throwable t)
        {
            getLogger().fatal("unexpected exception " + t.getMessage());

            redirectOnFailure(response);
        }
        finally
        {
            // Clear up after request
            leaveRequestContext();
        }

        getLogger().info("processed get request");
    }

    private void processPinRetrievalResponse (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Cookie pinRetrievalResponseCookie;
        Cookie wrappedSessionKeyCookie;
        ArrayList<CardPin> cardPins;
        PadEncryptedPins padEncryptedPins;
        CardPin nextCardPin;
        String nextOneTimePadKey;
        String nextOneTimePadKeyCookieName;
        String nextEncryptedPin;
        PadEncryptedPin nextPadEncryptedPin;
        Cookie nextOneTimePadKeyCookie;

        pinRetrievalResponseCookie = null;
        wrappedSessionKeyCookie = null;
        cardPins = null;
        padEncryptedPins = null;
        nextCardPin = null;
        nextOneTimePadKey = null;
        nextOneTimePadKeyCookieName = null;
        nextEncryptedPin = null;
        nextPadEncryptedPin = null;
        nextOneTimePadKeyCookie = null;

        getLogger().debug("pin retrieval response cookie name = " + getPinRetrievalResponseCookieDetails().getName());

        // Get pin retrieval response cookie
        pinRetrievalResponseCookie = CookieUtils.getCookieByName(request, getPinRetrievalResponseCookieDetails().getName());

        // Did we get pin retrieval response cookie OK?
        if (pinRetrievalResponseCookie == null)
        {
            // Failed to get pin retrieval response cookie
            getLogger().warn("pin retrieval response cookie " + getPinRetrievalResponseCookieDetails().getName() + " not found");

            redirectOnFailure(response);

            return;
        }

        // Get wrapped session key cookie
        wrappedSessionKeyCookie = CookieUtils.getCookieByName(request, getWrappedSessionKeyCookieDetails().getName());

        // Did we get wrapped session key cookie OK?
        if (wrappedSessionKeyCookie == null)
        {
            // Failed to get wrapped session key cookie
            getLogger().warn("retrieving wrapped session key cookie");

            redirectOnFailure(response);

            return;
        }

		
		
		/*if(pinRetrievalResponseCookie.getDomain().compareToIgnoreCase(getPinRetrievalResponseCookieDetails().getDomain()) !=0)
		{
			getLogger().error("processing pin retrieval response domain is not same as configured domain");

            redirectOnFailure(response);

            return;
		}
		/*
		if(wrappedSessionKeyCookie.getDomain().compareToIgnoreCase(getWrappedSessionKeyCookieDetails().getDomain()) !=0)
		{
			getLogger().error("processing pin retrieval response wrapped Session Key domain is not same as configured domain");

            redirectOnFailure(response);

            return;
		}*/

        try
        {
            // Process pin retrieval response
            cardPins = getPinAgent().processPinRetrievalResponse(pinRetrievalResponseCookie.getValue(), wrappedSessionKeyCookie.getValue());
        }
        catch (Exception e)
        {
            getLogger().error("processing pin retrieval response " + e.getMessage());

            redirectOnFailure(response);

            return;
        }

        // Instantiate container to hold one time pad encrypted pin numbers
        padEncryptedPins = new PadEncryptedPins(getPinRetrievalRequestCookieDetails().getDomain());

        // Process each card pin returned by pin authority
        for (int i = 0; i < cardPins.size(); i++)
        {
            // Get next card pin
            nextCardPin = cardPins.get(i);

            try
            {
                // Generate one time pad key that will be used to encrypt pin number
                nextOneTimePadKey = PinOneTimePadKeyGenerator.generateKey(nextCardPin.getPin().length());
            }
            catch (NoSuchAlgorithmException nsae)
            {
                getLogger().error("generating one time pad key " + nsae.getMessage());

                redirectOnFailure(response);

                return;
            }

            try
            {
                // Generate random name for the cookie that is to hold one time pad key
                nextOneTimePadKeyCookieName = generateOneTimePadKeyCookieName();
            }
            catch (NoSuchAlgorithmException nsae)
            {
                getLogger().error("generating random one time pad key cookie name " + nsae.getMessage());

                redirectOnFailure(response);

                return;
            }

            try
            {
                // One time pad encrypt pin
                nextEncryptedPin = OneTimePadCipher.encrypt(nextCardPin.getPin(), nextOneTimePadKey);
            }
            catch (InvalidKeyException ike)
            {
                getLogger().error("one time pad encrypting pin " + ike.getMessage());

                redirectOnFailure(response);

                return;
            }

            // Store next encrypted pin along with its cookie name
            nextPadEncryptedPin = new PadEncryptedPin(nextEncryptedPin, nextOneTimePadKeyCookieName);
            padEncryptedPins.add(nextPadEncryptedPin);

            // Issue next one time pad key cookie
            nextOneTimePadKeyCookie = CookieUtils.createCookieForData(nextOneTimePadKeyCookieName, nextOneTimePadKey, getPinRetrievalRequestCookieDetails().getDomain(), ViewPinConstants.ONE_TIME_PAD_COOKIE_PATH);
                                                                                                                                                                                
                                                                                                                                                                                
            nextOneTimePadKeyCookie.setSecure(true);
            response.addCookie(nextOneTimePadKeyCookie);
        }
        
        // Ensure session only remains valid for a short period of time
        request.getSession().setMaxInactiveInterval(ViewPinConstants.SESSION_INVALID_AFTER);

        // Make one time pad encrypted pin numbers available to the JSP that will display them
        request.setAttribute("com.safenetinc.viewpin.agent.otp.PadEncryptedPins", padEncryptedPins);

        // Clean up cookies that are no longer needed
        response.addCookie(CookieUtils
                .deleteCookieByName(getWrappedSessionKeyCookieDetails().getName(), getWrappedSessionKeyCookieDetails().getDomain(), getWrappedSessionKeyCookieDetails().getPath()));
        response.addCookie(CookieUtils.deleteCookieByName(getPinRetrievalResponseCookieDetails().getName(), getPinRetrievalResponseCookieDetails().getDomain(), getPinRetrievalResponseCookieDetails()
                .getPath()));
        response.addCookie(CookieUtils.deleteCookieByName(getPinRetrievalRequestCookieDetails().getName(), getPinRetrievalRequestCookieDetails().getDomain(), getPinRetrievalRequestCookieDetails()
                .getPath()));

        // Pass control to JSP that controls display of pin numbers
        getServletConfig().getServletContext().getRequestDispatcher("/showPin.jsp").forward(request, response);
    }

    private void requestPinRetrieval (HttpServletRequest request, HttpServletResponse response)
    {
        String cardHolderVerificationValueParameter;
        CardHolderVerificationValue cardHolderVerificationValue;
        String expiryDateMonthParameter;
        String expiryDateYearParameter;
        ExpiryDate expiryDate;
        String primaryAccountNumberParameter;
        PrimaryAccountNumber primaryAccountNumber;
        String authorityWrappingCertificateSubjectKeyIdentifierParameter;
		String authorityName;
        SubjectKeyIdentifier authorityWrappingCertificateSubjectKeyIdentifier;
        PinAuthority pinAuthority;
        CardHolderVerification cardHolderVerification;
        PinRetrievalRequest pinRetrievalRequest;
        Cookie wrappedSessionKeyIssuedCookie;
        Cookie pinRetrievalRequestIssuedCookie;

        cardHolderVerificationValueParameter = null;
        cardHolderVerificationValue = null;
        expiryDateMonthParameter = null;
        expiryDateYearParameter = null;
        expiryDate = null;
        primaryAccountNumberParameter = null;
        primaryAccountNumber = null;
        authorityWrappingCertificateSubjectKeyIdentifierParameter = null;
		authorityName = null;
        authorityWrappingCertificateSubjectKeyIdentifier = null;
        pinAuthority = null;
        cardHolderVerification = null;
        pinRetrievalRequest = null;
        wrappedSessionKeyIssuedCookie = null;
        pinRetrievalRequestIssuedCookie = null;

        getLogger().info("generating pin retrieval request");

        // Get card holder verification value parameter
        cardHolderVerificationValueParameter = request.getParameter(ViewPinConstants.CARD_HOLDER_VERIFICATION_VALUE_PARAMETER_NAME);

        try
        {
            // Instantiate card holder verification value object
            cardHolderVerificationValue = new CardHolderVerificationValue(cardHolderVerificationValueParameter);
        }
        catch (InvalidCardHolderVerificationValueException ichvv)
        {
            getLogger().warn("card holder verification value " + ichvv.getMessage());

            redirectOnFailure(response);

            return;
        }

        // Get expiry date month and expiry date year parameters
        expiryDateMonthParameter = request.getParameter(ViewPinConstants.EXPIRY_DATE_MONTH_PARAMETER_NAME);
        expiryDateYearParameter = request.getParameter(ViewPinConstants.EXPIRY_DATE_YEAR_PARAMETER_NAME);

        // Determine if expiry date month and expiry date year parameters have been submitted
        if (expiryDateMonthParameter != null && expiryDateYearParameter != null)
        {
            try
            {
                // Instantiate expiry date object
                expiryDate = new ExpiryDate(expiryDateMonthParameter, expiryDateYearParameter);
            }
            catch (InvalidExpiryDateException iede)
            {
                // Invalid expiry date
                getLogger().warn("expiry date " + iede.getMessage());

                redirectOnFailure(response);

                return;
            }
        }

        // Get primary account number parameter
        primaryAccountNumberParameter = request.getParameter(ViewPinConstants.PRIMARY_ACCOUNT_NUMBER_PARAMETER_NAME);

        // Determine if primary account number parameter has been submitted
        if (primaryAccountNumberParameter != null)
        {
            try
            {
                // Instantiate primary account number object
                primaryAccountNumber = new PrimaryAccountNumber(primaryAccountNumberParameter);
            }
            catch (InvalidPrimaryAccountNumberException ipane)
            {
                getLogger().warn("primary account number " + ipane.getMessage());

                redirectOnFailure(response);

                return;
            }
        }

		
/*
        // Get authority wrapping certificate subject key identifier parameter
        authorityWrappingCertificateSubjectKeyIdentifierParameter = request.getParameter(ViewPinConstants.AUTHORITY_SUBJECT_KEY_IDENTIFIER_PARAMETER_NAME);

        // Did we get authority wrapping certificate subject key identifier parameter OK?
        if (authorityWrappingCertificateSubjectKeyIdentifierParameter != null)
        {
            try
            {
                // Instantiate authority wrapping certificate subject key identifier
                authorityWrappingCertificateSubjectKeyIdentifier = new SubjectKeyIdentifier(authorityWrappingCertificateSubjectKeyIdentifierParameter);
            }
            catch (InvalidSubjectKeyIdentifierException iskie)
            {
                getLogger().warn("pin authority wrapping certificate subject key identifier " + iskie.getMessage());

                redirectOnFailure(response);

                return;
            }

            // Get pin authority by wrapping certificate subject key identifier
            pinAuthority = getPinAgent().getPinAuthorities().getByWrapping(authorityWrappingCertificateSubjectKeyIdentifier);

            // Did we get pin authority by wrapping certificate subject key identifier OK?
            if (pinAuthority == null)
            {
                // Failed to get pin authority by wrapping certificate subject key identifier
                getLogger().warn("pin authority wrapping certificate subject key identifier " + authorityWrappingCertificateSubjectKeyIdentifier + " not found");

                redirectOnFailure(response);

                return;
            }

            getLogger().debug("using explict pin authority " + pinAuthority.getWrappingCertificateSubjectKeyIdentifier());
			*/
		/* BOA request  to fetch PINAuthority based name to find redirection URL */
		// Get authority name
        authorityName = request.getParameter(ViewPinConstants.AUTHORITY_NAME);	

		// Did we get authority wrapping certificate subject key identifier parameter OK?
        if (authorityName != null)
        {
            try
            {
                 // Get pin authority by Name
				pinAuthority = getPinAgent().getPinAuthorities().getByName(authorityName);
				
				
				
            }
            catch (Exception iskie)
            {
                getLogger().error("pin authority name not found ");

                redirectOnFailure(response);

                return;
            }

        }
        else
        {
            // Pin authority wrapping certificate subject key identifier not passed in, get default
            pinAuthority = getPinAgent().getPinAuthorities().getDefault();

            // Did we get default pin authority OK?
            if (pinAuthority == null)
            {
                // Failed to get default pin authority
                getLogger().error("establishing default pin authority");

                redirectOnFailure(response);

                return;
            }

            getLogger().debug("using default pin authority " + pinAuthority.getWrappingCertificateSubjectKeyIdentifier());
        }

        // Instantiate CardHolderVerification object
        cardHolderVerification = new CardHolderVerification(cardHolderVerificationValue, expiryDate, primaryAccountNumber);

        try
        {
            // Generate pin retrieval request
            pinRetrievalRequest = getPinAgent().generatePinRetrievalRequest(cardHolderVerification, pinAuthority);

            if (getLogger().isDebugEnabled() == true)
            {
                try
                {
                    // Dump serialised pin retrieval request
                    String s = new String(XMLUtils.serialise(pinRetrievalRequest.getPinRetrievalRequestDocument()));
                }
                catch (Exception e)
                {
                    getLogger().error("serialising pin retrieval request document " + e.getMessage());
                }
            }

            getLogger().debug(
                    "issuing wrapped session key cookie with name " + getWrappedSessionKeyCookieDetails().getName() + " to domain " + getWrappedSessionKeyCookieDetails().getDomain() + " with path "
                            + getWrappedSessionKeyCookieDetails().getPath());

            // Issue wrapped session key cookie
            wrappedSessionKeyIssuedCookie = CookieUtils.createCookieForData(getWrappedSessionKeyCookieDetails().getName(), pinRetrievalRequest.getAgentWrappedSessionKey()
                    .getSafeEncodedWrappedSessionKey(), getWrappedSessionKeyCookieDetails().getDomain(), getPinRetrievalRequestCookieDetails().getPath());


			// Add wrapped session key cookie to HTTP response
            response.addCookie(wrappedSessionKeyIssuedCookie);

            getLogger().debug("issued wrapped session key cookie  ok ");

            getLogger().debug(
                    "issuing pin retrieval request cookie with name " + getPinRetrievalRequestCookieDetails().getName() + " to domain " + getPinRetrievalRequestCookieDetails().getDomain()
                            + " with path " + getPinRetrievalRequestCookieDetails().getPath());

            // Issue pin retrieval request cookie
            pinRetrievalRequestIssuedCookie = CookieUtils.createCookieForData(getPinRetrievalRequestCookieDetails().getName(), pinRetrievalRequest.getEncodedCompressedPinRetrievalRequestDocument(),
                    getPinRetrievalRequestCookieDetails().getDomain(), getPinRetrievalRequestCookieDetails().getPath());


            // Add pin retrieval request cookie to HTTP response
            response.addCookie(pinRetrievalRequestIssuedCookie);
          

            getLogger().debug("issued pin retrieval request cookie ok");

            // Pin retrieval request successfully generated
            redirectOnSuccess(response, pinAuthority,ViewPinConstants.PIN_VIEW_REQUEST);

            getLogger().info("generated pin retrieval request ok");
        }
        catch (ViewPinRequestException prre)
        {
            String exceptionMessage = "generating pin retrieval request";

            if (prre.getMessage() != null)
            {
                exceptionMessage += " " + prre.getMessage();
            }

            getLogger().error(exceptionMessage);

            redirectOnFailure(response);
        }
    }
    
    private void requestPinChange (HttpServletRequest request, HttpServletResponse response)
    {
        String cardHolderVerificationValueParameter;
        CardHolderVerificationValue cardHolderVerificationValue;
        String oldPinParameter;
        String newPinParameter;
        CardPin oldPin;
        CardPin newPin;
        String expiryDateMonthParameter;
        String expiryDateYearParameter;
        ExpiryDate expiryDate;
        String primaryAccountNumberParameter;
        PrimaryAccountNumber primaryAccountNumber;
        String authorityWrappingCertificateSubjectKeyIdentifierParameter;
        SubjectKeyIdentifier authorityWrappingCertificateSubjectKeyIdentifier;
        PinAuthority pinAuthority;
        PinChangeData pinChangeData;
        PinChangeRequest pinChangeRequest;
        Cookie pinChangeRequestIssuedCookie;

        cardHolderVerificationValueParameter = null;
        cardHolderVerificationValue = null;
        oldPinParameter=null;
        oldPin = null;
        newPinParameter=null;
        newPin= null;
        expiryDateMonthParameter = null;
        expiryDateYearParameter = null;
        expiryDate = null;
        primaryAccountNumberParameter = null;
        primaryAccountNumber = null;
        authorityWrappingCertificateSubjectKeyIdentifierParameter = null;
        authorityWrappingCertificateSubjectKeyIdentifier = null;
        pinAuthority = null;
        pinChangeData = null;
        pinChangeRequest = null;
        pinChangeRequestIssuedCookie = null;

        getLogger().info("generating pin change request");
        
       
        // Get card holder verification value parameter
        cardHolderVerificationValueParameter = request.getParameter(ViewPinConstants.CARD_HOLDER_VERIFICATION_VALUE_PARAMETER_NAME);
             
        try
        {
            // Instantiate card holder verification value object
            cardHolderVerificationValue = new CardHolderVerificationValue(cardHolderVerificationValueParameter);
        }
        catch (InvalidCardHolderVerificationValueException ichvv)
        {
            getLogger().warn("card holder verification value " + ichvv.getMessage());

            redirectOnFailure(response);

            return;
        }

        // Get expiry date month and expiry date year parameters
        expiryDateMonthParameter = request.getParameter(ViewPinConstants.EXPIRY_DATE_MONTH_PARAMETER_NAME);
        expiryDateYearParameter = request.getParameter(ViewPinConstants.EXPIRY_DATE_YEAR_PARAMETER_NAME);

        // Determine if expiry date month and expiry date year parameters have been submitted
        if (expiryDateMonthParameter != null && expiryDateYearParameter != null)
        {
            try
            {
                // Instantiate expiry date object
                expiryDate = new ExpiryDate(expiryDateMonthParameter, expiryDateYearParameter);
            }
            catch (InvalidExpiryDateException iede)
            {
                // Invalid expiry date
                getLogger().warn("expiry date " + iede.getMessage());

                redirectOnFailure(response);

                return;
            }
        }

        // Get primary account number parameter
        primaryAccountNumberParameter = request.getParameter(ViewPinConstants.PRIMARY_ACCOUNT_NUMBER_PARAMETER_NAME);

        // Determine if primary account number parameter has been submitted
        if (primaryAccountNumberParameter != null)
        {
            try
            {
                // Instantiate primary account number object
                primaryAccountNumber = new PrimaryAccountNumber(primaryAccountNumberParameter);
            }
            catch (InvalidPrimaryAccountNumberException ipane)
            {
                getLogger().warn("primary account number " + ipane.getMessage());

                redirectOnFailure(response);

                return;
            }
        }
        
        // Get the value of Old, New, And re confirmed Card PIN no's 
        oldPinParameter = request.getParameter(ViewPinConstants.OLD_PIN);
        newPinParameter = request.getParameter(ViewPinConstants.NEW_PIN);
        
        if (oldPinParameter != null)
        {
        	try
        	{
        		oldPin = new CardPin(oldPinParameter);
        	}
        	catch (InvalidCardPinException icpe)
        	{
        		getLogger().warn("Old Pin " + icpe.getMessage());

                redirectOnFailure(response);

                return;
        	}
        }
       
        if (newPinParameter != null)
        {
        	try
        	{
        		newPin = new CardPin(newPinParameter);
        	}
        	catch (InvalidCardPinException icpe)
        	{
        		getLogger().warn("New Pin " + icpe.getMessage());

                redirectOnFailure(response);

                return;
        	}
        }
                       
        // Get authority wrapping certificate subject key identifier parameter
        authorityWrappingCertificateSubjectKeyIdentifierParameter = request.getParameter(ViewPinConstants.AUTHORITY_SUBJECT_KEY_IDENTIFIER_PARAMETER_NAME);

        // Did we get authority wrapping certificate subject key identifier parameter OK?
        if (authorityWrappingCertificateSubjectKeyIdentifierParameter != null)
        {
            try
            {
                // Instantiate authority wrapping certificate subject key identifier
                authorityWrappingCertificateSubjectKeyIdentifier = new SubjectKeyIdentifier(authorityWrappingCertificateSubjectKeyIdentifierParameter);
            }
            catch (InvalidSubjectKeyIdentifierException iskie)
            {
                getLogger().warn("pin authority wrapping certificate subject key identifier " + iskie.getMessage());

                redirectOnFailure(response);

                return;
            }

            // Get pin authority by wrapping certificate subject key identifier
            pinAuthority = getPinAgent().getPinAuthorities().getByWrapping(authorityWrappingCertificateSubjectKeyIdentifier);

            // Did we get pin authority by wrapping certificate subject key identifier OK?
            if (pinAuthority == null)
            {
                // Failed to get pin authority by wrapping certificate subject key identifier
                getLogger().warn("pin authority wrapping certificate subject key identifier " + authorityWrappingCertificateSubjectKeyIdentifier + " not found");

                redirectOnFailure(response);

                return;
            }

            getLogger().debug("using explict pin authority " + pinAuthority.getWrappingCertificateSubjectKeyIdentifier());
        }
        else
        {
            // Pin authority wrapping certificate subject key identifier not passed in, get default
            pinAuthority = getPinAgent().getPinAuthorities().getDefault();

            // Did we get default pin authority OK?
            if (pinAuthority == null)
            {
                // Failed to get default pin authority
                getLogger().error("establishing default pin authority");

                redirectOnFailure(response);

                return;
            }

            getLogger().debug("using default pin authority " + pinAuthority.getWrappingCertificateSubjectKeyIdentifier());
        }

        // Instantiate PinChangeData object
        pinChangeData = new PinChangeData(cardHolderVerificationValue, expiryDate, primaryAccountNumber, oldPin, newPin);
        
        
        try
        {
            // Generate pin retrieval request
        	pinChangeRequest = getPinAgent().generatePinChangeRequest(pinChangeData, pinAuthority);

            if (getLogger().isDebugEnabled() == true)
            {
                try
                {
                    // Dump serialised pin retrieval request
                    String s = new String(XMLUtils.serialise(pinChangeRequest.getPinChangeRequestDocument()));
					
                }
                catch (Exception e)
                {
                    getLogger().error("serialising pin retrieval request document " + e.getMessage());
                }
            }

            getLogger().debug(
                    "issuing wrapped session key cookie with name " + getWrappedSessionKeyCookieDetails().getName() + " to domain " + getWrappedSessionKeyCookieDetails().getDomain() + " with path "
                            + getWrappedSessionKeyCookieDetails().getPath());

            getLogger().debug(
                    "issuing pin change request cookie with name " + getPinChangeRequestCookieDetails().getName() + " to domain " + getPinChangeRequestCookieDetails().getDomain()
                            + " with path " + getPinChangeRequestCookieDetails().getPath());

            // Issue pin retrieval request cookie
            pinChangeRequestIssuedCookie = CookieUtils.createCookieForData(getPinChangeRequestCookieDetails().getName(), pinChangeRequest.getEncodedCompressedPinChangeRequestDocument(),
                    getPinChangeRequestCookieDetails().getDomain(), getPinChangeRequestCookieDetails().getPath());

            // Add pin Change request cookie to HTTP response
            response.addCookie(pinChangeRequestIssuedCookie);

            getLogger().debug("issued pin Change request cookie ok");

            // Pin retrieval request successfully generated
            redirectOnSuccess(response, pinAuthority,ViewPinConstants.PIN_CHANGE_REQUEST);

            getLogger().info("generated pin retrieval request ok");
        }
        catch (ViewPinRequestException prre)
        {
            String exceptionMessage = "generating pin Change request";

            if (prre.getMessage() != null)
            {
                exceptionMessage += " " + prre.getMessage();
            }

            getLogger().error(exceptionMessage);

            redirectOnFailure(response);
        }
        
    }

    private void redirectOnSuccess (HttpServletResponse response, PinAuthority pinAuthority,Integer requestType)
    {
    	String redirectionUrl;
    	
    	redirectionUrl=null;
    	
    	
        try
        {
        	redirectionUrl=pinAuthority.getRedirectionUrl().toExternalForm().toString()+"?"+ViewPinConstants.REQUEST_TYPE+"="+ requestType.toString();
            getLogger().debug("authority redirection url = " + redirectionUrl);

            // Redirect client to pin authority
            response.sendRedirect(redirectionUrl);
        }
        catch (IOException ioe)
        {
            String exceptionMessage = "redirecting client to pin authority";

            if (ioe.getMessage() != null)
            {
                exceptionMessage += " " + ioe.getMessage();
            }

            getLogger().error(exceptionMessage);
        }
        catch (Exception e)
        {
            String exceptionMessage = "redirecting client to pin authority";

            if (e.getMessage() != null)
            {
                exceptionMessage += " " + e.getMessage();
            }

            getLogger().error(exceptionMessage);
            
            redirectOnFailure (response);
        }

    }

    private void redirectOnFailure (HttpServletResponse response)
    {
        getLogger().debug("error redirection url = " + getErrorRedirectionUrl().toExternalForm());

        try
        {
            response.sendRedirect(getErrorRedirectionUrl().toExternalForm());
        }
        catch (IOException ioe)
        {
            String exceptionMessage = "redirecting client on error";

            if (ioe.getMessage() != null)
            {
                exceptionMessage += " " + ioe.getMessage();
            }

            getLogger().error(exceptionMessage);
        }
    }

    /**
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy ()
    {
        super.destroy();
    }

    private Logger getLogger ()
    {
        return logger;
    }

    private synchronized void setPinAgent (PinAgent pinAgent)
    {
        this.pinAgent = pinAgent;
    }

    private synchronized PinAgent getPinAgent ()
    {
        return this.pinAgent;
    }

    private void initLogging () throws AgentInitException
    {
        File loggingConfigurationFile;

        loggingConfigurationFile = null;

        loggingConfigurationFile = ConfigurationReader.openConfigurationFile(ViewPinConstants.LOGGING_CONFIGURATION_FILE,getLogger());

        // Load logging configuraton file
        DOMConfigurator.configure(loggingConfigurationFile.getAbsolutePath());
    }
    
    private synchronized void setErrorRedirectionUrl (URL errorRedirectionUrl)
    {
        this.errorRedirectionUrl = errorRedirectionUrl;
    }

    private URL getErrorRedirectionUrl ()
    {
        return this.errorRedirectionUrl;
    }

    private synchronized void setWrappedSessionKeyCookieDetails (CookieDetails wrappedSessionKeyIssuedCookie)
    {
        this.wrappedSessionKeyCookieDetails = wrappedSessionKeyIssuedCookie;
    }

    private CookieDetails getWrappedSessionKeyCookieDetails ()
    {
        return this.wrappedSessionKeyCookieDetails;
    }

    private synchronized void setPinRetrievalRequestCookieDetails (CookieDetails pinRetrievalRequestIssuedCookie)
    {
        this.pinRetrievalRequestCookieDetails = pinRetrievalRequestIssuedCookie;
    }

    private synchronized CookieDetails getPinRetrievalRequestCookieDetails ()
    {
        return this.pinRetrievalRequestCookieDetails;
    }

    private synchronized void setPinChangeRequestCookieDetails (CookieDetails pinChangeRequestIssuedCookie)
    {
        this.pinChangeRequestCookieDetails = pinChangeRequestIssuedCookie;
    }

    private synchronized CookieDetails getPinChangeRequestCookieDetails ()
    {
        return this.pinChangeRequestCookieDetails;
    }
    
    private synchronized void setPinRetrievalResponseCookieDetails (CookieDetails pinRetrievalResponseIssuedCookie)
    {
        this.pinRetrievalResponseCookieDetails = pinRetrievalResponseIssuedCookie;
    }

    private synchronized CookieDetails getPinRetrievalResponseCookieDetails ()
    {
        return this.pinRetrievalResponseCookieDetails;
    }

    private boolean isValidCookieName (String cookieName)
    {
        boolean valid;

        valid = false;

        if (cookieName == null)
        {
            return valid;
        }

        if (cookieName.matches(ViewPinConstants.COOKIE_NAME_REGEX) == true)
        {
            valid = true;
        }

        return valid;
    }

    private String generateOneTimePadKeyCookieName () throws NoSuchAlgorithmException
    {
        return Utils.generateRandomString(ViewPinConstants.ONE_TIME_PAD_KEY_COOKIE_NAME_LENGTH);
    }
}