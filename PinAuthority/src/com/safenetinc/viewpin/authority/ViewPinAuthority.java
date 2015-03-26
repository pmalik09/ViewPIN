package com.safenetinc.viewpin.authority;


public class ViewPinAuthority {

	
	public PinAuthorityResponse processViewPinRequest(String viewPinRequest, int requestType, CardHolderDetails cardHolderDetails)
	{
	
		
		ViewPinAuthorityImplementation viewPinAuthorityImplementation = new ViewPinAuthorityImplementation();
		PinAuthorityResponse pinAuthorityResponse = viewPinAuthorityImplementation.processViewPinRequest(viewPinRequest,  requestType,  cardHolderDetails);
		
		return pinAuthorityResponse;
		
		
	}
}
