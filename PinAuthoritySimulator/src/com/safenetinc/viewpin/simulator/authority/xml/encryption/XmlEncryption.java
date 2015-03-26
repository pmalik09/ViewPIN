package com.safenetinc.viewpin.simulator.authority.xml.encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.safenetinc.viewpin.simulator.authority.Utils;
import com.safenetinc.viewpin.simulator.authority.ViewPinConstants;

/**
 * Class to support XML Encryption
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class XmlEncryption
{
    private static final Logger logger = Logger.getLogger(XmlEncryption.class);

    private XmlEncryption()
    {
        super();
    }

    /**
     * Decrypts an encrypted XML Element
     * 
     * @param sessionKey The key to use
     * @param encryptedDataElement The element to decrypt
     * @return String containing the results of the decryption
     * @throws XPathExpressionException Thrown if locating the encrypted elements was not possible
     * @throws NoSuchAlgorithmException Thrown if an invalid encryption algorithm is specified
     * @throws InvalidKeyException Thrown if the secret/session key is invalid
     * @throws NoSuchPaddingException Thrown if the padding format is unknown or invalid
     * @throws IllegalBlockSizeException Thrown if the encrypted block size is invalid
     * @throws InvalidAlgorithmParameterException Thrown if the parameters are invalid for the specified
     *         algorithm
     * @throws BadPaddingException Thrown if the padding is invalid
     * @throws XPathFactoryConfigurationException Thrown if there is a problem invoking the XPath engine
     * 
     */
    public static String decryptEncryptedData (SecretKey sessionKey, Element encryptedDataElement) throws XPathExpressionException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, XPathFactoryConfigurationException
    {
        XPath xp;
        String encodedCipherValue;
        byte[] decodedCipherValue;
        String encryptionMethodAlgorithm;
        String sessionCipherAlgorithmName;
        String decryptedCipherValue;

        xp = null;
        encodedCipherValue = null;
        decodedCipherValue = null;
        encryptionMethodAlgorithm = null;
        sessionCipherAlgorithmName = null;
        decryptedCipherValue = null;

        // Initialise XPath
        xp = Utils.createXPath();

        // Get encoded cipher value
        encodedCipherValue = (String) xp.evaluate("xenc:CipherData/xenc:CipherValue/text()", encryptedDataElement, XPathConstants.STRING);

        // Decode cipher value
        decodedCipherValue = Base64.decodeBase64(encodedCipherValue.getBytes());

        // Get encryption method algorithm
        encryptionMethodAlgorithm = (String) xp.evaluate("xenc:EncryptionMethod/@Algorithm", encryptedDataElement, XPathConstants.STRING);

        // Get encrytion algorithm name
        sessionCipherAlgorithmName = XmlEncryptionAlgorithmMapper.getAlgorithm(encryptionMethodAlgorithm);

        // Decrypt cipher value
        decryptedCipherValue = decrypt(sessionKey, decodedCipherValue, sessionCipherAlgorithmName);

        return decryptedCipherValue;
    }

    private static String decrypt (SecretKey sessionKey, byte[] cipherTextPlusIv, String sessionCipherAlgorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException
    {
        StringBuffer clearText;
        Cipher sessionCipher;
        int sessionCipherBlockSize;
        String sessionCipherTransformation;
        byte[] iv;
        IvParameterSpec ivps;
        byte[] cipherTextMinusIv;
        int totalCipherTextBlocks;
        byte[] nextClearTextBlock;
        int totalNonPaddingCipherTextBlocks;
        byte[] lastClearTextBlock;
        int totalPaddingBytes;
        int totalNonPaddingBytes;
        byte[] nonPaddingBytes;

        clearText = null;
        sessionCipher = null;
        sessionCipherBlockSize = 0;
        sessionCipherTransformation = null;
        iv = null;
        ivps = null;
        cipherTextMinusIv = null;
        totalCipherTextBlocks = 0;
        nextClearTextBlock = null;
        totalNonPaddingCipherTextBlocks = 0;
        lastClearTextBlock = null;
        totalPaddingBytes = 0;
        totalNonPaddingBytes = 0;
        nonPaddingBytes = null;

        // Build session cipher transformation
        sessionCipherTransformation = sessionCipherAlgorithm + "/CBC/NoPadding";

        // Instantiate session cipher
        sessionCipher = Cipher.getInstance(sessionCipherTransformation);

        getLogger().debug("session cipher transformation = " + sessionCipherTransformation);

        // Get session cipher block size
        sessionCipherBlockSize = sessionCipher.getBlockSize();

        getLogger().debug("session cipher block size = " + sessionCipherBlockSize);

        getLogger().debug("cipher text plus initialisation vector length = " + cipherTextPlusIv.length);

        // Ensure we have an initialisation vector and at least one cipher text block
        if (cipherTextPlusIv.length < (sessionCipherBlockSize * 2))
        {
            // We do not have an initialisation vector and at least one cipher text block
            throw new IllegalBlockSizeException("initialisation vector and at least one cipher text block is required");
        }

        // Extract intialisation vector
        iv = new byte[sessionCipherBlockSize];
        System.arraycopy(cipherTextPlusIv, 0, iv, 0, iv.length);
        ivps = new IvParameterSpec(iv);

        getLogger().debug("intialisation vector = " + new String(Hex.encodeHex(iv)));

        // Intialise session cipher
        sessionCipher.init(Cipher.DECRYPT_MODE, sessionKey, ivps);

        // Create buffer to hold cipher text minus initialisation vector
        cipherTextMinusIv = new byte[cipherTextPlusIv.length - sessionCipherBlockSize];

        // Remove intialisation vector from cipher text
        System.arraycopy(cipherTextPlusIv, sessionCipherBlockSize, cipherTextMinusIv, 0, cipherTextMinusIv.length);

        // Ensure length of cipher text is a multiple of the session key block size
        if ((cipherTextMinusIv.length % sessionCipherBlockSize) != 0)
        {
            throw new IllegalBlockSizeException("cipher text is not a multiple of session key block size");
        }

        // Calculate total number of cipher blocks
        totalCipherTextBlocks = cipherTextMinusIv.length / sessionCipherBlockSize;

        getLogger().debug("cipher text length minus initialisation vector = " + cipherTextMinusIv.length);

        getLogger().debug("total cipher text blocks = " + totalCipherTextBlocks);

        // Work out how many non padding cipher text blocks we have
        totalNonPaddingCipherTextBlocks = totalCipherTextBlocks - 1;

        getLogger().debug("total non padding cipher text blocks = " + totalNonPaddingCipherTextBlocks);

        clearText = new StringBuffer();

        // Work through each non padding cipher text block
        for (int i = 0; i < totalNonPaddingCipherTextBlocks; i++)
        {
            // Decrypt next non padding cipher text block
            nextClearTextBlock = sessionCipher.update(cipherTextMinusIv, i * sessionCipherBlockSize, sessionCipherBlockSize);

            getLogger().debug("next clear text block = " + new String(nextClearTextBlock));

            clearText.append(new String(nextClearTextBlock));
        }

        // Decrypt last cipher text block
        lastClearTextBlock = sessionCipher.doFinal(cipherTextMinusIv, totalNonPaddingCipherTextBlocks * sessionCipherBlockSize, sessionCipherBlockSize);

        getLogger().debug("last clear text block = " + new String(Hex.encodeHex(lastClearTextBlock)));

        // Determine total number of padding bytes in last clear text block
        totalPaddingBytes = lastClearTextBlock[sessionCipherBlockSize - 1];

        getLogger().debug("total padding bytes in last clear text block = " + totalPaddingBytes);

        // Ensure total padding padding bytes is between one and session cipher block size
        if ((totalPaddingBytes < 1) || (totalPaddingBytes > sessionCipherBlockSize))
        {
            // Total padding bytes is not between one and session cipher block size
            throw new BadPaddingException("total padding bytes is not between one and session cipher block size");
        }

        // Work out how many bytes of last clear text block are non padding bytes
        totalNonPaddingBytes = sessionCipherBlockSize - totalPaddingBytes;

        getLogger().debug("total non padding bytes = " + totalNonPaddingBytes);

        // Are there any non padding bytes in the last cipher text block?
        if (totalNonPaddingBytes > 0)
        {
            // There are non padding bytes in the last cipher text block
            nonPaddingBytes = new byte[totalNonPaddingBytes];

            // Get non padding bytes in last cipher text block
            System.arraycopy(lastClearTextBlock, 0, nonPaddingBytes, 0, nonPaddingBytes.length);

            clearText.append(new String(nonPaddingBytes));
        }
        else
        {
            getLogger().debug("no non padding bytes in last block");
        }

        return clearText.toString();
    }

    /**
     * Gets a session key from an encrypted XML Document
     * 
     * @param pinRetrievalRequestDocument The document in question
     * @param sessionCipherEncryptionMethodAlgorithm The type of encryption in use
     * @param authorityKeyStore The key store to load the asymmetric wrapping key pair/certificate from
     * @return The session key
     * @throws KeyStoreException Thrown if there is a problem reading from the Key Store
     * @throws NoSuchAlgorithmException Thrown if an invalid encryption algorithm is specified
     * @throws UnrecoverableKeyException Thrown if unable to recover the secret/session key
     * @throws XPathExpressionException Thrown if locating the encrypted elements was not possible
     * @throws NoSuchPaddingException Thrown if the padding format is unknown or invalid
     * @throws InvalidKeyException Thrown if the secret/session key is invalid
     * @throws XPathFactoryConfigurationException Thrown if there is a problem invoking the XPath engine
     */
    public static SecretKey recoverSessionKey (Document pinRetrievalRequestDocument, String sessionCipherEncryptionMethodAlgorithm, KeyStore authorityKeyStore) throws KeyStoreException,
            NoSuchAlgorithmException, UnrecoverableKeyException, XPathExpressionException, NoSuchPaddingException, InvalidKeyException, XPathFactoryConfigurationException
    {
        SecretKey sessionKey;
        XPath xp;
        String encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier;
        byte[] pinAuthorityWrappingCertificateSubjectKeyIdentifier;
        String hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier;
        Element encryptedKeyElement;
        String wrappingKeyAlgorithm;
        String wrappingCipherTransformation;
        PrivateKey authorityWrappingKey;
        String encodedWrappedSessionKey;
        byte[] decodedWrappedSessionKey;
        Cipher unwrappingCipher;
        String sessionKeyAlgorithm;

        sessionKey = null;
        xp = null;
        encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier = null;
        pinAuthorityWrappingCertificateSubjectKeyIdentifier = null;
        hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier = null;
        encryptedKeyElement = null;
        wrappingKeyAlgorithm = null;
        wrappingCipherTransformation = null;
        authorityWrappingKey = null;
        encodedWrappedSessionKey = null;
        decodedWrappedSessionKey = null;
        unwrappingCipher = null;
        sessionKeyAlgorithm = null;

        // Initialise XPath
        xp = Utils.createXPath();

        // Get pin authority wrapping certificate subject key identifier
        encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier = (String) xp.evaluate("/vp:PinRetrievalRequest/xenc:EncryptedKey/ds:KeyInfo/ds:X509Data/ds:X509SKI/text()",
                pinRetrievalRequestDocument, XPathConstants.STRING);

        // Did we get pin authority wrapping certificate subject key identifier OK?
        if (encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier == null)
        {
            // Failed to get pin authority wrapping certificate subject key identifier
            getLogger().error("retrieving pin authority wrapping certificate subject key identifier");

            return null;
        }

        getLogger().debug("Base64 encoded pin authority wrapping certificate subject key identifier = " + encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier);

        // Ensure pin authority wrapping certificate subject key identifier is Base64 encoded
        if (Base64.isArrayByteBase64(encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier.getBytes()) == false)
        {
            // Pin authority wrapping certificate subject key identifier is not Base64 encoded
            getLogger().error("pin authority wrapping certificate subject key identifier is not Base64 encoded");

            return null;
        }

        // Decode pin authority wrapping certificate subject key identifier
        pinAuthorityWrappingCertificateSubjectKeyIdentifier = Base64.decodeBase64(encodedPinAuthorityWrappingCertificateSubjectKeyIdentifier.getBytes());

        // Hex encode pin authority wrapping certificate subject key identifier
        hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier = new String(Hex.encodeHex(pinAuthorityWrappingCertificateSubjectKeyIdentifier));

        getLogger().debug("hex encoded pin authority wrapping certificate subject key identifier = " + hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier);

        // Get EncryptedKey element
        encryptedKeyElement = (Element) xp.evaluate("/vp:PinRetrievalRequest/xenc:EncryptedKey[@Id = 'EK']", pinRetrievalRequestDocument, XPathConstants.NODE);

        // Did we get EncryptedKey element OK?
        if (encryptedKeyElement == null)
        {
            // Failed to get EncryptedKey element
            getLogger().error("retrieving EncryptedKey element");

            return null;
        }

        // Get wrapping cipher algorithm
        wrappingKeyAlgorithm = (String) xp.evaluate("xenc:EncryptionMethod/@Algorithm", encryptedKeyElement, XPathConstants.STRING);

        // Did we get wrapping cipher algorithm OK?
        if (wrappingKeyAlgorithm == null)
        {
            // Failed to get wrapping cipher algorithm
            getLogger().error("retrieving wrapper cipher algorithm");

            return null;
        }

        getLogger().debug("wrapping cipher algoritm = " + wrappingKeyAlgorithm);

        try
        {
            // Determine wrapping cipher transformation
            wrappingCipherTransformation = XmlEncryptionAlgorithmMapper.getTransformation(wrappingKeyAlgorithm);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            getLogger().debug("no such wrapping cipher transformation " + wrappingKeyAlgorithm);

            return null;
        }

        getLogger().debug("wrapping cipher transformation = " + wrappingCipherTransformation);

        // Retrieve authority wrapping key from key store
        authorityWrappingKey = (PrivateKey) authorityKeyStore.getKey(hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier, ViewPinConstants.DEFAULT_KEYSTORE_PASSWORD.toCharArray());

        // Did we retrieve authority wrapping key from key store OK?
        if (authorityWrappingKey == null)
        {
            // Failed to retrieve authority wrapping key from key store
            getLogger().error("failed to retrieve authority wrapping key " + hexEncodedPinAuthorityWrappingCertificateSubjectKeyIdentifier);

            throw new KeyStoreException();
        }

        // Get encoded wrapped session key
        encodedWrappedSessionKey = (String) xp.evaluate("/vp:PinRetrievalRequest/xenc:EncryptedKey[@Id = 'EK']/xenc:CipherData/xenc:CipherValue/text()", pinRetrievalRequestDocument,
                XPathConstants.STRING);

        // Did we get encoded wrapped session key OK?
        if (encodedWrappedSessionKey == null)
        {
            // Failed to get encoded wrapped session key
            getLogger().error("retreiving encoded wrapped session key");

            return null;
        }

        // Ensure encoded wrapped session key is Base64 encoded
        if (Base64.isArrayByteBase64(encodedWrappedSessionKey.getBytes()) == false)
        {
            // Encoded wrapped session key is not Base64 encoded
            getLogger().error("encoded wrapped session key is not Base64 encoded");

            return null;
        }

        // Decode wrapped Session key
        decodedWrappedSessionKey = Base64.decodeBase64(encodedWrappedSessionKey.getBytes());

        // Instantiate wrapping cipher
        unwrappingCipher = Cipher.getInstance(wrappingCipherTransformation);
        unwrappingCipher.init(Cipher.UNWRAP_MODE, authorityWrappingKey);

        getLogger().debug("unmapped session key cipher algorithm = " + sessionCipherEncryptionMethodAlgorithm);

        try
        {
            // Determine session key cipher algorithm
            sessionKeyAlgorithm = XmlEncryptionAlgorithmMapper.getAlgorithm(sessionCipherEncryptionMethodAlgorithm);
        }
        catch (NoSuchAlgorithmException nsae)
        {
            getLogger().error("mapping session key cipher algorithm " + sessionCipherEncryptionMethodAlgorithm);

            return null;
        }

        getLogger().debug("mapped session key cipher algorithm = " + sessionKeyAlgorithm);

        // Unwrap session key
        sessionKey = (SecretKey) unwrappingCipher.unwrap(decodedWrappedSessionKey, sessionKeyAlgorithm, Cipher.SECRET_KEY);

        return sessionKey;
    }

    private static Logger getLogger ()
    {
        return logger;
    }
}