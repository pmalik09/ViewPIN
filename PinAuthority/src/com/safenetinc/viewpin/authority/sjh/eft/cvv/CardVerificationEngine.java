package com.safenetinc.viewpin.authority.sjh.eft.cvv;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.safenetinc.viewpin.authority.ExpiryDate;
import com.safenetinc.viewpin.authority.PrimaryAccountNumber;
import com.safenetinc.viewpin.authority.sjh.common.NibbleBuffer;

public class CardVerificationEngine 
{
	private static Logger logger = Logger.getLogger(CardVerificationEngine.class);
	
	private String cipherProviderName = null;
	
    public CardVerificationEngine(String cipherProviderName)
    {
    	super();
    	
    	setCipherProviderName(cipherProviderName);
    }
    
    public boolean verifyCardVerificationValue(CardVerificationValue cardVerificationValue, CardVerificationKeyPair cardVerificationKeyPair,
    	PrimaryAccountNumber primaryAccountNumber, ExpiryDate expiryDate, ServiceCode serviceCode) throws NoSuchAlgorithmException,
    	NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
		
    	boolean cardVerificationValueValid = false;
    	
    	NibbleBuffer nb = new NibbleBuffer(16); // 128 zero bits
    	
    	// Append primary account number
    	nb.append(primaryAccountNumber.getPrimaryAccountNumber());
    	
    	// Append expiry date
    	String formattedExpiryDate = expiryDate.getFormatted(); 
        String expiryDateMonth = formattedExpiryDate.substring(0, 2);
        String expiryDateYear = formattedExpiryDate.substring(3, 5);
        nb.append(expiryDateMonth);
        nb.append(expiryDateYear);
        
        // Append service code
        nb.append(serviceCode.getServiceCode()); // Service code
        
    //    getLogger().debug(new String(Hex.encodeHex(nb.getBuffer())));
    	
        byte[] panExpiryDateServiceCode = nb.getBuffer();

        // Split buffer containing primary account number, expiry date service code and zero padding into two equal blocks
        byte[] blockOne = new byte[8];
        byte[] blockTwo = new byte[8];
        System.arraycopy(panExpiryDateServiceCode, 0, blockOne, 0, 8);
        System.arraycopy(panExpiryDateServiceCode, 8, blockTwo, 0, 8);

        
        IntermediateCiphertextState ics = new IntermediateCiphertextState(getCipherProviderName());
        
        // Encrypt block one with CVK A
        ics.encrypt(blockOne, cardVerificationKeyPair.getKeyA());
        
        // xor block 2 then encrypt with CVK A
        ics.xorThenEncrypt(blockTwo, cardVerificationKeyPair.getKeyA());
        
        // Decrypt with CVK B
        ics.decrypt(cardVerificationKeyPair.getKeyB());
        
        // Encrypt with CVK A
        ics.encrypt(cardVerificationKeyPair.getKeyA());
        
        byte[] finalCiphertextState = ics.getCiphertextState();
        
        NibbleBuffer finalCiphertextStateNibbleBuffer = new NibbleBuffer(finalCiphertextState);
        
        NibbleBuffer lessThanTen = new NibbleBuffer(8);
        NibbleBuffer greaterThanNine = new NibbleBuffer(8);
        
        // Split numbers into two groups, under 10 and over 9
        for(int i = 0; i < finalCiphertextStateNibbleBuffer.getTotalNibbles(); i++)
        {
        	byte nextNibble = finalCiphertextStateNibbleBuffer.getNextNibble();
        	
        	if(nextNibble < 10)
        	{
        		lessThanTen.appendNibble(nextNibble);
        	}
        	else
        	{
        		greaterThanNine.appendNibble(nextNibble);
        	}
        }
        
        // Buffer to hold sorted nibbles
        NibbleBuffer numbersThenHex = new NibbleBuffer(8);
        
        //getLogger().debug("lessThanTen.getTotalNibbles() = " + lessThanTen.getTotalNibbles());
        
        lessThanTen.reset();
        
        // Concatenate both groups
        for(int i = 0; i < lessThanTen.getTotalNibbles(); i++)
        {
        	byte nextNumberNibble = lessThanTen.getNextNibble();
        	
        	numbersThenHex.appendNibble(nextNumberNibble);
        }
        
        //getLogger().debug("greaterThanNine.getTotalNibbles() = " + greaterThanNine.getTotalNibbles());
        
        greaterThanNine.reset();
        
        for(int i = 0; i < greaterThanNine.getTotalNibbles(); i++)
        {
        	byte nextHexNibble = greaterThanNine.getNextNibble();
        	
        	nextHexNibble -= 10;
        	
        	numbersThenHex.appendNibble(nextHexNibble);
        }
        
        //getLogger().debug("total nibbles less than 10 = " + lessThanTen.getTotalNibbles());
        //getLogger().debug("total nibbles greater than 9 = " + greaterThanNine.getTotalNibbles());
        
        //getLogger().debug(new String(Hex.encodeHex(lessThanTen.getBuffer())));
        //getLogger().debug(new String(Hex.encodeHex(greaterThanNine.getBuffer())));
        //getLogger().debug(new String(Hex.encodeHex(numbersThenHex.getBuffer())));
        
        numbersThenHex.reset();
        
        byte nextCardVerificationValueDigit = 0;
        
        StringBuffer sb = new StringBuffer();
        
        // Form recalculated CVV from first three numbers of sorted numbers
        for(int i = 0; i < 3; i++)
        {
        	nextCardVerificationValueDigit = numbersThenHex.getNextNibble();
        	
        	nextCardVerificationValueDigit += 0x30;
        	
        	sb.append((char)nextCardVerificationValueDigit);
        }
        
        String recalculatedCardVerificationValue = sb.toString();
        
        //getLogger().debug("cardholder submitted CVV = " + cardVerificationValue.getCardVerificationValue());
        //getLogger().debug("recalculated CVV = " + recalculatedCardVerificationValue);
        
        // Verify if CVV passed in matches recalculated value
        if(cardVerificationValue.getCardVerificationValue().equalsIgnoreCase(recalculatedCardVerificationValue) == true)
        {
        	// Recalculated CVV matches CVV passed in by card holder
        	cardVerificationValueValid = true;
        }
        else
        {
        	// Recalculated CVV does not match CVV passed in by card holder
        	cardVerificationValueValid = false;
        }
       
    	return cardVerificationValueValid;
    }
    
	private void setCipherProviderName(String cipherProviderName) 
	{
		this.cipherProviderName = cipherProviderName;
	}
	
	public String getCipherProviderName()
	{
		return this.cipherProviderName;
	}
	
    private Logger getLogger ()
	{
	    return logger;
	}
}
