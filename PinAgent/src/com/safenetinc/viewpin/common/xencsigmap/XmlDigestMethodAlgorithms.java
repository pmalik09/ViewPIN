// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/XmlDigestMethodAlgorithms.java 1.1 2008/09/04 10:47:26IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xencsigmap;

/**
 * Class to hold the XML Digital Signature URI values for the various digest algorithms
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class XmlDigestMethodAlgorithms
{
    /**
     * The SHA-1 digest algorithm
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA1   = "http://www.w3.org/2000/09/xmldsig#sha1";

    /**
     * The MD5 digest algorithm
     */
    public static final String DIGEST_METHOD_ALGORITHM_MD5    = "http://www.w3.org/2001/04/xmldsig-more#md5";

    /**
     * The SHA-256 digest algorithm
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#sha256";

    /**
     * The SHA-384 digest algorithm
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";

    /**
     * The SHA-512 digest algorithm
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#sha512";

    private XmlDigestMethodAlgorithms()
    {
        // Private constructor, nothing to do here
    }
}
