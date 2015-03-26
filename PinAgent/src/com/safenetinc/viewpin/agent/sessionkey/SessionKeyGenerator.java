// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/SessionKeyGenerator.java 1.1 2008/09/04 10:46:33IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.agent.sessionkey.exceptions.InvalidSessionKeyLengthException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedKeyTypeException;
import com.safenetinc.viewpin.common.xencsigmap.XmlEncryptionAlgorithmMapper;

/**
 * Class to handle generation of a session key
 * 
 * @author Stuart Horler
 *
 *
 */
public class SessionKeyGenerator 
{
	private static Logger logger = Logger.getLogger(SessionKeyGenerator.class);
	
    private SessionKeyGenerator()
    {
    	super();
    }
    
    /**
     * Generates a symmetric session key
     * @param sessionCipherProperties The properties associated with the key
     * @return The newly generated session key
     * @throws NoSuchAlgorithmException Thrown if an invalid algorithm is specified
     * @throws NoSuchProviderException Thrown if an invalid provider is specified
     * @throws UnsupportedKeyTypeException Thrown if the key type specified is unsupported
     * @throws InvalidSessionKeyLengthException Thrown if the specified session key length is invalid
     */
    public static SecretKey generateSessionKey(SessionCipherProperties sessionCipherProperties) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedKeyTypeException, InvalidSessionKeyLengthException
	{
		SecretKey sessionKey;
		String encryptionMethodAlgorithm;
		KeyGenerator kg;
		
		sessionKey = null;
		encryptionMethodAlgorithm = null;
		kg = null;
		
		getLogger().debug("session cipher key type = " + sessionCipherProperties.getKeyType().getKeyType());
		getLogger().debug("session cipher key length = " + sessionCipherProperties.getKeyLength());
		  
		// Get session cipher encryption method algorithm
		encryptionMethodAlgorithm = XmlEncryptionAlgorithmMapper.map(sessionCipherProperties);
		
		getLogger().debug("session cipher encryption method algorithm = " + encryptionMethodAlgorithm);
		
		// Get key generator instance
		kg = KeyGeneratorFactory.getInstance(encryptionMethodAlgorithm);
		
		getLogger().debug("generating session key");
		
        // Generate session key
        sessionKey = kg.generateKey();
        
        getLogger().debug("generated session key");
        
		return sessionKey;
	}

	private static Logger getLogger() 
	{
		return logger;
	}
}
