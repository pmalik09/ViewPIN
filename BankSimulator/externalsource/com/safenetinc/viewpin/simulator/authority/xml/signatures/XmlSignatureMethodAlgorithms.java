package com.safenetinc.viewpin.simulator.authority.xml.signatures;

/**
 * Class to store XML signature name URLs
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class XmlSignatureMethodAlgorithms
{
    /**
     * RSA with SHA-1
     */
    public static final String SIGNATURE_METHOD_ALGORITHM_RSA_SHA1   = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

    /**
     * RSA with MD5
     */
    public static final String SIGNATURE_METHOD_ALGORITHM_RSA_MD5    = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";

    /**
     * RSA with SHA-256
     */
    public static final String SIGNATURE_METHOD_ALGORITHM_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    /**
     * RSA with SHA-384
     */
    public static final String SIGNATURE_METHOD_ALGORITHM_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";

    /**
     * RSA with SHA-512
     */
    public static final String SIGNATURE_METHOD_ALGORITHM_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";

    private XmlSignatureMethodAlgorithms()
    {
        super();
    }
}
