// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinAuthority.java 1.2 2012/07/30 13:51:19IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import java.net.URL;
import java.security.cert.Certificate;

import com.safenetinc.viewpin.agent.sessionkey.PaddingScheme;
import com.safenetinc.viewpin.agent.sessionkey.SessionCipherProperties;
import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;


/**
 * Class to represent a PINAuthority
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class PinAuthority
{
    private SubjectKeyIdentifier    signingCertificateSubjectKeyIdentifier  = null;
	
	private String				    authorityName							 = null;

    private Certificate             signingCertificate                      = null;

    private SubjectKeyIdentifier    wrappingCertificateSubjectKeyIdentifier = null;

    private Certificate             wrappingCertificate                     = null;

    private PaddingScheme           wrappingPaddingScheme                   = null;

    private SessionCipherProperties sessionCipherProperties                 = null;

    private URL                     redirectionUrl                          = null;
	


    PinAuthority(String authorityName, SubjectKeyIdentifier signingCertificateSubjectKeyIdentifier, Certificate signingCertificate, SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier,
            Certificate wrappingCertificate, PaddingScheme wrappingPaddingScheme, SessionCipherProperties sessionCipherProperties, URL redirectionUrl)
    {
        super();
		setAuthorityName(authorityName);
        setSigningCertificateSubjectKeyIdentifier(signingCertificateSubjectKeyIdentifier);
        setSigningCertificate(signingCertificate);
        setWrappingCertificateSubjectKeyIdentifier(wrappingCertificateSubjectKeyIdentifier);
        setWrappingCertificate(wrappingCertificate);
        setWrappingPaddingScheme(wrappingPaddingScheme);
        setSessionCipherProperties(sessionCipherProperties);
        setRedirectionUrl(redirectionUrl);
    }

	
    public String getAuthorityName ()
    {
        return this.authorityName;
    }

	 /**
     * @return The Name of the Authority
     */
    private void setAuthorityName (String authorityName)
    {
        this.authorityName = authorityName;
    }
	
    private void setSigningCertificateSubjectKeyIdentifier (SubjectKeyIdentifier signingCertificateSubjectKeyIdentifier)
    {
        this.signingCertificateSubjectKeyIdentifier = signingCertificateSubjectKeyIdentifier;
    }

    /**
     * @return The SKI of the Authority's signing certificate
     */
    public SubjectKeyIdentifier getSigningCertificateSubjectKeyIdentifier ()
    {
        return this.signingCertificateSubjectKeyIdentifier;
    }

    private void setSigningCertificate (Certificate signingCertificate)
    {
        this.signingCertificate = signingCertificate;
    }

    /**
     * @return The Authority's signing certificate
     */
    public Certificate getSigningCertificate ()
    {
        return this.signingCertificate;
    }

    private void setWrappingCertificateSubjectKeyIdentifier (SubjectKeyIdentifier wrappingCertificateSubjectKeyIdentifier)
    {
        this.wrappingCertificateSubjectKeyIdentifier = wrappingCertificateSubjectKeyIdentifier;
    }

    /**
     * @return The SKI of the Authority's wrapping certificate
     */
    public SubjectKeyIdentifier getWrappingCertificateSubjectKeyIdentifier ()
    {
        return this.wrappingCertificateSubjectKeyIdentifier;
    }

    private void setWrappingCertificate (Certificate wrappingCertificate)
    {
        this.wrappingCertificate = wrappingCertificate;
    }

    /**
     * @return The Authority's wrapping certificate
     */
    public Certificate getWrappingCertificate ()
    {
        return this.wrappingCertificate;
    }

    private void setWrappingPaddingScheme (PaddingScheme wrappingPaddingScheme)
    {
        this.wrappingPaddingScheme = wrappingPaddingScheme;
    }

    /**
     * @return The wrapping padding scheme in use by the Authority
     */
    public PaddingScheme getWrappingPaddingScheme ()
    {
        return this.wrappingPaddingScheme;
    }

    private void setSessionCipherProperties (SessionCipherProperties sessionCipherProperties)
    {
        this.sessionCipherProperties = sessionCipherProperties;
    }

    /**
     * @return The {@link SessionCipherProperties} object representing the session encryption the Authority
     *         uses
     */
    public SessionCipherProperties getSessionCipherProperties ()
    {
        return this.sessionCipherProperties;
    }

    URL getRedirectionUrl ()
    {
        return this.redirectionUrl;
    }

    private void setRedirectionUrl (URL redirectionUrl)
    {
        this.redirectionUrl = redirectionUrl;
    }
	

}