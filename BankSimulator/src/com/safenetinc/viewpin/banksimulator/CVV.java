/*
 * Created on May 2, 2004
 * 
 *  
 */
package com.safenetinc.viewpin.banksimulator;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.safenetinc.viewpin.authority.ExpiryDate;
import com.safenetinc.viewpin.banksimulator.PrimaryAccountNumber;


/**
 * @author Paul Hampton
 * 
 *  
 */
public class CVV
{
    
    Random random = new Random();
    
    /**
     * Creates a new CVV object
     */
    public CVV()
    {
        //Nothing required here
    }
    
    /**
     * Method to calculate the value of a CVV number. This is used by the
     * simulator to show the user the CVV value they should enter at the point
     * where they retrieve their PIN. Designed to simulate the calculation of CVV
     * that would be performed by a real bank. Here we are using a randomly
     * generated 3DES key, a real bank would use a long lived secret key for this
     * purpose.
     * 
     * @param primaryAccountNumber The PAN of the card
     * @param expiryDate The expiry date of the card
     * @param serviceCode The service code for the calculation
     * @param iv The initialisation vector to use in the CVV calculation
     * @return The CVV as an Integer
     * @throws Exception 
     */
    public String calculateCVV(PrimaryAccountNumber primaryAccountNumber, ExpiryDate expiryDate, String serviceCode, String iv) throws Exception
    {
        IvParameterSpec ivps = null;
        Cipher c = null;
        ByteArrayOutputStream baos = null;
        byte[] cipherText = null;
        StringBuffer sb = null;
        byte[] keyBytes = new byte[24];//24 bytes in a 3DES key
        
        //Generate a random DES key. A real bank would use a long-lived key here
        this.random.nextBytes(keyBytes);
        SecretKey key = new SecretKeySpec(keyBytes,"DESede");
        
        //Use the IV we were passed
        ivps = new IvParameterSpec(Hex.decodeHex(iv.toCharArray()));

        //Now Setup the cipher ready for encrypting
        c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key, ivps);

        //Create a byte stream of the values that are used in CVV calculation
        try
        {
            baos = new ByteArrayOutputStream();
            baos.write(primaryAccountNumber.getPrimaryAccountNumber().getBytes());
            baos.write(expiryDate.getFormatted().getBytes());
            baos.write(serviceCode.getBytes());
        }
        finally
        {
            if (baos != null)
            {
                baos.close();
            }
        }

        if(baos ==null)
        {
            throw new Exception("Unable to initialise ByteArrayOutputStream");
        }
        //Encrypt our values
        cipherText = c.doFinal(baos.toByteArray());

        //Perform the modulo arithmetic that calculates the CVV from the cipher text
        sb = new StringBuffer();
        sb.append((cipherText[0] & 0x000000FF) % 10);
        sb.append((cipherText[1] & 0x000000FF) % 10);
        sb.append((cipherText[2] & 0x000000FF) % 10);

        //All done, return the CVV
        return sb.toString();

    }
    
    /**
     * Method to ensure that a String matches the parameters for a CVV
     * namely, that it is three digits
     * @param cardHolderVerificationValue The String to test
     * @return boolean denoting whether or not the string is a valid CVV
     */
    public static boolean validateCVV (String cardHolderVerificationValue)
    {
        //Check for null
        if(cardHolderVerificationValue == null)
            return false;
        //If not null check against the regular expression for three digits
        return cardHolderVerificationValue.matches("\\d\\d\\d");
    }
}