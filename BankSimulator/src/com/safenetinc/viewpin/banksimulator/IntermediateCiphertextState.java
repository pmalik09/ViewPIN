package com.safenetinc.viewpin.banksimulator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.safenetinc.viewpin.banksimulator.Utils;

public class IntermediateCiphertextState
{
	private String cipherProviderName = null;
		
	private byte[] currentCiphertextState = null;
	

	public IntermediateCiphertextState(String cipherProviderName)
	{
		super();
		
		setCipherProviderName(cipherProviderName);
	}
	
	public void encrypt(byte[] data, SecretKey key) throws NoSuchAlgorithmException,
	    NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
	    IllegalBlockSizeException, BadPaddingException
	{
		Cipher c = Cipher.getInstance("DES/ECB/NoPadding", getCipherProviderName());
        c.init(Cipher.ENCRYPT_MODE, key);
        
        this.currentCiphertextState = c.doFinal(data); 
	}
	
	public void encrypt(SecretKey key) throws NoSuchAlgorithmException,
	    NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
	    IllegalBlockSizeException, BadPaddingException
	{
		Cipher c = Cipher.getInstance("DES/ECB/NoPadding", getCipherProviderName());
	    c.init(Cipher.ENCRYPT_MODE, key);
	    
	    this.currentCiphertextState = c.doFinal(this.currentCiphertextState); 
	}
	
	public void decrypt(SecretKey key) throws NoSuchAlgorithmException,
	    NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
	    IllegalBlockSizeException, BadPaddingException
	{
		Cipher c = Cipher.getInstance("DES/ECB/NoPadding", getCipherProviderName());
	    c.init(Cipher.DECRYPT_MODE, key);
	    
	    this.currentCiphertextState = c.doFinal(this.currentCiphertextState);
	}
	
	public void decrypt(byte[] data, SecretKey key) throws NoSuchAlgorithmException,
        NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
        IllegalBlockSizeException, BadPaddingException
	{
		Cipher c = Cipher.getInstance("DES/ECB/NoPadding", getCipherProviderName());
        c.init(Cipher.DECRYPT_MODE, key);
        
        this.currentCiphertextState = c.doFinal(data);
	}
	
	public void xorThenEncrypt(byte[] data, SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
	{
		this.currentCiphertextState = Utils.xor(this.currentCiphertextState, data);
		
		encrypt(this.currentCiphertextState, key);
	}
	
	private void setCipherProviderName(String cipherProviderName) 
	{
		this.cipherProviderName = cipherProviderName;
	}
	
	public String getCipherProviderName()
	{
		return this.cipherProviderName;
	}
	
	public byte[] getCiphertextState()
	{
		return this.currentCiphertextState;
	}
}
