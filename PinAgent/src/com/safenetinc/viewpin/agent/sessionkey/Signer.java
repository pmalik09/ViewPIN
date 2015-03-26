// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/Signer.java 1.1 2008/09/04 10:46:36IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import java.security.PrivateKey;
import java.security.cert.Certificate;

import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;

/**
 * Class to store the attributes required for a signature operation
 * 
 * @author Stuart Horler
 *
 *
 */
public class Signer 
{
	private SubjectKeyIdentifier subjectKeyIdentifier = null;
	private PrivateKey signingKey = null;
	private Certificate signingCertificate = null;
	private String signatureMethodAlgorithm = null;
	
    /**
     * Constructor 
     * @param subjectKeyIdentifier The SKI to store
     * @param signingKey The signing private key to store
     * @param signingCertificate The signing certificate to store
     * @param signatureMethodAlgorithm The signature algorithm to use
     */
    public Signer(SubjectKeyIdentifier subjectKeyIdentifier, PrivateKey signingKey, Certificate signingCertificate, String signatureMethodAlgorithm)
    {
    	super();
    	
    	setSubjectKeyIdentifier(subjectKeyIdentifier);
    	setSigningKey(signingKey);
    	setSigningCertificate(signingCertificate);
    	setSignatureMethodAlgorithm(signatureMethodAlgorithm);
    }

	/**
	 * @return The signing key held by this class
	 */
	public PrivateKey getSigningKey() 
	{
		return this.signingKey;
	}

	private void setSigningKey(PrivateKey signingKey)
	{
		this.signingKey = signingKey;
	}

	/**
	 * @return The SKI held by this class
	 */
	public SubjectKeyIdentifier getSubjectKeyIdentifier() 
	{
		return this.subjectKeyIdentifier;
	}

	private void setSubjectKeyIdentifier(SubjectKeyIdentifier subjectKeyIdentifier) 
	{
		this.subjectKeyIdentifier = subjectKeyIdentifier;
	}
	
	private void setSigningCertificate(Certificate signingCertificate) 
	{
		this.signingCertificate = signingCertificate;
	}
	
	/**
	 * @return The signing certificate held by this class
	 */
	public Certificate getSigningCertificate() 
	{
		return this.signingCertificate;
	}

    /**
     * @return The signature algorithm held by this class
     */
    public String getSignatureMethodAlgorithm()
    {
        return this.signatureMethodAlgorithm;
    }

    private void setSignatureMethodAlgorithm(String signatureMethodAlgorithm)
    {
        this.signatureMethodAlgorithm = signatureMethodAlgorithm;
    }
}