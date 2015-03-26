// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/SessionCipherProperties.java 1.2 2011/12/20 15:39:31IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import com.safenetinc.viewpin.agent.sessionkey.exceptions.InvalidSessionKeyLengthException;

/**
 * Class to manage the properties of a symmetric encryption operation
 * @author Stuart Horler
 *
 *
 */
public class SessionCipherProperties 
{
	private static final int BLOCK_SIZE_8_BYTES = 8;
	private static final int BLOCK_SIZE_16_BYTES = 16;
    private static final int KEY_LENGTH_DESEDE_168 = 168;
    private static final int KEY_LENGTH_AES_128 = 128;
    private static final int KEY_LENGTH_AES_192 = 192;
    private static final int KEY_LENGTH_AES_256 = 256;
    
	private KeyType keyType = null;
	private int keyLength = 0;
	private int blockSize = 0;
	
	/**
	 * Constructor
	 * @param keyType The key type to store
	 * @param keyLength The length of the session key
	 * @throws InvalidSessionKeyLengthException Thrown if an invalid key length is specified
	 */
	public SessionCipherProperties(KeyType keyType, int keyLength) throws InvalidSessionKeyLengthException 
	{
		super();

		setKeyType(keyType);
		
		if(keyType.getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_DESEDE) == true)
		{
			setBlockSize(BLOCK_SIZE_8_BYTES);
		}
		else
		{
			if(keyType.getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_AES) == true)
			{
				setBlockSize(BLOCK_SIZE_16_BYTES);
			}
		}
		
		setKeyLength(keyLength);
		
		validateKeyLength();
	}
	
	private void validateKeyLength() throws InvalidSessionKeyLengthException
	{
		if(getKeyType().getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_DESEDE) == true)
		{
			if(getKeyLength() != KEY_LENGTH_DESEDE_168)
			{
				throw new InvalidSessionKeyLengthException("DESede key length must be " + KEY_LENGTH_DESEDE_168 + " not " + getKeyLength());
			}
		}
		else
		{
			if(getKeyType().getKeyType().equalsIgnoreCase(KeyType.KEY_TYPE_AES) == true)
			{
				switch(getKeyLength())
				{
				    case KEY_LENGTH_AES_128 :
				    case KEY_LENGTH_AES_192 :
				    case KEY_LENGTH_AES_256 :
				    	
				    	break;
				    	
				    default :
				    	
				    	throw new InvalidSessionKeyLengthException("AES session key length must be either " + KEY_LENGTH_AES_128 +
				            " or " + KEY_LENGTH_AES_192 + " or " + KEY_LENGTH_AES_256 + " not " + getKeyLength());
				}
			}
		}
	}

	private void setKeyType(KeyType keyType)
	{
		this.keyType = keyType;
	}

	/**
	 * @return KeyType
	 */
	public KeyType getKeyType()
	{
		return this.keyType;
	}

	private void setKeyLength(int keyLength)
	{
		this.keyLength = keyLength;
	}

	/**
	 * @return Key length
	 */
	public int getKeyLength()
	{
		return this.keyLength;
	}

	private void setBlockSize(int blockSize)
    {
    	this.blockSize = blockSize;
    }

    /**
     * @return Block size
     */
    public int getBlockSize()
    {
    	return this.blockSize;
    }
}