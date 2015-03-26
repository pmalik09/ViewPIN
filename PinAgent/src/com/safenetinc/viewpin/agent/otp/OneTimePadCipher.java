// $Id: PinAgent/src/com/safenetinc/viewpin/agent/otp/OneTimePadCipher.java 1.1 2008/09/04 10:46:16IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.otp;

import java.security.InvalidKeyException;

/**
 * Class to handle one time pad encryption
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class OneTimePadCipher
{
    private OneTimePadCipher()
    {
        super();
    }

    /**
     * Encrypts a String with a OTP
     * 
     * @param clearText The String to encrypt
     * @param padKey The OTP to use
     * @return The encrypted String
     * @throws InvalidKeyException Thrown if the length of the key doesn't match the length of the String to
     *         encrypt.
     */
    public static String encrypt (String clearText, String padKey) throws InvalidKeyException
    {
        StringBuffer cipherText;
        int nextClearTextCharacter;
        int nextPadKeyCharacter;
        int nextCipherTextCharacter;

        cipherText = null;
        nextClearTextCharacter = 0;
        nextPadKeyCharacter = 0;
        nextCipherTextCharacter = 0;

        // Ensure key length is the same length as clear text
        if (clearText.length() != padKey.length())
        {
            throw new InvalidKeyException("key length must equal clear text length");
        }

        cipherText = new StringBuffer(clearText.length());

        for (int i = 0; i < clearText.length(); i++)
        {
            nextClearTextCharacter = Integer.parseInt(clearText.charAt(i) + "");

            nextPadKeyCharacter = Integer.parseInt(padKey.charAt(i) + "");

            nextCipherTextCharacter = nextClearTextCharacter ^ nextPadKeyCharacter;

            cipherText.append(toHexCharacter(nextCipherTextCharacter));
        }

        return cipherText.toString();
    }

    private static char toHexCharacter (int nibble)
    {
        char c;

        c = '\0';

        switch (nibble)
        {
            case 0:

                c = '0';

                break;

            case 1:

                c = '1';

                break;

            case 2:

                c = '2';

                break;

            case 3:

                c = '3';

                break;

            case 4:

                c = '4';

                break;

            case 5:

                c = '5';

                break;

            case 6:

                c = '6';

                break;

            case 7:

                c = '7';

                break;

            case 8:

                c = '8';

                break;

            case 9:

                c = '9';

                break;

            case 10:

                c = 'a';

                break;

            case 11:

                c = 'b';

                break;

            case 12:

                c = 'c';

                break;

            case 13:

                c = 'd';

                break;

            case 14:

                c = 'e';

                break;

            case 15:

                c = 'f';

                break;

            default:

                break;

        }

        return c;
    }
}