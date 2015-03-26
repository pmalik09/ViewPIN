package com.safenetinc.viewpin.authority.xml.encryption;

import java.security.NoSuchAlgorithmException;

/**
 * Class to map an XML Encryption algorithm name to a JCE/JCA encryption algorithm name
 * 
 * @author Stuart Horler
 * 
 */
public class XmlEncryptionAlgorithmMapper
{
    private static final String ALGORITHM_NAME_TRIPLEDES_CBC = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";

    private static final String ALGORITHM_NAME_AES128_CBC    = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";

    private static final String ALGORITHM_NAME_AES192_CBC    = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";

    private static final String ALGORITHM_NAME_AES256_CBC    = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";

    private static final String ALGORITHM_NAME_RSA_OAEP_MGF1 = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1";

    private static final String ALGORITHM_NAME_RSA_PKCS15    = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";

    private static final String TRANSFORMATION_TRIPLEDES_CBC = "DESede/CBC/NoPadding";

    private static final String TRANSFORMATION_AES_CBC       = "AES/CBC/PKCS5Padding";

    private static final String TRANSFORMATION_RSA_OAEP_MGF1 = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";

    private static final String TRANSFORMATION_RSA_PKCS15    = "RSA/ECB/PKCS1Padding";

    private XmlEncryptionAlgorithmMapper()
    {
        super();
    }

    /**
     * Converts an XML Encryption algorithm name to a JCE/JCA algorithm String
     * 
     * @param encryptionMethodAlgorithmName The XML Encryption algorithm name
     * @return The JCA/JCE operation string
     * @throws NoSuchAlgorithmException Thrown if an unknown or invalid XML Encryption algorithm name is
     *         specified
     */
    public static String getTransformation (String encryptionMethodAlgorithmName) throws NoSuchAlgorithmException
    {
        String transformation;

        transformation = null;

        if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_TRIPLEDES_CBC) == 0)
        {
            transformation = TRANSFORMATION_TRIPLEDES_CBC;
        }
        else if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_AES128_CBC) == 0)
        {
            transformation = TRANSFORMATION_AES_CBC;
        }
        else if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_AES192_CBC) == 0)
        {
            transformation = TRANSFORMATION_AES_CBC;
        }
        else if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_AES256_CBC) == 0)
        {
            transformation = TRANSFORMATION_AES_CBC;
        }
        else if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_RSA_OAEP_MGF1) == 0)
        {
            transformation = TRANSFORMATION_RSA_OAEP_MGF1;
        }
        else if (encryptionMethodAlgorithmName.compareToIgnoreCase(ALGORITHM_NAME_RSA_PKCS15) == 0)
        {
            transformation = TRANSFORMATION_RSA_PKCS15;
        }
        else
        {
            throw new NoSuchAlgorithmException();
        }

        return transformation;
    }

    /**
     * Gets a cryptographic algorithm name from an XML Encryption name
     * 
     * @param encryptionMethodAlgorithmName The XML Encryption name
     * @return The encryption algorithm name
     * @throws NoSuchAlgorithmException Thrown if an unknown or invalid XML Encryption algorithm name is
     *         specified
     */
    public static String getAlgorithm (String encryptionMethodAlgorithmName) throws NoSuchAlgorithmException
    {
        String algorithm;
        String transformation;

        algorithm = null;
        transformation = null;

        transformation = getTransformation(encryptionMethodAlgorithmName);

        algorithm = transformation.substring(0, transformation.indexOf('/'));

        return algorithm;
    }
}