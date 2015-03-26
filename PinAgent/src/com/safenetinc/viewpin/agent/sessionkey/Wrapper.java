// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/Wrapper.java 1.1 2008/09/04 10:46:39IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import java.security.PrivateKey;
import java.security.cert.Certificate;

import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;

/**
 * Class to represent an asymmetric wrapping object
 * 
 * @author Stuart Horler
 *
 *
 */
public class Wrapper
{
	private SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier = null;
	private Certificate wrappingCertificate = null;
	private PrivateKey wrappingKey = null;
	private PaddingScheme wrappingPaddingScheme = null;
	
    /**
     * Constructor
     * @param wrappingCertificateSubjectKeyIdentifier The SKU of the wrapping certificate
     * @param wrappingCertificate The wrapping certificate
     * @param wrappingKey The private wrapping key
     * @param wrappingPaddingScheme The padding scheme to be used when wrapping
     */
    public Wrapper(SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier, Certificate wrappingCertificate,
        PrivateKey wrappingKey, PaddingScheme wrappingPaddingScheme)
    {
    	super();
    	
    	setWrappingCertificateSubjectKeyIdentifier(wrappingCertificateSubjectKeyIdentifier);
    	setWrappingCertificate(wrappingCertificate);
        setWrappingKey(wrappingKey);
        setWrappingPaddingScheme(wrappingPaddingScheme);
    }
	
	private void setWrappingCertificateSubjectKeyIdentifier(SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier)
	{
		this.wrappingCertificateSubjectKeyIdentifier = wrappingCertificateSubjectKeyIdentifier;
	}

	/**
	 * @return The SKI of the wrapping certificate
	 */
	public SubjectKeyIdentifier getWrappingCertificateSubjectKeyIdentifier() 
	{
		return this.wrappingCertificateSubjectKeyIdentifier;
	}
	
	private void setWrappingCertificate(Certificate wrappingCertificate) 
	{
		this.wrappingCertificate = wrappingCertificate;
	}
	
	/**
	 * @return The wrapping certificate
	 */
	public Certificate getWrappingCertificate() 
	{
		return this.wrappingCertificate;
	}

	private void setWrappingKey(PrivateKey wrappingKey) 
	{
		this.wrappingKey = wrappingKey;
	}
	
	/**
	 * @return The private wrapping key
	 */
	public PrivateKey getWrappingKey() 
	{
		return this.wrappingKey;
	}
	
	private void setWrappingPaddingScheme(PaddingScheme wrappingPaddingScheme)
	{
		this.wrappingPaddingScheme = wrappingPaddingScheme;
	}
	
	/**
	 * @return The padding scheme that is to be used when wrapping
	 */
	public PaddingScheme getWrappingPaddingScheme() 
	{
		return this.wrappingPaddingScheme;
	}
}