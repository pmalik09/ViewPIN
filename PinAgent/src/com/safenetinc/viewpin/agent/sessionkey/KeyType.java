// $Id: PinAgent/src/com/safenetinc/viewpin/agent/sessionkey/KeyType.java 1.1 2008/09/04 10:46:27IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.sessionkey;

import com.safenetinc.viewpin.agent.sessionkey.exceptions.UnsupportedKeyTypeException;

/**
 * Class to represent an encyption key type
 * 
 * @author Stuart Horler
 *
 *
 */
public class KeyType
{
    /**
     * The type for Triple DES (EDE)
     */
    public static final String KEY_TYPE_DESEDE = "DESede";
    /**
     * The type for AES
     */
    public static final String KEY_TYPE_AES = "AES";
    
    private String keyType = null;

    private KeyType(String keyType)
    {
        super();

        setKeyType(keyType);
    }

    /**
     * Get an instance of this class representing a particular key type
     * @param keyType The type of key to represent
     * @return a new instance of this class representing the keyType specified
     * @throws UnsupportedKeyTypeException Thrown if an invalid key type was specified
     */
    public static KeyType getInstance(String keyType) throws UnsupportedKeyTypeException
    {
        KeyType kt;

        kt = null;

       	if(keyType.equalsIgnoreCase(KEY_TYPE_DESEDE) == true)
        {
            kt = new KeyType(KEY_TYPE_DESEDE);
        }   	
    	else
    	{
    		if(keyType.equalsIgnoreCase(KEY_TYPE_AES) == true)
            {
                kt = new KeyType(KEY_TYPE_AES);
            }
    		else
    		{
    			throw new UnsupportedKeyTypeException();		
    		}
    	}

        return kt;
    }

    private void setKeyType(String keyType)
    {
        this.keyType = keyType;
    }

    /**
     * @return The key type represented by this class
     */
    public String getKeyType()
    {
        return this.keyType;
    }
}