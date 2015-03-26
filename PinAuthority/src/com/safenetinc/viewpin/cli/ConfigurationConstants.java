package com.safenetinc.viewpin.cli;

/** 
  *	class to hold the constant used by the view and edit PinAuthority configuration 
*/
public class ConfigurationConstants {
	
	public ConfigurationConstants() {
		
	}

	/**
	* Path to hold PinAuthorityConfiguration file 
	*/
	public static final String WebApp_Directory = "/usr/tomcat/webapps/";
	public static final String PinAuthority_Directory = "ViewPINAuthority/WEB-INF/classes/";
	
	/**
	*  path to use-files
	*/
	public static final String USR_FILE_DIR = "/usr-files/";
	
	/**
	* configuration Directory
	*/
	public static final String Config_Directory = "/configuration/";
	/**
	* PinAuthorityConfiguration file
	*/
	public static final String Authority_Config_File = "PinAuthorityConfiguration.xml";
	/** 
	*Temp file to hold the element before copy to original one
	*/
	public static final String New_Authority_Config_File = "PinAuthorityConfiguration.xml.new";
	
	/**
	* PinAuthority Version File
	*/
	public static final String Authority_Version_File = "PinAuthorityVersion.txt";
	
	/**
	* PinAuthority Version
	*/
	public static final String PinAuthorityVersion = "PINAuthorityVersion";
	
	/**
	* The PinAuthority Signing Subject Key Identifier
	*/
		public static final String PinAuthoritySigningSKI = "PinAuthority Signing Subject Key Identifier";
	/**
	* XPath for PinAuthority Signing Subject Key Identifier
	*/
		public static final String PinAuthoritySigningSKIPath = "/PinAuthorityConfiguration/PinAuthoritySigningKeySKI";
	
	/**
	* The PinAuthority Wrapping Certificate Subject Key Identifier
	*/
		public static final String PinAuthorityWrappingCertificateSKIKey = "PinAuthority Wrapping Certificate Subject Key Identifier"; 
	/**
	* XPath for PinAuthority Wrapping Certificate Subject Key Identifier
	*/
		public static final String PinAuthorityWrappingCertificateSKIPath =	"/PinAuthorityConfiguration/PinAuthorityWrappingCertificateSubjectKeyIdentifier";

	
	/**
	* constant to display the PinAuthority configuration element
	*/
	public static final String PinAuthoritySigningKeySKI = "PinAuthoritySigningKeySKI";
	public static final String MaximumFailedAuthenticationAttempts = "MaximumFailedAuthenticationAttempts";
	public static final String PinAuthorityWrappingCertificateSubjectKeyIdentifier = "PinAuthorityWrappingCertificateSubjectKeyIdentifier";
	public static final String CardHolderDataElements = "CardHolderDataElements";
	
	public static final String PANElement = "PANElement";
	public static final String PINElement = "PINElement";
	public static final String ExpiryDateElement = "ExpiryDateElement";
	public static final String CVVElement = "CVVElement";
	
	public static final String Name = "Name";
	public static final String Encrypted = "Encrypted";
	public static final String EncryptionProperties = "EncryptionProperties";
	public static final String KeyIdentifier = "KeyIdentifier";
	public static final String KeyType = "KeyType";
	public static final String Transformation = "Transformation";
	public static final String MaximumReplayOpportunityWindow = "MaximumReplayOpportunityWindow";
	
	/** 
	* Path to max failed attempts 
	*/
	public static final String MaxFailedAuthenticAttemptsPath = "/PinAuthorityConfiguration/MaximumFailedAuthenticationAttempts";
	/**
	* Path to Maximum Opportunity Window
	*/
	public static final String MaximumReplayOpportunityWindowPath = "/PinAuthorityConfiguration/MaximumReplayOpportunityWindow";
	
	/** 
	* Constant to hold the String value for CardHolder Value 
	*/
	public static final String CardHolderPAN = "Card holder PAN";
	public static final String CardHolderCVV = "Card Holder CVV";
	public static final String CardHolderExpiryDate = "Card Holder Expiry Date";
	public static final String CardHolderPIN = "Card Holder PIN";
	
	/**
	* Path for PAN Value
	*/
	public static final String CardHolderPANPath = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/Name";
	/**
	* Path for CVV Value
	*/
	public static final String CardHolderCVVPath = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/Name";
	/**
	* Path for ExpiryDate Value
	*/
	public static final String CardHolderExpiryDatePath = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/Name";
	/**
	* Path for PIN Value
	*/
	public static final String CardHolderPINPath = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/Name";
	/** 
	* Constant to hold the String value for Key Encryption flag 
	*/
	public static final String CardHolderPANEncryption = "Card holder PAN Encryption";
	public static final String CardHolderCVVEncryption = "Card Holder CVV Encryption";
	public static final String CardHolderExpiryDateEncryption = "Card Holder Expiry Date Encryption";
	public static final String CardHolderPINEncryption = "Card Holder PIN Encryption";
	
