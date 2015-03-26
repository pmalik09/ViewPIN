package com.safenetinc.viewpin.cli.importeftkeys;

public class LongCryptoKiAttribute extends CryptoKiAttribute 
{
	private long value = 0L;
	
	public LongCryptoKiAttribute(long type, long value)
	{
		super(type);
		
		setValue(value);
	}

	@Override
	public Object getValue()
	{
		return new Long(this.value);
	}

	private void setValue(long value)
	{
		this.value = value;
	}
}
