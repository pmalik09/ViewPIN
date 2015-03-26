// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinAgent.java 1.4 2013/09/25 09:44:55IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;

import com.safenetinc.luna.LunaTokenManager;
import com.safenetinc.viewpin.agent.exceptions.AgentInitException;
import com.safenetinc.viewpin.agent.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.agent.exceptions.PinRetrievalResponseException;
import com.safenetinc.viewpin.agent.sessionkey.KeyType;
import com.safenetinc.viewpin.agent.sessionkey.PaddingScheme;
import com.safenetinc.viewpin.agent.sessionkey.SessionCipherProperties;
import com.safenetinc.viewpin.agent.sessionkey.Signer;
import com.safenetinc.viewpin.agent.sessionkey.Wrapper;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.InvalidSessionKeyLengthException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedKeyTypeException;
import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedPaddingSchemeException;
import com.safenetinc.viewpin.common.datastructures.CardHolderVerification;
import com.safenetinc.viewpin.common.datastructures.CardPin;
import com.safenetinc.viewpin.common.datastructures.PinChangeData;
import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;
import com.safenetinc.viewpin.common.datastructures.exceptions.InvalidSubjectKeyIdentifierException;

/**
 * Class to represent a PINAgent, in effect the heart of the ViewPIN system
 *
 * @author Stuart Horler
 *
 *
 */
class PinAgent
{
	private static Logger logger = Logger.getLogger(PinAgent.class);
	
	private Wrapper agentWrapper = null;
    private Signer agentSigner = null;
    private PinRetrievalResponseProcessor pinRetrievalResponseProcessor = null;
    private long replayWindow = 0L;
    private String digestMethodAlgorithm = null;
    private KeyStore keyStore = null;
    private PinAuthorities pinAuthorities = null;
    
    PinAgent(SubjectKeyIdentifier agentSigningCertificateSubjectKeyIdentifier, String signatureMethodAlgorithm,
        SubjectKeyIdentifier agentWrappingCertificateSubjectKeyIdentifier,
        PaddingScheme wrappingPaddingScheme, long replayWindow, String digestMethodAlgorithm) throws AgentInitException
    {
        super();
        
        try
		{
            // Initialise key store
	        initKeyStore();
            
	        // Instantiate pin authorities
            setPinAuthorities(new PinAuthorities());
            
            // Ensure session keys can be wrapped out
			LunaTokenManager.getInstance().SetSecretKeysExtractable(true);
			
			// Initialise agent signer
			initAgentSigner(agentSigningCertificateSubjectKeyIdentifier, signatureMethodAlgorithm);
			
			// Initialise agent wrapper
			initAgentWrapper(agentWrappingCertificateSubjectKeyIdentifier, wrappingPaddingScheme);
            
            // Initialise pin retrieval response generator
            setPinRetrievalResponseProcessor(new PinRetrievalResponseProcessor());
            
            // Store replay window
            setReplayWindow(replayWindow);
            
            // Store digest method algorithm
            setDigestMethodAlgorithm(digestMethodAlgorithm);
        }
		catch(Exception e)
		{
            getLogger().fatal("initialising agent " + e.getMessage());
			
			throw new AgentInitException(e.getMessage());
		}
    }
    
    PinRetrievalRequest generatePinRetrievalRequest(CardHolderVerification cardHolderVerification, PinAuthority pinAuthority) throws ViewPinRequestException
	{
	    PinRetrievalRequest pinRetrievalRequest;
	    
	    pinRetrievalRequest = null;
	    
	    // Generate pin retrieval request
        pinRetrievalRequest = ViewPinRequestGenerator.generatePinRetrievalRequest(pinAuthority, cardHolderVerification, this);
        
        return pinRetrievalRequest;
	}
    
