package com.safenetinc.viewpin.authority.importeftkeys;

public class CharArrayCryptoKiAttribute extends CryptoKiAttribute
{
	private char[] value = null;
	
	public CharArrayCryptoKiAttribute(long type, char[] value)
	{
		super(type);
		
		setValue(value);
	}
	
	private void setValue(char[] value)
	{
		this.value = value;
	}

	@Override
	public Object getValue() 
	{
		return this.value;
	}

}
