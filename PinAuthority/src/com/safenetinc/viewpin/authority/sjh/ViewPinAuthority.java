package com.safenetinc.viewpin.authority.sjh;

import org.apache.log4j.Logger;

import com.safenetinc.viewpin.authority.ErrorCodesConstants;
import com.safenetinc.viewpin.authority.PinAuthorityResponse;

public class ViewPinAuthority 
{
	private static Logger logger = Logger.getLogger(ViewPinAuthority.class);
	
	public PinAuthorityResponse retrievePin(RetrievePinServiceRequest retrievePinServiceRequest)
	{
		if(getLogger().isDebugEnabled() == true)
		{
			getLogger().debug("cvk = " + retrievePinServiceRequest.getCardVerificationKeyPairName());
			getLogger().debug("doc = " + retrievePinServiceRequest.getEncodedCompressedPinRetrievalRequestDocument());
			getLogger().debug("pan = " + retrievePinServiceRequest.getPrimaryAccountNumber());
			getLogger().debug("ed = " + retrievePinServiceRequest.getExpiryDate());
		
			CardPin[] cardPins = retrievePinServiceRequest.getCardPins();
			
			if(cardPins != null)
			{
				for(int i = 0; i < cardPins.length; i++)
				{
					getLogger().debug("zpk[" + i + "] = " + cardPins[i].getZonePinKeyName());
					getLogger().debug("pb[" + i + "] = "  + cardPins[i].getEncodedEncryptedPinBlock());
					getLogger().debug("pan[" + i + "] = " + cardPins[i].getPrimaryAccountNumber());
				}
			}
		}
		
		PinAuthorityResponse pinAuthorityResponse = new PinAuthorityResponse();
		
		// Ensure retrieve pin service request is not null
		if(retrievePinServiceRequest == null)
		{
			// Retrieve pin service request is null
			getLogger().error("retrieve pin service request is null");
						
			pinAuthorityResponse.setIsSuccess(false);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
			
			return pinAuthorityResponse;
		}
		
		try
		{
			PinRetrievalRequestProcessor prrp = new PinRetrievalRequestProcessor();
			
			// Process pin retrieval request
			pinAuthorityResponse = prrp.retrievePin(retrievePinServiceRequest);
		}
		catch(Exception e)
		{
			pinAuthorityResponse.setIsSuccess(false);
			pinAuthorityResponse.setErrorCode(ErrorCodesConstants.VIEW_PIN_REQUEST_ERROR);
			
			return pinAuthorityResponse;
		}
		
		return pinAuthorityResponse;
	}
	
	private static Logger getLogger()
	{
	    return logger;
	}
}