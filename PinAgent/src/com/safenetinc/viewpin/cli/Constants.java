package com.safenetinc.viewpin.cli; 


/**
 * Class containing the constants for editing ,updating and storing the PINAgent Configuration file
 * @author Pratibha Malik
 */
public class Constants {

	public Constants()
	{
		
	}
	
	//public static final String Start = "[";
	
	//public static final String End = "]";
	
	 /**
     * The tomcat webapp directory
     */
	public static final String Webapp_Directory = "/usr/tomcat/webapps/";

	 /**
     * The usr-file directory
     */
	public static final String USR_FILE_DIR = "/usr-files/";
	
	/**
     * The configuration  directory
     */
	public static final String Config_Directory = "/configuration/";
	
	/**
     * The Agent Configuration File
     */
	public static final String Agent_Config_File = "agentconfiguration.xml";
	
	/**
	* PinAgent Version File
	*/
	public static final String Agent_Version_File = "PinAgentVersion.txt";
	public static final String PinAgentVersion = "PINAgentVersion";
	
	/**
     * The Agent Configuration File
     */
	public static final String New_Agent_Config_File = "agentconfiguration.xml.new";
	
	 /**
     * The PinAgent Signing Certificate Subject Key Identifier
     */
	public static final String Name = "Name";
	
	
	/**
     * The Signing Certificate Tag
     */
	public static final String Signing_Certificate = "SigningCertificate";

	/**
     * The SSL Certificate Tag
     */
	public static final String SSL_Certificate = "SSLCertificate";

	/**
     * The Port Number Tag
     */
	public static final String Port_Number = "Port";

	
	/**
     * The SubjectKeyIdentifier Tag
     */
	public static final String Subject_Key_Identifier = "SubjectKeyIdentifier";
	
	/**
     * The SignatureMethodAlgorithm Tag
     */
	public static final String Signature_Method_Algorithm = "SignatureMethodAlgorithm";

	/**
     * The DigestMethodAlgorithm Tag
     */
	public static final String Digest_Method_Algorithm = "DigestMethodAlgorithm";
	
	/**
     * The Wrapping Certificate Tag
     */
	public static final String Wrapping_Certificate = "WrappingCertificate";
	
	/**
     * The Session Key Tag
     */
	public static final String Session_Key = "SessionKey";
	
	/**
     * The Algorithm Name Tag
     */
	public static final String Algorithm_Name = "AlgorithmName";
	
	/**
     * The Bit Length Tag
     */
	public static final String Bit_Length = "BitLength";
	
	/**
     * The Wrapping Padding Scheme Tag
     */
	public static final String Wrapping_Padding_Scheme = "WrappingPaddingScheme";
	
	/**
     * The Error Redirection URL Tag
     */
	public static final String Error_Redirection_URL = "ErrorRedirectionUrl";
	
	/**
     * The  Redirection URL Tag
     */
	public static final String Redirection_URL = "RedirectionUrl";
	
	/**
     * The  Wrapped Session Key Cookie  Tag
     */
	public static final String Wrapped_Session_Key_Cookie = "WrappedSessionKeyCookie";
	
	/**
     * The  Wrapped Session Key Cookie Domain Tag
     */
	public static final String Domain = "Domain";
	
	/**
     * The  Wrapped Session Key Cookie Name Value
     */
	public static final String Wrapped_Session_Key_Cookie_Name = "wrappedsessionkey";
		
	/**
     * The  Path Tag
     */
	public static final String Path = "Path";
	
	/**
     * The  Path Element Value
     */
	public static final String Path_Value = "/";
	
	/**
     * The PinRetrieval Request Cookie  Tag
     */
	public static final String Pin_Retrieval_Request_Cookie = "PinRetrievalRequestCookie";
	
	/**
     * The  PinRetrieval Request Cookie Name Value
     */
	public static final String Pin_Retrieval_Request_Cookie_Name = "pinretrievalrequest";
		
	/**
     * The PinRetrieval Response Cookie  Tag
     */
	public static final String Pin_Retrieval_Response_Cookie = "PinRetrievalResponseCookie";

	/**
     * The PinChange Request Cookie  Tag
     */
	public static final String Pin_Change_Request_Cookie = "PinChangeRequestCookie";
	
