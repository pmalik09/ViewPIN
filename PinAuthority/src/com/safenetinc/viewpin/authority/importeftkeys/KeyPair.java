package com.safenetinc.viewpin.authority.importeftkeys;
public class KeyPair 
{
	private long publicKeyHandle = 0L;
	private long privateKeyHandle = 0L;
	
	public KeyPair(long publicKeyHandle, long privateKeyHandle)
	{
		super();
		
		setPublicKeyHandle(publicKeyHandle);
		setPrivateKeyHandle(privateKeyHandle);
	}
	
	private void setPublicKeyHandle(long publicKeyHandle) 
	{
		this.publicKeyHandle = publicKeyHandle;
	}
	
	public long getPublicKeyHandle() 
	{
		return this.publicKeyHandle;
	}
	
	private void setPrivateKeyHandle(long privateKeyHandle)
	{
		this.privateKeyHandle = privateKeyHandle;
	}

	public long getPrivateKeyHandle()
	{
		return privateKeyHandle;
	}
}