    PinChangeRequest generatePinChangeRequest(PinChangeData pinChangeData, PinAuthority pinAuthority) throws ViewPinRequestException
	{
	    PinChangeRequest pinChangeRequest;
	    
	    pinChangeRequest = null;
	    
	    // Generate pin retrieval request
        pinChangeRequest = ViewPinRequestGenerator.generatePinChangeRequest(pinAuthority, pinChangeData, this);
        
        return pinChangeRequest;
	}

    
    ArrayList<CardPin> processPinRetrievalResponse(String encodedCompressedPinRetrievalResponse, String encodedWrappedSessionKey) throws PinRetrievalResponseException
    {
        ArrayList<CardPin> cardPins;
        
        cardPins = null;
        
        cardPins = getPinRetrievalResponseProcessor().processPinRetrievalResponse(encodedCompressedPinRetrievalResponse, encodedWrappedSessionKey, this);
    
        return cardPins;
    }
		
	private static Logger getLogger()
    {
        return logger;
    }
        
    private void initAgentSigner(SubjectKeyIdentifier agentSigningCertificateSubjectKeyIdentifier, String signatureMethodAlgorithm) throws AgentInitException
    {
    	PrivateKey agentSigningKey;
    	Certificate agentSigningCertificate;
    	
    	agentSigningKey = null;
    	agentSigningCertificate = null;
    	
    	getLogger().debug("agent signing key " + agentSigningCertificateSubjectKeyIdentifier);
		
		try 
		{
			// Retrieve agent signing key
			agentSigningKey = (PrivateKey)getKeyStore().getKey(agentSigningCertificateSubjectKeyIdentifier.getHexEncoded(),
			    "password".toCharArray());
			
			// Did we retrieve agent signing key OK?
			if(agentSigningKey == null)
			{
				// Failed to retrieve agent signing key
				getLogger().fatal("retrieving agent signing key " + agentSigningCertificateSubjectKeyIdentifier);
				
				throw new AgentInitException();
			}
		} 
		catch(Exception e) 
		{
			getLogger().fatal("retrieving agent signing key " + e.getMessage());
			
			throw new AgentInitException();
		}
		
		try
		{
			// Retrieve agent signing certificate
			agentSigningCertificate = getKeyStore().getCertificate(agentSigningCertificateSubjectKeyIdentifier.getHexEncoded());
			
			// Did we retrieve agent signing certificate OK?
			if(agentSigningCertificate == null)
			{
				// Failed to retrieve agent signing certificate
	            getLogger().fatal("retrieving agent signing certificate " + agentSigningCertificateSubjectKeyIdentifier);
				
				throw new AgentInitException();
			}
		}
		catch(Exception e) 
		{
			getLogger().fatal("retrieving agent signing certificate " + e.getMessage());
			
			throw new AgentInitException();
		}
		
		// Instantiate agent signer
		setAgentSigner(new Signer(agentSigningCertificateSubjectKeyIdentifier, agentSigningKey, agentSigningCertificate, signatureMethodAlgorithm));
    }

    private void initAgentWrapper(SubjectKeyIdentifier agentWrappingCertificateSubjectKeyIdentifier,
        PaddingScheme agentWrappingPaddingScheme) throws AgentInitException
    {
    	Certificate agentWrappingCertificate;
    	PrivateKey agentWrappingKey;
    	
    	agentWrappingCertificate = null;
    	agentWrappingKey = null;
    			
		try 
		{
			// Retrieve agent wrapping certificate
			agentWrappingCertificate = getKeyStore().getCertificate(agentWrappingCertificateSubjectKeyIdentifier.getHexEncoded());
		    
			// Did we retrieve agent wrapping certificate OK?
			if(agentWrappingCertificate == null)
			{
				// Failed to retrieve agent wrapping certificate
				getLogger().fatal("retrieving agent wrapping certificate");

			    throw new AgentInitException();
			}
		}
		catch(KeyStoreException kse) 
		{
			getLogger().fatal("retrieving agent wrapping certificate " + kse.getMessage());

		    throw new AgentInitException();
		}
        
        try
        {
            // Retrieve agent wrapping key
            agentWrappingKey = (PrivateKey)getKeyStore().getKey(agentWrappingCertificateSubjectKeyIdentifier.getHexEncoded(), null);
            
            // Did we retrieve agent wrapping key OK?
            if(agentWrappingKey == null)
            {
                // Failed to retrieve agent wrapping key
                getLogger().fatal("retrieving agent wrapping key");
                
                throw new AgentInitException();
            }
        }
        catch(Exception e) 
        {
            getLogger().fatal("retrieving agent wrapping key " + e.getMessage());

            throw new AgentInitException();
        }
            
		// Instantiate agent wrapper
	    setAgentWrapper(new Wrapper(agentWrappingCertificateSubjectKeyIdentifier, agentWrappingCertificate,
	        agentWrappingKey, agentWrappingPaddingScheme));
    }
        
