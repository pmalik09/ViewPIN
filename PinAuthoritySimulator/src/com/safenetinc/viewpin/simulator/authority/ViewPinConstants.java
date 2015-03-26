// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/ViewPinConstants.java 1.1 2008/09/04 10:49:20IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

/**
 * Class to hold the constants used by the PinAuthoritySimulator
 * 
 * @author Stuart Horler
 * 
 */
public class ViewPinConstants
{
    /**
     * The xml namespace prefix
     */
    public static final String NAMESPACE_NAMESPACE_PREFIX                     = "xmlns";

    /**
     * The xml namespace uri
     */
    public static final String NAMESPACE_NAMESPACE_URI                        = "http://www.w3.org/2000/xmlns/";

    /**
     * Constant representing the ViewPIN namespace prefix
     */
    public static final String VIEWPIN_NAMESPACE_PREFIX                       = "vp";

    /**
     * The ViewPIN namespace URI
     */
    public static final String VIEWPIN_NAMESPACE_URI                          = "http://www.safenet-inc.com/ns/viewpin";

    /**
     * The XML Encryption namespace prefix
     */
    public static final String XENC_NAMESPACE_PREFIX                          = "xenc";

    /**
     * The XML Encryption namespace URI
     */
    public static final String XENC_NAMESPACE_URI                             = "http://www.w3.org/2001/04/xmlenc#";

    /**
     * The XML Digital Signature namespace prefix
     */
    public static final String DSIG_NAMESPACE_PREFIX                          = "ds";

    /**
     * The XML Digital Signature namespace URI
     */
    public static final String DSIG_NAMESPACE_URI                             = "http://www.w3.org/2000/09/xmldsig#";

    /**
     * The path to the agent and authority keystore files and the authority configuration file
     */
    static String              CONFIGURATION_FILE_PATH;

    /**
     * The name of the agent keystore file
     */
    public static final String AGENT_KEYSTORE_FILE                            = "agent.ks";

    /**
     * The name of the authority keystore file
     */
    public static final String AUTHORITY_KEYSTORE_FILE                        = "authority.ks";

    /**
     * The password for the agent and authority keystores
     */
    public static final String DEFAULT_KEYSTORE_PASSWORD                      = "password";

    /**
     * Buffer length for decompression
     */
    public static final int    DECOMPRESSION_READ_BUFFER_LENGTH               = 512;

    /**
     * Maximum number of authentication attempts a customer is allowed
     */
    public static final int    DEFAULT_MAXIMUM_FAILED_AUTHENTICATION_ATTEMPTS = 3;

    private ViewPinConstants()
    {
        // Private, nothing to do here
    }

    /**
     * @param configuration_file_path the cONFIGURATION_FILE_PATH to set
     */
    public static void setCONFIGURATION_FILE_PATH (String configuration_file_path)
    {
        CONFIGURATION_FILE_PATH = configuration_file_path;
    }
}