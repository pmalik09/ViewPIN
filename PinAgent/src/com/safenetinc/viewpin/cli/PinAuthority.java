package com.safenetinc.viewpin.cli;

/**
 * Class to set and get methods for the PINAgent's PINAuthority
 * @author Pratibha Malik
 */
public class PinAuthority {
	private String PINAuthorityName = null;
	
	private String PINAuthoritySigningCertSKI = null;
	
	private String PINAuthorityWrappingCertSKI = null;
	
	private String PINAuthorityRedirectionURL = null;
	
	private String wrappingPaddingScheme = null;

	private String algorithmName = null;
	
	private String bitLength = null;
	
	
	
	public PinAuthority()
	{
		
		this.algorithmName = null;
		this.wrappingPaddingScheme = null;
		this.PINAuthorityWrappingCertSKI = null;
		this.PINAuthoritySigningCertSKI = null;
		this.PINAuthorityRedirectionURL = null;
		this.PINAuthorityName = null;
		this.bitLength = null;
		
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthorityName
	 */ 
	public void setPINAuthorityName(String PINAuthorityName)
	{
		this.PINAuthorityName = PINAuthorityName;
		
	}
	
	public String getPINAuthorityName()
	{
		return this.PINAuthorityName;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthoritySigningCertificateSKI 
	 */ 
	public void setPINAuthoritySigningCertSKI(String PINAuthoritySigningCertificateSKI)
	{
		this.PINAuthoritySigningCertSKI = PINAuthoritySigningCertificateSKI;
		
	}
	
	public String getPINAuthoritySigningCertSKI()
	{
		return this.PINAuthoritySigningCertSKI;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthorityWrappingCertificateSKI 
	 */ 
	public void setPINAuthorityWrappingCertSKI(String PINAuthorityWrappingCertificateSKI)
	{
		this.PINAuthorityWrappingCertSKI = PINAuthorityWrappingCertificateSKI;
		
	}
	
	public String getPINAuthorityWrappingCertSKI()
	{
		return this.PINAuthorityWrappingCertSKI;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthorityErrorRedirectionURL
	 */ 
	public void setPINAuthorityRedirectionURL(String PINAuthorityRedirectionURL)
	{
		this.PINAuthorityRedirectionURL = PINAuthorityRedirectionURL;
		
	}
	
	public String getPINAuthorityRedirectionURL()
	{
		return this.PINAuthorityRedirectionURL;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthority Wrapping Padding Scheme
	 */ 
	public void setPINAuthorityWrappingPaddingScheme(String wrappingPaddingScheme)
	{
		this.wrappingPaddingScheme = wrappingPaddingScheme;
		
	}
	
	public String getPINAuthorityWrappingPaddingScheme()
	{
		return this.wrappingPaddingScheme;
	}
	

	/*
	 * Get and set methods
	 * for setting and getting the PINAuthority AlgorithmName
	 */ 
	public void setPINAuthorityAlgorithmName(String algorithmName)
	{
		this.algorithmName = algorithmName;
		
	}
	
	public String getPINAuthorityAlgorithmName()
	{
		return this.algorithmName;
	}
	
	/*
	 * Get and set methods
	 * for setting and getting the PINAuthority BitLength
	 */ 
	public void setPINAuthorityBitLength(String bitLength)
	{
		this.bitLength = bitLength;
		
	}
	
	public String getPINAuthorityBitLength()
	{
		return this.bitLength;
	}
	
	
	
}
