// $Id: PinAgent/src/com/safenetinc/viewpin/agent/ViewPinRequestGenerator.java 1.6 2013/09/25 09:44:59IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.safenetinc.luna.provider.key.LunaKey;
import com.safenetinc.viewpin.agent.exceptions.ViewPinRequestException;
import com.safenetinc.viewpin.agent.sessionkey.SessionKeyGenerator;
import com.safenetinc.viewpin.agent.sessionkey.SessionKeyWrapper;
import com.safenetinc.viewpin.agent.sessionkey.WrappedSessionKey;
import com.safenetinc.viewpin.common.datastructures.CardHolderVerification;
import com.safenetinc.viewpin.common.datastructures.PinChangeData;
import com.safenetinc.viewpin.common.utils.UrlSafeBase64;
import com.safenetinc.viewpin.common.utils.XMLUtils;

/**
 * Class to handle the generation of the PINRetrievalRequest message
 * 
 * @author Stuart Horler
 *
 */
public class ViewPinRequestGenerator 
{
	private static Logger logger = Logger.getLogger(ViewPinRequestGenerator.class);
	
	private ViewPinRequestGenerator()
    {
    	super();
    }
    
    /**
     * Method to handle the generation of a PINRetrievalRequest document
     * @param pinAuthority The PinAuthority that this document is being generated for
     * @param cardHolderVerification The CVV to embed in the document
     * @param pinAgent The PINAgent instance
     * @return The PINRetrievalRequest document
     * @throws ViewPinRequestException Thrown if an error occurs during document creation
     */
    public static PinRetrievalRequest generatePinRetrievalRequest(PinAuthority pinAuthority,
        CardHolderVerification cardHolderVerification, PinAgent pinAgent) throws ViewPinRequestException
    {
    	PinRetrievalRequest pinRetrievalRequest;
    	SecretKey sessionKey;
    	WrappedSessionKey agentWrappedSessionKey;
    	WrappedSessionKey authorityWrappedSessionKey;
    	byte[] compressedPinRetrievalRequestDocument;
    	String encodedCompressedPinRetrievalRequestDocument;
    	Document pinRetrievalRequestDocument;
    	
    	pinRetrievalRequest = null;
    	sessionKey = null;
    	agentWrappedSessionKey = null;
    	authorityWrappedSessionKey = null;
    	compressedPinRetrievalRequestDocument = null;
    	encodedCompressedPinRetrievalRequestDocument = null;
    	pinRetrievalRequestDocument = null;
    	
    	try
    	{
    		try
    		{
                // Generate session key
    		    sessionKey = SessionKeyGenerator.generateSessionKey(pinAuthority.getSessionCipherProperties());
    		}
		    catch(Exception e)
            {
            	getLogger().error("generating session key " + e.getMessage());
            	
            	throw new ViewPinRequestException();
            }
		    
		    getLogger().debug("wrapping session key to pin agent");
			
            try
            {
				// Wrap session key to pin agent
			    agentWrappedSessionKey = SessionKeyWrapper.wrapSessionKey(pinAgent.getAgentWrapper().getWrappingCertificateSubjectKeyIdentifier(),
				    pinAgent.getAgentWrapper().getWrappingCertificate(), pinAgent.getAgentWrapper().getWrappingPaddingScheme(),
				    pinAuthority.getSessionCipherProperties(), sessionKey);
            }
		    catch(Exception e) 
			{
                getLogger().error("wrapping session key to pin agent " + e.getMessage());
            	
            	throw new ViewPinRequestException();
			}
			
			getLogger().debug("wrapped session key to pin agent ok");
                
			try
            {
        		// Wrap session key to authority
	            authorityWrappedSessionKey = SessionKeyWrapper.wrapSessionKey(pinAuthority.getWrappingCertificateSubjectKeyIdentifier(),
	                pinAuthority.getWrappingCertificate(), pinAuthority.getWrappingPaddingScheme(),
	                pinAuthority.getSessionCipherProperties(), sessionKey);
            }
	        catch(Exception e)
            {
            	getLogger().error("wrapping session key to pin authority " + e.getMessage());
            	
            	throw new ViewPinRequestException();
            }
            
	        getLogger().debug("wrapped session key to pin authority OK");
	    
	        try
			{
				// Generate pin retrieval request document
			    pinRetrievalRequestDocument = ViewPinRequestDocumentGenerator.generatePinRetrievalRequestDocument(sessionKey,
		            pinAuthority.getSessionCipherProperties(), cardHolderVerification,
		            authorityWrappedSessionKey, pinAgent.getAgentSigner(), pinAgent.getDigestMethodAlgorithm());
			
			    // Compress pin retrieval request document
			    compressedPinRetrievalRequestDocument = XMLUtils.compressDocument(pinRetrievalRequestDocument);
			
			    //getLogger().debug("compressed pin retrieval request document length = " + compressedPinRetrievalRequestDocument.length);
			    
			    // Safe encode compressed pin retrieval request document
			    encodedCompressedPinRetrievalRequestDocument = UrlSafeBase64.encode(compressedPinRetrievalRequestDocument);
			
			    //getLogger().debug("encoded compressed pin retrieval request document length = " + encodedCompressedPinRetrievalRequestDocument.length());
			
			  			    
			    pinRetrievalRequest = new PinRetrievalRequest(pinRetrievalRequestDocument, agentWrappedSessionKey,
                    encodedCompressedPinRetrievalRequestDocument);
            }
			catch(Exception e) 
			{
				getLogger().error(e.getMessage());
				//e.printStackTrace();
				
				throw new ViewPinRequestException();
			}
	    }
    	finally
    	{
            // Did we generate session key OK?
            if(sessionKey != null)
            {
            	getLogger().debug("destroying session key");
            	
                // Generated session key OK, destroy it
                ((LunaKey)sessionKey).DestroyKey();

                getLogger().debug("destroyed session key OK");
            }
    	}
    	
    	return pinRetrievalRequest;
    }
    
