package com.safenetinc.viewpin.authority.sjh.eft.pin.iso;

import com.safenetinc.viewpin.authority.sjh.eft.pin.InvalidPinException;
import com.safenetinc.viewpin.authority.sjh.eft.pin.Pin;

public class IsoPin extends Pin
{
	public static final int MINIMUM_ISO_PIN_LENGTH = 4;
	public static final int MAXIMUM_ISO_PIN_LENGTH = 12;
		
	public IsoPin(String pin) throws InvalidPinException
	{
		super(pin);
	}
	
	protected void validatePin(String pin) throws InvalidPinException
	{
		super.validatePin(pin);
		
		validatePinLength(pin);
	}
	
	private void validatePinLength(String pin) throws InvalidPinException
	{
		// Ensure pin length is within minimum and maximum ISO lengths
		if(pin.length() < MINIMUM_ISO_PIN_LENGTH || pin.length() > MAXIMUM_ISO_PIN_LENGTH)
		{
			throw new InvalidPinException("invalid iso PIN length, must be between " + 
			    MINIMUM_ISO_PIN_LENGTH + " and " + MAXIMUM_ISO_PIN_LENGTH + " numeric digits");
		}
	}
}
