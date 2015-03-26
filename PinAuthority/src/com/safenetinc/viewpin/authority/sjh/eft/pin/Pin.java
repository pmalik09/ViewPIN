package com.safenetinc.viewpin.authority.sjh.eft.pin;

public abstract class Pin 
{
	private static final String BASIC_PIN_REGEX = "^[0-9]*$";
	
	private String pin = null;
	
	public Pin(String pin) throws InvalidPinException
	{
		super();
		
		validatePin(pin);
		
		setPin(pin);
	}
	
	protected void validatePin(String pin) throws InvalidPinException
	{
		// Ensure pin passed in is not null
		if(pin == null)
		{
			throw new InvalidPinException("is null");
		}
		
		// Ensure that pin only contains numeric digits
		if(pin.matches(BASIC_PIN_REGEX) == false)
        {
			throw new InvalidPinException("pin must consist only of numeric digits");
        }
	}
	
	private void setPin(String pin)
	{
		this.pin = pin;
	}
	
	public String getPin()
	{
		return this.pin;
	}
	
	public String toString()
	{
		return getPin();
	}
}
