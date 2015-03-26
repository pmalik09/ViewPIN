// $Id: PinAgent/src/com/safenetinc/viewpin/agent/otp/PinOneTimePadKeyGenerator.java 1.2 2009/02/04 08:22:56IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent.otp;

import java.security.NoSuchAlgorithmException;

import com.safenetinc.viewpin.common.utils.EntropyPool;

/**
 * Class to handle generation of one time pad keys
 * 
 * @author Paul Hampton
 *
 *
 */
public class PinOneTimePadKeyGenerator 
{
	private PinOneTimePadKeyGenerator()
	{
	    super();
	}
	
	/**
	 * Generates a one time pad key
	 * @param length The length of the key to generate
	 * @return The one time pad key
	 * @throws NoSuchAlgorithmException Thrown if a problem occurs during entropy generation
	 */
	@SuppressWarnings("null")
    public static String generateKey(int length) throws NoSuchAlgorithmException
    {
    	StringBuffer padKey;
    	
    	padKey = null;
    	
    	while(true)
    	{
	    	padKey = new StringBuffer(length);
	    	
	    	for(int i = 0; i < length; i++)
	    	{
	    	    padKey.append(generateRandomDigit());	
	    	}
	    	
	    	// Ensure generated key is not equal to zero
	    	if(isKeyZero(padKey.toString()) == false)
	    	{
	    		break;
	    	}
	    }
    	if(padKey != null)
    		return padKey.toString();
    	else
    		return null;
    }
	
	private static boolean isKeyZero(String key)
	{
		boolean keyIsZero;
		int totalZeros;
		
		keyIsZero = false;
		totalZeros = 0;
		
		for(int i = 0; i < key.length(); i++)
		{
			if(key.charAt(i) == '0')
			{
				totalZeros++;
			}
		}
		
		if(totalZeros == key.length())
		{
			keyIsZero = true;
		}
		
		return keyIsZero;
	}
    
	private static int generateRandomDigit() throws NoSuchAlgorithmException
    {
        int randomDigit;
        
        randomDigit = 0;
        
        // Get random byte from entropy pool
        randomDigit = EntropyPool.getByte();
        
        // Ignore high order nibble
        randomDigit &= 0x0000000F;
        
        // Ensure random digit is between zero and nine
        randomDigit %= 10;
        
        return randomDigit;
    }
}