	/**
     * The  PinChange Request Cookie Name Value
     */
	public static final String Pin_Change_Request_Cookie_Name = "pinchangerequest";
	
	/**
     * The  PinRetrieval Response Cookie Name Value
     */
	public static final String Pin_Retrieva_Response_Cookie_Name = "pinretrievalresponse";
	
	/**
     * The Replay Window Tag
     */
	public static final String Replay_Win = "ReplayWindow";
	
	/**
     * The  PinAuthority Tag
     */
	public static final String PIN_Authority = "PinAuthority";
	
	/**
     * The  WrappingPaddingScheme Tag
     */
	public static final String WrappingPaddingScheme = "WrappingPaddingScheme";
	
	 /**
     * The PinAgent Signing Certificate Subject Key Identifier
     */
	public static final String PIN_Agent_Signing_Certificate_SKI = "PINAgent Signing Certificate Subject Key Identifier";
	
	/**
     * The PinAgent Wrapping Certificate Subject Key Identifier
     */
	public static final String PIN_Agent_Wrapping_Certificate_SKI = "PINAgent Wrapping Certificate Subject Key Identifier"; 
	
	
	/**
     * The PinAgent SSL Certificate Subject Key Identifier
     */
	public static final String PIN_Agent_SSL_Certificate_SKI = "PINAgent SSL Certificate Subject Key Identifier";
	
	
	/**
     * The PinAgent Error Redirection URL
     */
	public static final String PIN_Agent_Error_Redirection_URL = "PINAgent Error Redirection URL";
	
	/**
     * The PinAgent Wrapped Session Cookie Domain
     */
	public static final String PIN_Agent_Wrapped_Session_Cookie_Domain = "PINAgent Wrapped Session Cookie Domain";
	
	/**
     * The PinAgent Port Number
     */
	public static final String PIN_Agent_Port_Number = "PINAgent Port Number";
	
	/**
     * The PIN Retrieval Request Cookie
     */
	public static final String PIN_Retrieval_Request_Cookie = "PIN Retrieval Request Cookie Domain";
	
	/**
     * The PIN Retrieval Response Cookie
     */
	public static final String PIN_Retrieval_Response_Cookie = "PIN Retrieval Response Cookie Domain";
	
	/**
     * The PIN Change Request Cookie
     */
	public static final String PIN_Change_Request_Cookie = "PIN Change Request Cookie Domain";
	

	/**
     * The Replay Window
     */
	public static final String Replay_Window = "Replay Window";
	
	 /**
     * The PinAgent Signing Certificate Subject Key Identifier
     */
	public static final String PIN_Authority_Name = "Pin Authority Name";
	
	 /**
     * The PinAuthority Signing Certificate Subject Key Identifier
     */
	public static final String PIN_Authority_Signing_Certificate_SKI = "PINAuthority Signing Certificate Subject Key Identifier";
	
	/**
     * The PinAgent Wrapping Certificate Subject Key Identifier
     */
	public static final String PIN_Authority_Wrapping_Certificate_SKI = "PINAuthority Wrapping Certificate Subject Key Identifier"; 
	
	/**
     * The PinAgent Error Redirection URL
     */
	public static final String PIN_Authority_Error_Redirection_URL = "PINAuthority Redirection URL";

	/**
     * Default value of PIN Authority SessionKey Bit Length
     */
	public static final int PIN_Authority_Session_Key_Bit_Length = 128;
	
	/**
     *  Default value of PIN Authority SessionKey AlgorithmName
     */
	public static final String PIN_Authority_Session_Key_Algorithm_Name= "AES";

	/**
 *  Default value of PIN Authority Wrapping Padding Scheme Name
 */
	public static final String PIN_Authority_Wrapping_Padding_Scheme_Name= "OAEP";
	
	//list of XPaths of the elements to be fetched from the XML document
	
	 /**
     * XPath for PinAuthority Name
     */
	public static final String PINAgentNamePath = "/PinAgent/Name"; 
	
	 /**
     * XPath for PinAgent Signing Certificate Subject Key Identifier
     */
	public static final String PIN_Agent_Signing_Certificate_SKI_Path = "/PinAgent/SigningCertificate/SubjectKeyIdentifier"; 
	
