// $Id: PinAgent/src/com/safenetinc/viewpin/agent/ViewPinConstants.java 1.3 2012/07/30 13:52:27IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

/**
 * Class to hold constants used by the PINAgent system.
 * 
 * @author Paul Hampton
 */
public class ViewPinConstants
{
    /**
     * The XML namespace
     */
    public static final String NAMESPACE_NAMESPACE                             = "http://www.w3.org/2000/xmlns/";

    /**
     * The XML Encryption namespace
     */
    public static final String XENC_NAMESPACE                                  = "http://www.w3.org/2001/04/xmlenc#";

    /**
     * The XML digital signature namespace
     */
    public static final String DSIG_NAMESPACE                                  = "http://www.w3.org/2000/09/xmldsig#";

    /**
     * The name for the RNG
     */
    public static final String RANDOM_NUMBER_GENERATOR_ALGORITHM_NAME          = "LunaRNG";

    /**
     * The length of the buffer to use during message decompression
     */
    public static final int    DECOMPRESSION_READ_BUFFER_LENGTH                = 512;

    /**
     * The path to the pre-decryption schema
     */
    public static final String VIEWPIN_PRE_DECRYPTION_SCHEMA                   = "schemas/ViewPinPreDecryption.xsd";

    /**
     * The path to the post-decryption schema
     */
    public static final String VIEWPIN_POST_DECRYPTION_SCHEMA                  = "schemas/ViewPinPostDecryption.xsd";

    /**
     * The prefix added to the XML Namespace
     */
    public static final String NAMESPACE_NAMESPACE_PREFIX                      = "xmlns";

    /**
     * The URI for the XML Namespace
     */
    public static final String NAMESPACE_NAMESPACE_URI                         = "http://www.w3.org/2000/xmlns/";

    /**
     * The prefix added to the ViewPIN Namespace
     */
    public static final String VIEWPIN_NAMESPACE_PREFIX                        = "vp";

    /**
     * The ViewPIN namespace URI
     */
    public static final String VIEWPIN_NAMESPACE_URI                           = "http://www.safenet-inc.com/ns/viewpin";

    /**
     * The XML encryption namespace
     */
    public static final String XENC_NAMESPACE_PREFIX                           = "xenc";

    /**
     * The XML Encryption namespace URI
     */
    public static final String XENC_NAMESPACE_URI                              = "http://www.w3.org/2001/04/xmlenc#";

    /**
     * The XML Digital Signature namespace prefix
     */
    public static final String DSIG_NAMESPACE_PREFIX                           = "ds";

    /**
     * The XML Digital Signature namespace URI
     */
    public static final String DSIG_NAMESPACE_URI                              = "http://www.w3.org/2000/09/xmldsig#";

    /**
     * The format for dates handled by ViewPIN
     */
    public static final String DATE_TIME_FORMAT                                = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Regex for validating the name of a cookie
     */
    public static final String COOKIE_NAME_REGEX                               = "^[a-zA-Z0-9]{1,40}$";

    /**
     * Minimum replay window
     */
    public static final long   MINIMUM_SENSIBLE_REPLAY_WINDOW                  = 1000L; // one second
    
    /**
     * Maximum replay window
     */
    public static final long   MAXIMUM_SENSIBLE_REPLAY_WINDOW                  = 3600000L; // one hour
   
    /**
     * PIN Change Request Identifier
     */
    public static final int  PIN_CHANGE_REQUEST               			   = 01; 
    
    /**
     * PIN View Request Identifier
     */
    public static final int  PIN_VIEW_REQUEST              				   = 02; 
    
    /**
     * Length of the randomly generated one time pad cookie name
     */
    public static final int    ONE_TIME_PAD_KEY_COOKIE_NAME_LENGTH             = 10;

    /**
     * Name of the agent configuration file
     */
    public static final String AGENT_CONFIGURATION_FILE                        = "agentconfiguration.xml";

    /**
     * Post parameter name for request type
     */
    public static final String REQUEST_TYPE            				   = "requesttype";

    /**
     * Post parameter name for old PIN
     */
    public static final String OLD_PIN             							   = "oldpin";

    /**
     * Post parameter name for new PIN 
     */
    public static final String NEW_PIN                						   = "newpin";

    /**
     * post parameter name for CVV value
     */
    public static final String CARD_HOLDER_VERIFICATION_VALUE_PARAMETER_NAME   = "cardholderverificationvalue";

    /**
     * Post parameter name for expiry date
     */
    public static final String EXPIRY_DATE_MONTH_PARAMETER_NAME                = "expirydatemonth";

    /**
     * Post parameter name for expiry year
     */
    public static final String EXPIRY_DATE_YEAR_PARAMETER_NAME                 = "expirydateyear";

    /**
     * Post parameter name for PAN
     */
    public static final String PRIMARY_ACCOUNT_NUMBER_PARAMETER_NAME           = "primaryaccountnumber";

    /**
     * Post parameter name for Authority SKI
     */
    public static final String AUTHORITY_SUBJECT_KEY_IDENTIFIER_PARAMETER_NAME = "authoritysubjectkeyidentifier";
	
	/**
     * Post parameter name for Authority NAME
     */
    public static final String AUTHORITY_NAME 									= "authorityname";

    /**
     * Name of log4j config
     */
    public static final String LOGGING_CONFIGURATION_FILE                      = "pinagent-log4j.xml";

    /**
     * Version of the PINAgent, automatically updated to match svn global revision by the ant build
     */
    public static final String VERSION										   ="version";

    /**
     * Path to be used when issuing cookies containing one time pad keys
     */
	public static String ONE_TIME_PAD_COOKIE_PATH 							   = "/";

	/**
	 * Number of seconds after which session should be invalidated 
	 */
	public static int SESSION_INVALID_AFTER 								   = 120; // Two minutes
	
    private ViewPinConstants()
    {
        super();
    }
}
