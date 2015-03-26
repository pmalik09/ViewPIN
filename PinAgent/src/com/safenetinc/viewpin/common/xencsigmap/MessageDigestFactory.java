// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/MessageDigestFactory.java 1.1 2008/09/04 10:47:22IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xencsigmap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Class to return a {@link MessageDigest} object for an associated message digest algorithm
 * @author Stuart Horler
 *
 *
 */
public class MessageDigestFactory
{
    private static final String ALGORITHM_SHA1 = "SHA-1";
    private static final String ALGORITHM_MD5 = "MD5";
    private static final String ALGORITHM_SHA256 = "SHA-256";
    private static final String ALGORITHM_SHA384 = "SHA-384";
    private static final String ALGORITHM_SHA512 = "SHA-512";
    
    private MessageDigestFactory()
    {
        super();
    }
    
    /**
     * Method to return a {@link MessageDigest} object associated with the specified message digest algorithm
     * @param digestMethodAlgorithm The message digest algorithm
     * @return The {@link MessageDigest} object associated with the specified algorithm
     * @throws NoSuchAlgorithmException
     */
    public static MessageDigest getInstance(String digestMethodAlgorithm) throws NoSuchAlgorithmException
    {
        MessageDigest messageDigestEngine;
        String algorithm;
        
        messageDigestEngine = null;
        algorithm = null;
        
        if(digestMethodAlgorithm.compareTo(XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA1) == 0)
        {
            algorithm = ALGORITHM_SHA1;
        }
        else if(digestMethodAlgorithm.compareTo(XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_MD5) == 0)
        {
            algorithm = ALGORITHM_MD5;
        }
        else if(digestMethodAlgorithm.compareTo(XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA256) == 0)
        {
            algorithm = ALGORITHM_SHA256;
        }    
        else if(digestMethodAlgorithm.compareTo(XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA384) == 0)
        {
            algorithm = ALGORITHM_SHA384;
        }
        else if(digestMethodAlgorithm.compareTo(XmlDigestMethodAlgorithms.DIGEST_METHOD_ALGORITHM_SHA512) == 0)
        {
            algorithm = ALGORITHM_SHA512;
        }
        else
        {
            throw new NoSuchAlgorithmException();
        }
        
        messageDigestEngine = MessageDigest.getInstance(algorithm);
        
        return messageDigestEngine;
    }
}