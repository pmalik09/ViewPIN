// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/CipherFactory.java 1.3 2013/09/25 09:44:56IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.common.xencsigmap;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


/**
 * Class to map encryption algorithm types to an associated {@link Cipher} object
 * @author Stuart Horler
 *
 */
public class CipherFactory
{
    private static final String TRANSFORMATION_DESEDE = "DESede/CBC/ISO10126Padding";
   // private static final String TRANSFORMATION_AES = "AES/CBC/ISO10126Padding";
    private static final String TRANSFORMATION_AES = "AES/CBC/PKCS5Padding";
    private static final String TRANSFORMATION_RSA_OAEP = "RSA/NONE/OAEPWithSHA1AndMGF1Padding";
    private static final String TRANSFORMATION_RSA_PKCS15 = "RSA/ECB/PKCS1Padding";
    
    private CipherFactory()
    {
        super();
    }
    
    /**
     * Returns a {@link Cipher} object for the specified encryption method
     * @param encryptionMethodAlgorithm The encryption method to use
     * @return The {@link Cipher} object
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws NoSuchProviderException
     */
    public static Cipher getInstance(String encryptionMethodAlgorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException
    {
        Cipher c;
        String transformation;
        
        c = null;
        transformation = null;
        
        if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_DESEDE) == 0)
        {
            transformation = TRANSFORMATION_DESEDE;
        }
        else if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES128) == 0)
        {
            transformation = TRANSFORMATION_AES;
        }
        else if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES192) == 0)
        {
            transformation = TRANSFORMATION_AES;
        }
        else if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES256) == 0)
        {
            transformation = TRANSFORMATION_AES;
        }
        else if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_RSA_OAEP_MGF1) == 0)
        {
            transformation = TRANSFORMATION_RSA_OAEP;
        }
        else if(encryptionMethodAlgorithm.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_RSA_PKCS15) == 0)
        {
            transformation = TRANSFORMATION_RSA_PKCS15;
        }
        else
        {
            throw new NoSuchAlgorithmException();    
        }
        
        c = Cipher.getInstance(transformation, "LunaProvider");
        
        return c;
    }
}