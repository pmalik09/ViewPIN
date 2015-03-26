// $Id: BankSimulator/externalsource/com/safenetinc/viewpin/simulator/authority/UrlSafeBase64.java 1.1 2008/09/04 10:38:33IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

/**
 * Class to produce Base64 that is safe for use on URLs and in cookies. Used to encode values that are placed
 * into cookies.
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class UrlSafeBase64
{
    private static final char PLUS          = '+';

    private static final char DASH          = '-';

    private static final char FORWARD_SLASH = '/';

    private static final char UNDERSCORE    = '_';

    private static final char EQUALS        = '=';

    private static final char PERIOD        = '.';

    private UrlSafeBase64()
    {
        super();
    }

    /**
     * Encodes the supplied byte array
     * 
     * @param unencoded The unencoded byte array
     * @return Safe base64 encoded version of the supplied byte array
     */
    public static String encode (final byte[] unencoded)
    {
        String encoded;

        encoded = null;

        // Encode unencoded value
        encoded = new String(Base64.encodeBase64(unencoded, false));

        // Replace unsafe Base64 characters with safe equivalents
        encoded = encoded.replace(PLUS, DASH);
        encoded = encoded.replace(FORWARD_SLASH, UNDERSCORE);
        encoded = encoded.replace(EQUALS, PERIOD);

        return encoded;
    }

    /**
     * Decodes a safe base64 encoded string
     * 
     * @param encoded The encoded string
     * @return byte array containing the decoded string
     * @throws IOException thrown if an invalid encoded string is supplied
     */
    public static byte[] decode (String encoded) throws IOException
    {
        String encodedReplaced;
        byte[] decoded;

        decoded = null;

        // Ensure encoded value is not null
        if (encoded == null)
        {
            // Encoded value is null
            throw new IOException("is null");
        }

        // Replace safe characters with official Base64 equivalents
        encodedReplaced = encoded.replace(DASH, PLUS);
        encodedReplaced = encodedReplaced.replace(UNDERSCORE, FORWARD_SLASH);
        encodedReplaced = encodedReplaced.replace(PERIOD, EQUALS);

        // Ensure encoded value is valid Base64
        if (Base64.isArrayByteBase64(encodedReplaced.getBytes()) == false)
        {
            // Encoded value is not valid Base64
            throw new IOException("invalid encoding");
        }

        // Decode encoded Base64 value
        decoded = Base64.decodeBase64(encodedReplaced.getBytes());

        return decoded;
    }
}