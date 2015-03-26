package com.safenetinc.viewpin.cli; 



import java.util.LinkedList;


/*this class lists the get and set methods
 * for the agent configuration file elements
 */
public class PinAgentConfigurationElements {
	
	private static String PINAgentName = null;
	
	private static String PINAgentSigningCertSKI = null;
	
	private static String PINAgentSSLCertSKI = null;
	
	private static String PINAgentWrappingCertSKI = null;
	
	private static String PINAgentErrorRedirectionURL = null;
	
	private static String PINAgentWrappedSessionCookieDomain = null;
	
	private static String  PINRetrievalRequestCookie = null;
	
	private static String PINRetrievalResponseCookie = null;
	
	private static String  PINChangeRequestCookie = null;
		
	private static int PINAuthorityCount = 0;
	
	private static int portNumber = 0;
	
	private static String SignatureMethodAlgorithm = null;
	
	private static String DigestMethodAlgorithm = null;
	
	private static int ReplayWindow = 0;
	
	private static LinkedList<PinAuthority> pinAuthorities = new LinkedList<PinAuthority>();
	
	public PinAgentConfigurationElements()
	{
		
	}
	
	/* 
	 * Get and set methods
	 * for setting and getting the PINAgentname 
	 */ 
	public void setPINAgentName(String PINAgentName)
	{
		this.PINAgentName = PINAgentName;
	}
	
	public String getPINAgentName()
	{
		return this.PINAgentName;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgentSigningCertificateSKI 
	 */ 
	public void setPINAgentSigningCertSKI(String PINAgentSigningCertSKI)
	{
		this.PINAgentSigningCertSKI = PINAgentSigningCertSKI;
		
	}
	
	public String getPINAgentSigningCertSKI()
	{
		return this.PINAgentSigningCertSKI;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the SignatureMethodAlgorithm 
	 */ 
	public void setPINAgentSignatureMethodAlgorithm(String SignatureMethodAlgorithm)
	{
		this.SignatureMethodAlgorithm = SignatureMethodAlgorithm;
		
	}
	
	public String getPINAgentSignatureMethodAlgorithm()
	{
		return this.SignatureMethodAlgorithm;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the DigestMethodAlgorithm 
	 */ 
	public void setPINAgentDigestMethodAlgorithm(String DigestMethodAlgorithm)
	{
		this.DigestMethodAlgorithm = DigestMethodAlgorithm;
		
	}
	
	public String getPINAgentDigestMethodAlgorithm()
	{
		return this.DigestMethodAlgorithm;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgentWrappingCertificateSKI 
	 */ 
	public void setPINAgentWrappingCertSKI(String PINAgentWrappingCertSKI)
	{
		this.PINAgentWrappingCertSKI = PINAgentWrappingCertSKI;
		
	}
	
	public String getPINAgentWrappingCertSKI()
	{
		return this.PINAgentWrappingCertSKI;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgentErrorRedirectionURL
	 */ 
	public void setPINAgentErrorRedirectionURL(String PINAgentErrorRedirectionURL)
	{
		this.PINAgentErrorRedirectionURL = PINAgentErrorRedirectionURL;
		
	}
	
	public String getPINAgentErrorRedirectionURL()
	{
		return this.PINAgentErrorRedirectionURL;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgentWrappedSessionCookieDomain
	 */ 
	public void setPINAgentWrappedSessionCookieDomain(String PINAgentWrappedSessionCookieDomain)
	{
		this.PINAgentWrappedSessionCookieDomain = PINAgentWrappedSessionCookieDomain;
		
	}
	
	public String getPINAgentWrappedSessionCookieDomain()
	{
		return this.PINAgentWrappedSessionCookieDomain;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINRetrievalRequestCookie
	 */ 
	public void setPINRetrievalRequestCookie(String PINRetrievalRequestCookie)
	{
		this.PINRetrievalRequestCookie = PINRetrievalRequestCookie;
		
	}
	
	public String getPINRetrievalRequestCookie()
	{
		return this.PINRetrievalRequestCookie;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINRetrievalResponseCookie
	 */ 
	public void setPINRetrievalResponseCookie(String PINRetrievalResponseCookie)
	{
		this.PINRetrievalResponseCookie = PINRetrievalResponseCookie;
		
	}
	
	public String getPINRetrievalResponseCookie()
	{
		return this.PINRetrievalResponseCookie;
	}
	
	
	/*
	 * Get and set methods
	 * for setting and getting the PINChangeRequestCookie
	 */ 
	public void setPINChangeRequestCookie(String PINChangeRequestCookie)
	{
		this.PINChangeRequestCookie = PINChangeRequestCookie;
		
	}
	
	public String getPINChangeRequestCookie()
	{
		return this.PINChangeRequestCookie;
	}
	/*
	 * Get and set methods
	 * for setting and getting the ReplayWindow
	 */ 
	public void setReplayWindow(int ReplayWindow)
	{
		this.ReplayWindow = ReplayWindow;
		
	}
	
	public int getReplayWindow()
	{
		return this.ReplayWindow;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthority count
	 */ 
	public void setPINAuthorityCount(int count)
	{
		this.PINAuthorityCount = count;
		
	}
	
	public int getPINAuthorityCount()
	{
		return this.PINAuthorityCount;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting Linked List PinAuthorities
	 */ 
	

	public LinkedList<PinAuthority> getPinAuthorites()
	{
		return this.pinAuthorities ;
	}
	
	
	public void setPinAuthorities(LinkedList<PinAuthority> pinAuthorities)
	{
		this.pinAuthorities = pinAuthorities;
		
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgentSSLSKI 
	 */ 
	public void setPINAgentSSLCertSKI(String PINAgentSSLCertSKI)
	{
		this.PINAgentSSLCertSKI = PINAgentSSLCertSKI;
		
	}
	
	public String getPINAgentSSLCertSKI()
	{
		return this.PINAgentSSLCertSKI;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAgent port
	 */ 
	public void setPINAgentPortNumber(int port)
	{
		this.portNumber = port;
		
	}
	
	public int getPINAgentPortNumber()
	{
		return this.portNumber;
	}
	
}