    void addPinAuthority(String name, String signingCertificateSubjectKeyIdentifier, String wrappingCertificateSubjectKeyIdentifier,
        String wrappingPaddingScheme, String sessionKeyAlgorithmName, int sessionKeyBitLength, URL redirectionUrl) throws AgentInitException
    {
    	PinAuthority pa;
		String authorityName;
		SubjectKeyIdentifier scski;
    	Certificate signingCertificate;
    	SubjectKeyIdentifier wcski;
        Certificate wrappingCertificate;
    	PaddingScheme wps;
    	KeyType sessionKeyType;
    	SessionCipherProperties scp;
    	
    	authorityName = null;
		pa = null;
    	scski = null;
    	signingCertificate = null;
    	
    	wcski = null;
    	wrappingCertificate = null;
    	wps = null;
    	sessionKeyType = null;
    	scp = null;
    	
		try
		{
			if(name != null)
			authorityName = name;
			else
			{
			// Authority name is invalid
    		getLogger().fatal("authority name"  + "is null");
			}
		}
		catch(Exception e)
		{
			// Authority name is invalid
    		getLogger().fatal("authority " + name + "is invalid");
		
			throw new AgentInitException();
		}
    	try
    	{
    		// Instantiate authority signing certificate subject key identifier
    		scski = new SubjectKeyIdentifier(signingCertificateSubjectKeyIdentifier);
    	}
    	catch(InvalidSubjectKeyIdentifierException iskie)
		{
    		// Authority signing certificate subject key identifier is invalid
    		getLogger().fatal("authority " + name + " signing certificate subject key identifier " + iskie.getMessage());
		
			throw new AgentInitException();
		}
    	
    	try
    	{
    		// Instantiate authority signing certificate
			signingCertificate = getKeyStore().getCertificate(scski.getHexEncoded());
			
			if(signingCertificate == null)
			{
				getLogger().fatal("retrieving authority " + name + " signing certificate " + 
				    signingCertificateSubjectKeyIdentifier);
				
				throw new AgentInitException();
			}
		}
    	catch(KeyStoreException kse)
    	{
    		getLogger().fatal("retrieving authority " + name + " signing certificate " + 
    		    signingCertificateSubjectKeyIdentifier + " " + kse.getMessage());
    		
    		throw new AgentInitException();
		}
    	
    	try
    	{
    		// Instantiate authority wrapping certificate subject key identifier
    		wcski = new SubjectKeyIdentifier(wrappingCertificateSubjectKeyIdentifier);
    	}
    	catch(InvalidSubjectKeyIdentifierException iskie)
		{
    		// Wrapping certificate subject key identifier is invalid
    		getLogger().fatal("authority " + name + " wrapping certificate subject key identifier " + 
    		    iskie.getMessage());
	
		    throw new AgentInitException();
		}
    
    	try
    	{
    		// Get authority wrapping certificate
    		wrappingCertificate = getKeyStore().getCertificate(wcski.getHexEncoded());
    		
    		// Did we get authority wrapping certificate OK?
    		if(wrappingCertificate == null)
    		{
    			getLogger().fatal("retrieving authority " + name + " wrapping certificate " +
    			    wrappingCertificateSubjectKeyIdentifier);
    			
    			throw new AgentInitException();
    		}
    	}
    	catch(KeyStoreException kse)
    	{
    		getLogger().fatal("authority " + name + " wrapping certificate " + 
    		    wrappingCertificateSubjectKeyIdentifier + " " + kse.getMessage());
    		
    		throw new AgentInitException();
		}
    	
    	try
    	{
    		// Instantiate authority wrapping padding scheme
	        wps = PaddingScheme.getInstance(wrappingPaddingScheme);
    	}
	    catch(UnsupportedPaddingSchemeException upse) 
    	{
	    	// Authority wrapping padding scheme is invalid
	    	getLogger().fatal("authority " + name + " wrapping padding scheme is invalid");
		    
		    throw new AgentInitException();
    	} 
	    
	    try
	    {
	    	// Instantiate authority session key type
	    	sessionKeyType = KeyType.getInstance(sessionKeyAlgorithmName);
	    }
	    catch(UnsupportedKeyTypeException ukte) 
    	{
	    	// Authority session key type is unsupported
	    	getLogger().fatal("authority " + name + " session key algorithm name " + sessionKeyAlgorithmName + 
    	        " is unsupported");
    		
			throw new AgentInitException();
		}

	    try 
	    {
	    	// Instantiate session cipher properties
	  	    scp = new SessionCipherProperties(sessionKeyType, sessionKeyBitLength);
		}
	    catch(InvalidSessionKeyLengthException iskle)
	    {
	    	// Invalid session key length
	    	getLogger().fatal("authority " + name + " " + iskle.getMessage());
    		
			throw new AgentInitException();
		}
	    
	    pa = new PinAuthority(authorityName,scski, signingCertificate, wcski, wrappingCertificate, wps, scp, redirectionUrl);
        getPinAuthorities().add(pa);
    }
    
