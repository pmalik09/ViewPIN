package com.safenetinc.viewpin.simulator.authority.xml.signatures;

/**
 * Class to store the algorithm URLs for XML Digital Signatures
 * 
 * @author Stuart Horler
 * 
 */
public class XmlDigestMethodAlgorithms
{
    /**
     * SHA-1
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA1   = "http://www.w3.org/2000/09/xmldsig#sha1";

    /**
     * MD5
     */
    public static final String DIGEST_METHOD_ALGORITHM_MD5    = "http://www.w3.org/2001/04/xmldsig-more#md5";

    /**
     * SHA-256
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#sha256";

    /**
     * SHA-348
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";

    /**
     * SHA-512
     */
    public static final String DIGEST_METHOD_ALGORITHM_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#sha512";

    private XmlDigestMethodAlgorithms()
    {
        super();
    }
}