	/**
	* Path for PAN encrypted flag
	*/
	public static final String CardHolderPANEncryptedPath = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/Encrypted";
	/**
	* Path for CVV encrypted flag
	*/
	public static final String CardHolderCVVEncryptedPath = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/Encrypted";
	/**
	* Path for ExpiryDate encrypted flag
	*/
	public static final String CardHolderExpiryDateEncryptedPath = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/Encrypted";
	/**
	* Path for PIN encrypted flag
	*/
	public static final String CardHolderPINEncryptedPath = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/Encrypted";
	
	/** 
	* Constant to hold the String value for Key Identifier 
	*/
	public static final String CardHolderPANKeyIdentifier = "Card holder PAN Key Identifier";
	public static final String CardHolderCVVKeyIdentifier = "Card Holder CVV Key Identifier";
	public static final String CardHolderExpiryDateKeyIdentifier = "Card Holder Expiry Date Key Identifier";
	public static final String CardHolderPINKeyIdentifier = "Card Holder PIN Key Identifier";
	
	/**
	* Path for PAN key identifier 
	*/
	public static final String CardHolderPANKeyIdentifierPath = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/EncryptionProperties/KeyIdentifier";
	/**
	* Path for CVV key identifier 
	*/
	public static final String CardHolderCVVKeyIdentifierPath = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/EncryptionProperties/KeyIdentifier";
	/**
	* Path for ExpiryDate key identifier 
	*/
	public static final String CardHolderExpiryDateKeyIdentifierPath = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/EncryptionProperties/KeyIdentifier";
	/**
	* Path for PIN key identifier 
	*/
	public static final String CardHolderPINKeyIdentifierPath = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/EncryptionProperties/KeyIdentifier";
	
	/** 
	* Constant to hold the String value for Key Type 
	*/
	public static final String CardHolderPANKeyType = "Card holder PAN Key Type";
	public static final String CardHolderCVVKeyType = "Card Holder CVV Key Type";
	public static final String CardHolderExpiryDateKeyType = "Card Holder Expiry Date Key Type";
	public static final String CardHolderPINKeyType = "Card Holder PIN Key Type";
	
	/** 
	* Path for PAN Key type 
	*/
	public static final String CardHolderPANKeyTypePath = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/EncryptionProperties/KeyType";
	/** 
	* Path for CVV Key type 
	*/
	public static final String CardHolderCVVKeyTypePath = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/EncryptionProperties/KeyType";
	/** 
	* Path for ExpiryDate Key type 
	*/
	public static final String CardHolderExpiryDateKeyTypePath = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/EncryptionProperties/KeyType";
	/** 
	* Path for PIN Key type 
	*/
	public static final String CardHolderPINKeyTypePath = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/EncryptionProperties/KeyType";
	
	/**
	* constant to display the string representation for the following value
	*/
	public static final String CardHolderPANTransformation = "Card holder PAN Encryption Transform";
	public static final String CardHolderCVVTransformation = "Card Holder CVV Encryption Transform";
	public static final String CardHolderExpiryDateTransformation = "Card Holder Expiry Date Encryption Transform";
	public static final String CardHolderPINTransformation = "Card Holder PIN Encryption Transform";
	
	/**
	* Path for PAN Encryption Transformation
	*/
	public static final String CardHolderPANTransformationPath = "/PinAuthorityConfiguration/CardHolderDataElements/PANElement/EncryptionProperties/Transformation";
	/**
	* Path for CVV Encryption Transformation
	*/
	public static final String CardHolderCVVTransformationPath = "/PinAuthorityConfiguration/CardHolderDataElements/CVVElement/EncryptionProperties/Transformation";
	/**
	* Path for ExpiryDate Encryption Transformation
	*/
	public static final String CardHolderExpiryDateTransformationPath = "/PinAuthorityConfiguration/CardHolderDataElements/ExpiryDateElement/EncryptionProperties/Transformation";
	/**
	* Path for PIN Encryption Transformation
	*/
	public static final String CardHolderPINTransformationPath = "/PinAuthorityConfiguration/CardHolderDataElements/PINElement/EncryptionProperties/Transformation";
		
}