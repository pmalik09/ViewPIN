// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/XmlEncryptionMethodAlgorithms.java 1.1 2008/09/04 10:47:30IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xencsigmap;

/**
 * Class to hold the XML Encryption URI values for the various encryption algorithms
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class XmlEncryptionMethodAlgorithms
{
    /**
     * Triple DES (encrypt,decrypt,encrypt)
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_DESEDE        = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";

    /**
     * AES 128
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_AES128        = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";

    /**
     * AES 192
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_AES192        = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";

    /**
     * AES 256
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_AES256        = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";

    /**
     * RSA with OEAP and MGF1
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_RSA_OAEP_MGF1 = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1";

    /**
     * RSA with PKCS#15
     */
    public static final String ENCRYPTION_METHOD_ALGORITHM_RSA_PKCS15    = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";

    private XmlEncryptionMethodAlgorithms()
    {
        //Private class nothing to do here
    }
}
