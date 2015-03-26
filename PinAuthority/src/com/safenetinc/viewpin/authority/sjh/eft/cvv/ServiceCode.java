package com.safenetinc.viewpin.authority.sjh.eft.cvv;


public class ServiceCode 
{
	private static final String SERVICE_CODE_REGEX = "^[0-9]{3}$";
	
	private String serviceCode = null;
	
	public ServiceCode(String serviceCode) throws InvalidServiceCodeException
	{
		super();
		
		validateServiceCode(serviceCode);
		
		setServiceCode(serviceCode);
	}
	
	private void validateServiceCode(String serviceCode) throws InvalidServiceCodeException
	{
		if(serviceCode.matches(SERVICE_CODE_REGEX) == false)
		{
			throw new InvalidServiceCodeException("service code must comprise of exactly three numeric digits");
		}
	}

	public String getServiceCode()
	{
		return this.serviceCode;
	}

	private void setServiceCode(String serviceCode)
	{
		this.serviceCode = serviceCode;
	}
}
