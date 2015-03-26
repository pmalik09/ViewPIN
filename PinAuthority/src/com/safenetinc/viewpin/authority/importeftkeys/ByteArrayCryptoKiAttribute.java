package com.safenetinc.viewpin.authority.importeftkeys;

public class ByteArrayCryptoKiAttribute extends CryptoKiAttribute 
{
	private byte[] value = null;
	
	public ByteArrayCryptoKiAttribute(long type, byte[] value)
	{
		super(type);
		
		setValue(value);
	}

	@Override
	public Object getValue() 
	{
		return this.value;
	}

	private void setValue(byte[] value)
	{
		this.value = value;
	}
}
