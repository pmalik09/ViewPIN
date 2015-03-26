// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/KeyGeneratorFactory.java 1.3 2013/09/25 09:44:58IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.common.xencsigmap.XmlEncryptionMethodAlgorithms;

/**
 * Factory class to handle instantiation of {@link KeyGenerator} objects
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class KeyGeneratorFactory
{
  //  private static final String ALGORITHM_DESEDE                  = "LunaDESede";
  private static final String ALGORITHM_DESEDE                  = "DESede";

    //private static final String ALGORITHM_AES                     = "LunaAES";
	private static final String ALGORITHM_AES                     = "AES";

    private static final int    KEY_LENGTH_DESEDE_168             = 168;

    private static final int    KEY_LENGTH_AES_128                = 128;

    private static final int    KEY_LENGTH_AES_192                = 192;

    private static final int    KEY_LENGTH_AES_256                = 256;

    private static final String RANDOM_NUMBER_GENERATOR_ALGORITHM = "LunaRNG";
//   private static final String RANDOM_NUMBER_GENERATOR_ALGORITHM = "RNG";

    private static Logger       logger                            = Logger.getLogger(KeyGeneratorFactory.class);

    private KeyGeneratorFactory()
    {
        super();
    }

    /**
     * Returns a new {@link KeyGenerator} instance
     * 
     * @param xmlEncryptionAlgorithmMethod String representing the encryption method that the
     *        {@link KeyGenerator} will be producing keys for
     * @return A new {@link KeyGenerator} instance
     * @throws NoSuchAlgorithmException Thrown if the specified encryption algorithm is invalid
     * @throws NoSuchProviderException Thrown if there is a problem with the underlying cryptography provider
     */
    public static KeyGenerator getInstance (String xmlEncryptionAlgorithmMethod) throws NoSuchAlgorithmException, NoSuchProviderException
    {
        KeyGenerator kg;
        String algorithm;
        int keyLength;
        SecureRandom randomNumberGenerator;

        kg = null;
        algorithm = null;
        keyLength = 0;
        randomNumberGenerator = null;

        if (xmlEncryptionAlgorithmMethod.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_DESEDE) == 0)
        {
            algorithm = ALGORITHM_DESEDE;
            keyLength = KEY_LENGTH_DESEDE_168;
        }
        else if (xmlEncryptionAlgorithmMethod.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES128) == 0)
        {
            algorithm = ALGORITHM_AES;
            keyLength = KEY_LENGTH_AES_128;
        }
        else if (xmlEncryptionAlgorithmMethod.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES192) == 0)
        {
            algorithm = ALGORITHM_AES;
            keyLength = KEY_LENGTH_AES_192;
        }
        else if (xmlEncryptionAlgorithmMethod.compareTo(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES256) == 0)
        {
            algorithm = ALGORITHM_AES;
            keyLength = KEY_LENGTH_AES_256;
        }
        else
        {
            throw new NoSuchAlgorithmException();
        }

        // Get key generator instance
        kg = KeyGenerator.getInstance(algorithm, "LunaProvider");

        getLogger().debug("key generator algorithm = " + kg.getAlgorithm());
        getLogger().debug("key generator provider = " + kg.getProvider());

        // Get random number generator instance
        randomNumberGenerator = SecureRandom.getInstance(RANDOM_NUMBER_GENERATOR_ALGORITHM, "LunaProvider");

        getLogger().debug("random number generator algorithm = " + randomNumberGenerator.getAlgorithm());
        getLogger().debug("random number generator provider = " + randomNumberGenerator.getProvider());

        // Initialise key generator
        kg.init(keyLength, randomNumberGenerator);

        return kg;
    }

    private static Logger getLogger ()
    {
        return logger;
    }
}