package com.safenetinc.viewpin.authority.importeftkeys;

public class BooleanCryptoKiAttribute extends CryptoKiAttribute 
{
	private boolean value = false;
	
	public BooleanCryptoKiAttribute(long type, boolean value)
	{
		super(type);
		
		setValue(value);
	}

	@Override
	public Object getValue() 
	{
		return new Boolean(value);
	}

	private void setValue(boolean value) 
	{
		this.value = value;
	}
}
