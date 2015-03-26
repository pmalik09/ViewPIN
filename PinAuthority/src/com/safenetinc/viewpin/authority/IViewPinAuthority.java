package com.safenetinc.viewpin.authority;


public interface IViewPinAuthority {
	
	public PinAuthorityResponse processViewPinRequest(String viewPinRequest, int requestType, CardHolderDetails cardHolderDetails);
	
	
		
	

}