    Signer getAgentSigner() 
	{
		return this.agentSigner;
	}

	private void setAgentSigner(Signer agentSigner) 
	{
		this.agentSigner = agentSigner;
	}
		
	private void setAgentWrapper(Wrapper agentWrapper) 
	{
		this.agentWrapper = agentWrapper;
	}
	
	Wrapper getAgentWrapper() 
	{
		return this.agentWrapper;
	}
    
    private void setPinRetrievalResponseProcessor(PinRetrievalResponseProcessor pinRetrievalResponseProcessor)
    {
        this.pinRetrievalResponseProcessor = pinRetrievalResponseProcessor;
    }

    private PinRetrievalResponseProcessor getPinRetrievalResponseProcessor()
    {
        return this.pinRetrievalResponseProcessor;
    }
    
    private void setReplayWindow(long replayWindow)
    {
        this.replayWindow = replayWindow;
    }
    
    long getReplayWindow()
    {
        return this.replayWindow;
    }

    private void setDigestMethodAlgorithm(String digestMethodAlgorithm)
    {
        this.digestMethodAlgorithm = digestMethodAlgorithm;
    }
    
    String getDigestMethodAlgorithm()
    {
        return this.digestMethodAlgorithm;
    }
    
    private void initKeyStore() throws AgentInitException
    {
        KeyStore ks;
        ByteArrayInputStream is1;
		is1 = new ByteArrayInputStream(("slot:1").getBytes());
        ks = null;
        
        try 
        {
            ks = KeyStore.getInstance("Luna", "LunaProvider");
            ks.load(is1, null);
            setKeyStore(ks);
        }
        catch(Exception e)
        {    
            getLogger().fatal("initalising key store " + e.getMessage());       
            
            throw new AgentInitException();
        }
    }

    private KeyStore getKeyStore()
    {
        return this.keyStore;
    }

    private void setKeyStore(KeyStore keyStore)
    {
        this.keyStore = keyStore;
    }

    private void setPinAuthorities(PinAuthorities pinAuthorities)
    {
        this.pinAuthorities = pinAuthorities;
    }
    
    PinAuthorities getPinAuthorities()
    {
        return this.pinAuthorities;
    }
}