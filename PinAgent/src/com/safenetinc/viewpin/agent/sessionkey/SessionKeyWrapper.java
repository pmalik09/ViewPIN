// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/SessionKeyWrapper.java 1.1 2008/09/04 10:46:34IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;

/**
 * Class to handle asymmetric wrapping and unwrapping of symmetric session keys
 * 
 * @author Stuart Horler
 *
 *
 */
public class SessionKeyWrapper 
{
	private static Logger logger = Logger.getLogger(SessionKeyWrapper.class);
	
    private SessionKeyWrapper()
    {
    	super();
    }
    
    /**
     * Wrap a session key with a wrapping certificate
     * 
     * @param wrappingCertificateSubjectKeyIdentifier The SKI of the wrapping certificate
     * @param wrappingCertificate The certificate to use for wrapping
     * @param wrappingPaddingScheme The padding scheme for the wrapping/encryption
     * @param sessionCipherProperties The properties for the symmetric encryption
     * @param sessionKey The session/secret key to wrap
     * @return The {@link WrappedSessionKey} version of the session key
     * @throws InvalidKeyException Thrown if an invalid encryption key is specified
     * @throws NoSuchAlgorithmException Thrown if an invalid encryption algorithm is specified
     * @throws NoSuchPaddingException Thrown if an invalid padding algorithm is specified
     * @throws IllegalStateException Thrown if the cipher ends in an invalid state
     * @throws IllegalBlockSizeException Thrown if the block size for the cipher is invalid
     */
    public static WrappedSessionKey wrapSessionKey(SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier, 
        Certificate wrappingCertificate, PaddingScheme wrappingPaddingScheme, 
        SessionCipherProperties sessionCipherProperties, SecretKey sessionKey) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, IllegalStateException, IllegalBlockSizeException
	{
		WrappedSessionKey wrappedSessionKey;
		byte[] unencodedWrappedSessionKey;
	    
	    wrappedSessionKey = null;
	    unencodedWrappedSessionKey = null;
	    
	    getLogger().debug("wrapping certificate subject key identifier=" + wrappingCertificateSubjectKeyIdentifier.getHexEncoded());
		
	    // Wrap session key
        unencodedWrappedSessionKey = wrapSessionKey(wrappingCertificate,
            wrappingPaddingScheme, sessionKey);
    	
        // Encapsulate wrapped session key
	    wrappedSessionKey = new WrappedSessionKey(wrappingCertificateSubjectKeyIdentifier,
	        wrappingPaddingScheme, unencodedWrappedSessionKey,
	        sessionCipherProperties.getKeyType());
	    
        return wrappedSessionKey;	    
	}
	
	private static byte[] wrapSessionKey(Certificate wrappingCertificate, PaddingScheme wrappingPaddingScheme, SecretKey sessionKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalStateException, IllegalBlockSizeException
	{
		byte[] wrappedSessionKey;
		Cipher c;
		
		wrappedSessionKey = null;
		c = null;
		
		getLogger().debug("wrapping padding scheme=" + wrappingPaddingScheme.getPaddingScheme());
		
		getLogger().debug("session key algorithm=" + sessionKey.getAlgorithm());
		
		getLogger().debug("wrapping cipher transformation=" + wrappingPaddingScheme.getTransformation());
		
	    // Instantiate wrapping cipher
	    c = Cipher.getInstance(wrappingPaddingScheme.getTransformation());
		
	    // Initialise wrapping cipher
	    c.init(Cipher.WRAP_MODE, wrappingCertificate);
	    
	    getLogger().debug("wrapping session key");
	    
	    // Wrap session key
		wrappedSessionKey = c.wrap(sessionKey);
		
		getLogger().debug("wrapped session key");
		
		return wrappedSessionKey;
	}
    
    /**
     * Unwraps a wrapped session key
     * @param wrappedSessionKey The wrapped session key
     * @param wrappingPaddingScheme The padding scheme used when the key was wrapped
     * @param wrappingKey The wrapping key
     * @param sessionCipherProperties The properties associated with this cryptographic operation
     * @return The unwrapped session key
     * @throws NoSuchPaddingException Thrown if an invalid padding form is specified
     * @throws NoSuchAlgorithmException Thrown if an invalid algorithm is specified
     * @throws InvalidKeyException Thrown id the wrapping key is invalid
     */
    public static SecretKey unwrapSessionKey(byte[] wrappedSessionKey, PaddingScheme wrappingPaddingScheme, PrivateKey wrappingKey, SessionCipherProperties sessionCipherProperties) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKey sessionKey;
        Cipher c;
        
        sessionKey = null;
        c = null;
        
        getLogger().debug("wrapping padding scheme=" + wrappingPaddingScheme.getPaddingScheme());
        
        getLogger().debug("wrapping cipher transformation=" + wrappingPaddingScheme.getTransformation());
        
        getLogger().debug("session cipher key type = " + sessionCipherProperties.getKeyType().getKeyType());
        
        // Instantiate wrapping cipher
        c = Cipher.getInstance(wrappingPaddingScheme.getTransformation());
        
        // Initialise wrapping cipher
        c.init(Cipher.UNWRAP_MODE, wrappingKey);

        getLogger().debug("unwrapping session key");
        
        // Unwrap session key
        sessionKey = (SecretKey)c.unwrap(wrappedSessionKey, sessionCipherProperties.getKeyType().getKeyType(), Cipher.SECRET_KEY);
        
        getLogger().debug("unwrapped session key");
        
        return sessionKey;
    }
	
	private static Logger getLogger()
	{
		return SessionKeyWrapper.logger;
	}
}