	/**
     * XPath for PinAgent Wrapping Certificate Subject Key Identifier
     */
	public static final String PIN_Agent_Wrapping_Certificate_SKI_Path = "/PinAgent/WrappingCertificate/SubjectKeyIdentifier";
	
	/**
     * XPath for PinAgent SignatureMethodAlgorithm
     */
	public static final String PIN_Agent_Signature_Method_Algorithm_Path = "/PinAgent/SignatureMethodAlgorithm";
	
	/**
     * XPath for PINAgent Error Redirection URL
     */
	public static final String PIN_Agent_Error_Redirection_URL_Path = "/PinAgent/ErrorRedirectionUrl"; 

	/**
     * XPath for PIN Agent Wrapped Session Key Cookie
     */
	public static final String PIN_Agent_Wrapped_Session_Key_Cookie_Path = "/PinAgent/WrappedSessionKeyCookie/Domain";
	
	/**
     * XPath for PIN Agent PIN Retrieval Request Cookie
     */
	public static final String PIN_Agent_Retrieval_Request_Cookie_Path = "/PinAgent/PinRetrievalRequestCookie/Domain"; 

	
	/**
     * XPath for PIN Agent PIN Change Request Cookie
     */
	public static final String PIN_Agent_Change_Request_Cookie_Path = "/PinAgent/PinChangeRequestCookie/Domain"; 
		
	/**
     * XPath for PinAgent Digest Method Algorithm
     */
	public static final String PIN_Agent_Digest_Method_Algorithm_Path = "/PinAgent/DigestMethodAlgorithm";
	
	
	/**
     * XPath for PIN Agent PIN Retrieval Response Cookie
     */
	public static final String PIN_Agent_Retrieval_Response_Cookie_Path = "/PinAgent/PinRetrievalResponseCookie/Domain";
	
	/**
     * XPath for PIN Agent Replay Window
     */
	public static final String PIN_Agent_Replay_Window_Path = "/PinAgent/ReplayWindow";
		
	/**
     * XPath for PIN Authority Index
     */
	public static final String PIN_Authority_Path = "/PinAgent/PinAuthorities/PinAuthority";

	/**
     * XPath for PIN Authority Name
     */
	public static final String PIN_Authority_Name_Path = "/Name";
	
	/**
     * XPath for PIN Authority 
     */
	public static final String PIN_Authority_Count = "count(//PinAgent/PinAuthorities/PinAuthority)";
	
	/**
     * XPath for PIN Authority Signing Certificate Subject Key Identifier
     */
	public static final String PIN_Authority_Signing_Certificate_SKI_Path = "/SigningCertificate/SubjectKeyIdentifier"; 
	
	/**
     * XPath for PIN Authority Wrapping Certificate Subject Key Identifier
     */
	public static final String PIN_Authority_Wrapping_Certificate_SKI_Path = "/WrappingCertificate/SubjectKeyIdentifier";
	
	/**
     * XPath for PIN Authority Wrapping Padding Scheme
     */
	public static final String PIN_Authority_Wrapping_Padding_Scheme_Path = "/WrappingPaddingScheme"; 

	/**
     * XPath for PIN Authority SessionKey BitLength
     */
	public static final String PIN_Authority_Session_Key_Bit_Length_Path = "/SessionKey/BitLength";
	
	/**
     * XPath for PIN Authority SessionKey AlgorithmName
     */
	public static final String PIN_Authority_Session_Key_Algorithm_Name_Path = "/SessionKey/AlgorithmName";
		
	/**
     * XPath for PIN Authority Error Redirection URL
     */
	public static final String PIN_Authority_Error_Redirection_URL_Path = "/RedirectionUrl";
	
	/**
     * XPath for PIN Agent SSL Certificate 
     */
	public static final String PIN_Agent_SSL_Certificate_SKI_Path = "/PinAgent/SSLCertificate/SubjectKeyIdentifier";
	
	/**
     * XPath for PIN Agent Port 
     */
	public static final String PIN_Agent_Port_Number_Path = "/PinAgent/Port"; 

	
	
}