    /**
     * Method to handle the generation of a PINChangeRequest document
     * @param pinAuthority The PinAuthority that this document is being generated for
     * @param cardHolderVerification The CVV to embed in the document
     * @param pinAgent The PINAgent instance
     * @return The PINChangeRequest document
     * @throws ViewPinRequestException Thrown if an error occurs during document creation
     */
    public static PinChangeRequest generatePinChangeRequest(PinAuthority pinAuthority,
        PinChangeData pinChangeData, PinAgent pinAgent) throws ViewPinRequestException
    {
    	PinChangeRequest pinChangeRequest;
    	SecretKey sessionKey;
    	WrappedSessionKey authorityWrappedSessionKey;
    	byte[] compressedPinChangeRequestDocument;
    	String encodedCompressedPinChangeRequestDocument;
    	Document pinChangeRequestDocument;
    	
    	pinChangeRequest = null;
    	sessionKey = null;
    	authorityWrappedSessionKey = null;
    	compressedPinChangeRequestDocument = null;
    	encodedCompressedPinChangeRequestDocument = null;
    	pinChangeRequestDocument = null;
    	
    	try
    	{
    		try
    		{
                // Generate session key
    		    sessionKey = SessionKeyGenerator.generateSessionKey(pinAuthority.getSessionCipherProperties());
    		}
		    catch(Exception e)
            {
            	getLogger().error("generating session key " + e.getMessage());
            	
            	throw new ViewPinRequestException();
            }
		    
		    getLogger().debug("wrapping session key to pin agent");
			
           try
            {
        		// Wrap session key to authority
	            authorityWrappedSessionKey = SessionKeyWrapper.wrapSessionKey(pinAuthority.getWrappingCertificateSubjectKeyIdentifier(),
	                pinAuthority.getWrappingCertificate(), pinAuthority.getWrappingPaddingScheme(),
	                pinAuthority.getSessionCipherProperties(), sessionKey);
            }
	        catch(Exception e)
            {
            	getLogger().error("wrapping session key to pin authority " + e.getMessage());
            	
            	throw new ViewPinRequestException();
            }
            
	        getLogger().debug("wrapped session key to pin authority OK");
	    
	        try
			{
				// Generate pin change request document
			    pinChangeRequestDocument = ViewPinRequestDocumentGenerator.generatePinChangeRequestDocument(sessionKey,
		            pinAuthority.getSessionCipherProperties(), pinChangeData,
		            authorityWrappedSessionKey, pinAgent.getAgentSigner(), pinAgent.getDigestMethodAlgorithm());
			
			   // System.out.println("pinChangeRequestDocument" + pinChangeRequestDocument.toString());
			    // Compress pin change request document
			    compressedPinChangeRequestDocument = XMLUtils.compressDocument(pinChangeRequestDocument);
			
			//    getLogger().debug("compressed pin change request document length = " + compressedPinChangeRequestDocument.length);
			    
			    // Safe encode compressed pin change request document
			    encodedCompressedPinChangeRequestDocument = UrlSafeBase64.encode(compressedPinChangeRequestDocument);
			
			  //  getLogger().debug("encoded compressed pin change request document length = " + encodedCompressedPinChangeRequestDocument.length());
			
			   // getLogger().debug("encoded compressed pin change document = " + encodedCompressedPinChangeRequestDocument);
			    
			    pinChangeRequest = new PinChangeRequest(pinChangeRequestDocument, encodedCompressedPinChangeRequestDocument);
            }
			catch(Exception e) 
			{
				getLogger().error(e.getMessage());
				
				throw new ViewPinRequestException();
			}
	    }
    	finally
    	{
            // Did we generate session key OK?
            if(sessionKey != null)
            {
            	getLogger().debug("destroying session key");
            	
                // Generated session key OK, destroy it
                ((LunaKey)sessionKey).DestroyKey();

                getLogger().debug("destroyed session key OK");
            }
    	}
    	
    	return pinChangeRequest;
    }
    
    
	private static Logger getLogger()
	{
		return ViewPinRequestGenerator.logger;
	}
}