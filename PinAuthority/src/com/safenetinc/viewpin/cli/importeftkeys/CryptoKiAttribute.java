package com.safenetinc.viewpin.cli.importeftkeys;

public abstract class CryptoKiAttribute 
{
	private long type = 0L;
	
	public CryptoKiAttribute(long type)
	{
		super();
		
		setType(type);
	}
	
	private void setType(long type) 
	{
		this.type = type;
	}
	
	public long getType() 
	{
		return this.type;
	}

	public abstract Object getValue();
}
