// $Id: PinAgent/src/com/safenetinc/viewpin/common/xencsigmap/XmlEncryptionAlgorithmMapper.java 1.1 2008/09/04 10:47:28IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xencsigmap;

import com.safenetinc.viewpin.agent.sessionkey.KeyType;
import com.safenetinc.viewpin.agent.sessionkey.PaddingScheme;
import com.safenetinc.viewpin.agent.sessionkey.SessionCipherProperties;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.InvalidSessionKeyLengthException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedKeyTypeException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedPaddingSchemeException;

/**
 * Class to handle mapping of both KeyTypes to encryption methods and padding schemes to padding methods
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class XmlEncryptionAlgorithmMapper
{
    private static final int XML_ENCRYPTION_KEY_LENGTH_DESEDE_168 = 168;

    private static final int XML_ENCRYPTION_KEY_LENGTH_AES_128    = 128;

    private static final int XML_ENCRYPTION_KEY_LENGTH_AES_192    = 192;

    private static final int XML_ENCRYPTION_KEY_LENGTH_AES_256    = 256;

    private XmlEncryptionAlgorithmMapper()
    {
        super();
    }

    /**
     * Maps a key type to an encryption algorithm
     * 
     * @param sessionCipherProperties The {@link SessionCipherProperties} containing the KeyType
     * @return String containing the associated encryption method algorithm name
     * @throws UnsupportedKeyTypeException Thrown if an unsupported key type is specified
     * @throws InvalidSessionKeyLengthException Thrown if the length of the key doesn't match the specified
     *         key type
     */
    public static String map (SessionCipherProperties sessionCipherProperties) throws UnsupportedKeyTypeException, InvalidSessionKeyLengthException
    {
        String xmlEncryptionAlgorithm;

        xmlEncryptionAlgorithm = null;

        if (sessionCipherProperties.getKeyType().getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_DESEDE) == true)
        {
            switch (sessionCipherProperties.getKeyLength())
            {
                case XML_ENCRYPTION_KEY_LENGTH_DESEDE_168:

                    xmlEncryptionAlgorithm = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_DESEDE;

                    break;

                default:

                    throw new InvalidSessionKeyLengthException();
            }
        }
        else
        {
            if (sessionCipherProperties.getKeyType().getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_AES) == true)
            {
                switch (sessionCipherProperties.getKeyLength())
                {
                    case XML_ENCRYPTION_KEY_LENGTH_AES_128:

                        xmlEncryptionAlgorithm = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES128;

                        break;

                    case XML_ENCRYPTION_KEY_LENGTH_AES_192:

                        xmlEncryptionAlgorithm = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES192;

                        break;

                    case XML_ENCRYPTION_KEY_LENGTH_AES_256:

                        xmlEncryptionAlgorithm = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES256;

                        break;

                    default:

                        throw new InvalidSessionKeyLengthException();
                }
            }
            else
            {
                throw new UnsupportedKeyTypeException();
            }
        }

        return xmlEncryptionAlgorithm;
    }

    /**
     * Maps an encryption algorithm name to a new {@link SessionCipherProperties} object
     * @param xmlEncryptionAlgorithm The name of the encryption algorithm, taken from the constants held in {@link XmlEncryptionMethodAlgorithms}
     * @return The {@link SessionCipherProperties} object initialised for the specified encryption algorithm
     * @throws UnsupportedKeyTypeException Thrown if the algorithm specified is unsupported
     * @throws InvalidSessionKeyLengthException Thrown if the session key length is invalid
     */
    public static SessionCipherProperties map (String xmlEncryptionAlgorithm) throws UnsupportedKeyTypeException, InvalidSessionKeyLengthException
    {
        SessionCipherProperties sessionCipherProperties;
        KeyType keyType;
        int keyLength;

        sessionCipherProperties = null;
        keyType = null;
        keyLength = 0;

        if (xmlEncryptionAlgorithm.equalsIgnoreCase(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_DESEDE) == true)
        {
            keyType = KeyType.getInstance(KeyType.KEY_TYPE_DESEDE);

            keyLength = XML_ENCRYPTION_KEY_LENGTH_DESEDE_168;
        }
        else if (xmlEncryptionAlgorithm.equalsIgnoreCase(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES128) == true)
        {
            keyType = KeyType.getInstance(KeyType.KEY_TYPE_AES);

            keyLength = XML_ENCRYPTION_KEY_LENGTH_AES_128;
        }
        else if (xmlEncryptionAlgorithm.equalsIgnoreCase(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES192) == true)
        {
            keyType = KeyType.getInstance(KeyType.KEY_TYPE_AES);

            keyLength = XML_ENCRYPTION_KEY_LENGTH_AES_192;
        }
        else if (xmlEncryptionAlgorithm.equalsIgnoreCase(XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_AES256) == true)
        {
            keyType = KeyType.getInstance(KeyType.KEY_TYPE_AES);

            keyLength = XML_ENCRYPTION_KEY_LENGTH_AES_256;
        }
        else
        {
            throw new UnsupportedKeyTypeException();
        }

        sessionCipherProperties = new SessionCipherProperties(keyType, keyLength);

        return sessionCipherProperties;
    }

    /**
     * Maps a padding scheme onto an encryption method algorithm name
     * @param paddingScheme The PaddingScheme to map from
     * @return The encryption algorithm name
     * @throws UnsupportedPaddingSchemeException Thrown if an unsupported padding scheme is specified
     */
    public static String map (final PaddingScheme paddingScheme) throws UnsupportedPaddingSchemeException
    {
        String encryptionMethodAlgorithmName;

        encryptionMethodAlgorithmName = null;

        if (paddingScheme.getPaddingScheme().equalsIgnoreCase(PaddingScheme.PADDING_SCHEME_OAEP) == true)
        {
            encryptionMethodAlgorithmName = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_RSA_OAEP_MGF1;
        }
        else if (paddingScheme.getPaddingScheme().equalsIgnoreCase(PaddingScheme.PADDING_SCHEME_PKCS115) == true)
        {
            encryptionMethodAlgorithmName = XmlEncryptionMethodAlgorithms.ENCRYPTION_METHOD_ALGORITHM_RSA_PKCS15;
        }
        else
        {
            throw new UnsupportedPaddingSchemeException();
        }

        return encryptionMethodAlgorithmName;
    }
}