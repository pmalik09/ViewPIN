// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/WrappedSessionKey.java 1.1 2008/09/04 10:46:38IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import org.apache.commons.codec.binary.Base64;

import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;
import com.safenetinc.viewpin.common.utils.UrlSafeBase64;

/**
 * Class representing a wrapped session key
 * 
 * @author Stuart Horler
 *
 *
 */
public class WrappedSessionKey 
{
	private SubjectKeyIdentifier subjectKeyIdentifier = null;
    private PaddingScheme paddingScheme = null;
    private byte[] wrappedSessionKey = null;
    private String encodedWrappedSessionKey = null;
    private KeyType sessionKeyType = null;
    private String safeEncodedWrappedSessionKey = null;

	/**
	 * Constructor
	 * @param subjectKeyIdentifier The SKI of the certificate that wraps this key
	 * @param paddingScheme The padding scheme of the wrapping
	 * @param wrappedSessionKey The wrapped key
	 * @param sessionKeyType The type of the wrapped key
	 */
	public WrappedSessionKey(SubjectKeyIdentifier subjectKeyIdentifier, PaddingScheme paddingScheme, byte[] wrappedSessionKey,
        KeyType sessionKeyType)
    {
        super();

        setSubjectKeyIdentifier(subjectKeyIdentifier);
        setPaddingScheme(paddingScheme);
        setWrappedSessionKey(wrappedSessionKey);
        setSessionKeyType(sessionKeyType);

        // Encode wrapped session key
        setEncodedWrappedSessionKey(new String(Base64.encodeBase64(getWrappedSessionKey())));
        
        // Safe encode wrapped session key
        setSafeEncodedWrappedSessionKey(UrlSafeBase64.encode(getWrappedSessionKey()));
    }

	private void setSubjectKeyIdentifier(SubjectKeyIdentifier subjectKeyIdentifier)
    {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    /**
     * @return The SKI of the wrapping certificate
     */
    public SubjectKeyIdentifier getSubjectKeyIdentifier()
    {
        return this.subjectKeyIdentifier;
    }

    private void setPaddingScheme(PaddingScheme paddingScheme)
    {
        this.paddingScheme = paddingScheme;
    }

    /**
     * @return The padding scheme used for the wrapping
     */
    public PaddingScheme getPaddingScheme()
    {
        return this.paddingScheme;
    }

    private void setWrappedSessionKey(byte[] wrappedSessionKey)
    {
        this.wrappedSessionKey = wrappedSessionKey;
    }

    /**
     * @return The wrapped session key
     */
    public byte[] getWrappedSessionKey()
    {
        return this.wrappedSessionKey.clone();
    }

    private void setEncodedWrappedSessionKey(String encodedWrappedSessionKey)
    {
        this.encodedWrappedSessionKey = encodedWrappedSessionKey;
    }

    /**
     * @return The encoded, wrapped session key
     */
    public String getEncodedWrappedSessionKey()
    {
        return this.encodedWrappedSessionKey;
    }

    private void setSessionKeyType(KeyType sessionKeyType)
    {
        this.sessionKeyType = sessionKeyType;
    }

    /**
     * @return The session key type
     */
    public KeyType getSessionKeyType()
    {
        return this.sessionKeyType;
    }

    private void setSafeEncodedWrappedSessionKey(String safeEncodedWrappedSessionKey) 
    {
		this.safeEncodedWrappedSessionKey = safeEncodedWrappedSessionKey;
	}
    
	/**
	 * @return The safe base64 encoded wrapped session key
	 */
	public String getSafeEncodedWrappedSessionKey() 
	{
		return this.safeEncodedWrappedSessionKey;
	}
}